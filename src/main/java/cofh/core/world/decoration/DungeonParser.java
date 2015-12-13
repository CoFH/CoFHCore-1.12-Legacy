package cofh.core.world.decoration;

import cofh.api.world.IGeneratorParser;
import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.WeightedRandomNBTTag;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.world.WorldGenDungeon;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.DungeonHooks.DungeonMob;

import org.apache.logging.log4j.Logger;

public class DungeonParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String generatorName, JsonObject genObject, Logger log, List<WeightedRandomBlock> resList, int clusterSize,
			List<WeightedRandomBlock> matList) {

		ArrayList<WeightedRandomNBTTag> mobList = new ArrayList<WeightedRandomNBTTag>();
		if (genObject.has("spawnEntity")) {
			if (!FeatureParser.parseEntityList(genObject.get("spawnEntity"), mobList)) {
				log.warn("Entry specifies invalid entity list for 'dungeon' generator! Using 'Pig'!");
				mobList.clear();
				NBTTagCompound tag = new NBTTagCompound();
				tag.setString("EntityId", "Pig");
				mobList.add(new WeightedRandomNBTTag(100, tag));
			}
		} else {
			log.warn("Entry specifies invalid entity list for 'dungeon' generator! Using 'Pig'!");
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("EntityId", "Pig");
			mobList.add(new WeightedRandomNBTTag(100, tag));
		}
		WorldGenDungeon r = new WorldGenDungeon(resList, matList, mobList);
		if (genObject.has("spawnerFloor")) {
			resList = new ArrayList<WeightedRandomBlock>();
			if (FeatureParser.parseResList(genObject.get("spawnerFloor"), resList, true)) {
				r.floor = resList;
			} else {
				log.warn("Entry specifies invalid block list for 'spawnerFloor'! Using walls.");
			}
		}
		{
			if (genObject.has("lootTable")) {
				ArrayList<DungeonMob> lootList = new ArrayList<DungeonMob>();
				if (FeatureParser.parseWeightedStringList(genObject.get("lootTable"), lootList)) {
					r.lootTables = lootList;
				} else {
					log.warn("Entry specifies invalid string list for 'lootTable'! Using default.");
				}
			}
			if (genObject.has("maxChests")) {
				r.maxChests = genObject.get("maxChests").getAsInt();
			}
			if (genObject.has("chestAttempts")) {
				r.maxChestTries = MathHelper.clamp(genObject.get("chestAttempts").getAsInt(), 1, 5);
			}

			if (genObject.has("minHoles")) {
				r.minHoles = genObject.get("minHoles").getAsInt();
			}
			if (genObject.has("maxHoles")) {
				r.maxHoles = genObject.get("maxHoles").getAsInt();
			}

			if (genObject.has("minHeight")) {
				r.minHeight = genObject.get("minHeight").getAsInt();
			}
			if (genObject.has("maxHeight")) {
				r.maxHeight = genObject.get("maxHeight").getAsInt();
			}

			if (genObject.has("minWidthX")) {
				r.minWidthX = genObject.get("minWidthX").getAsInt();
			}
			if (genObject.has("maxWidthX")) {
				r.maxWidthX = genObject.get("maxWidthX").getAsInt();
			}
			if (genObject.has("minWidthZ")) {
				r.minWidthZ = genObject.get("minWidthZ").getAsInt();
			}
			if (genObject.has("maxWidthZ")) {
				r.maxWidthZ = genObject.get("maxWidthZ").getAsInt();
			}
		}
		return r;
	}

}
