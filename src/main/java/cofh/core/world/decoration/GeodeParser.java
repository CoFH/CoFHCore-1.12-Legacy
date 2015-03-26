package cofh.core.world.decoration;

import cofh.api.world.IGeneratorParser;
import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenGeode;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenerator;

import org.apache.logging.log4j.Logger;

public class GeodeParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String generatorName, JsonObject genObject, Logger log, List<WeightedRandomBlock> resList, int clusterSize,
			List<WeightedRandomBlock> matList) {

		ArrayList<WeightedRandomBlock> list = new ArrayList<WeightedRandomBlock>();
		if (!genObject.has("crust")) {
			log.info("Entry does not specify crust for 'geode' generator. Using stone.");
			list.add(new WeightedRandomBlock(Blocks.stone));
		} else {
			if (!FeatureParser.parseResList(genObject.get("crust"), list, true)) {
				log.warn("Entry specifies invalid crust for 'geode' generator! Using obsidian!");
				list.clear();
				list.add(new WeightedRandomBlock(Blocks.obsidian));
			}
		}
		WorldGenGeode r = new WorldGenGeode(resList, matList, list);
		{
			if (genObject.has("hollow")) {
				r.hollow = genObject.get("hollow").getAsBoolean();
			}
			if (genObject.has("filler")) {
				list = new ArrayList<WeightedRandomBlock>();
				if (!FeatureParser.parseResList(genObject.get("filler"), list, true)) {
					log.warn("Entry specifies invalid filler for 'geode' generator! Not filling!");
				} else {
					r.fillBlock = list;
				}
			}
		}
		return r;
	}

}
