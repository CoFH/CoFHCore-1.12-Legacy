package cofh.core.world.feature;

import cofh.api.world.IFeatureGenerator;
import cofh.api.world.IFeatureParser;
import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.WeightedRandomNBTTag;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.world.WorldGenAdvLakes;
import cofh.lib.world.WorldGenBoulder;
import cofh.lib.world.WorldGenDecoration;
import cofh.lib.world.WorldGenDungeon;
import cofh.lib.world.WorldGenGeode;
import cofh.lib.world.WorldGenMinableCluster;
import cofh.lib.world.WorldGenMinableLargeVein;
import cofh.lib.world.WorldGenSparseMinableCluster;
import cofh.lib.world.WorldGenSpike;
import cofh.lib.world.WorldGenStalactite;
import cofh.lib.world.WorldGenStalagmite;
import cofh.lib.world.feature.FeatureBase;
import cofh.lib.world.feature.FeatureBase.GenRestriction;
import cofh.lib.world.feature.FeatureGenUniform;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.DungeonHooks.DungeonMob;

import org.apache.logging.log4j.Logger;

public class UniformParser implements IFeatureParser {

	private final List<WeightedRandomBlock> defaultMaterial;

	public UniformParser() {

		defaultMaterial = generateDefaultMaterial();
	}

	protected List<WeightedRandomBlock> generateDefaultMaterial() {

		return Arrays.asList(new WeightedRandomBlock(new ItemStack(Blocks.stone, 1, -1)));
	}

	@Override
	public IFeatureGenerator parseFeature(String featureName, JsonObject genObject, Logger log) {

		List<WeightedRandomBlock> resList = new ArrayList<WeightedRandomBlock>();

		if (!FeatureParser.parseResList(genObject.get("block"), resList)) {
			return null;
		}
		int clusterSize = 0;
		int numClusters = 0;
		boolean retrogen = false;
		GenRestriction biomeRes = GenRestriction.NONE;
		GenRestriction dimRes = GenRestriction.NONE;

		if (genObject.has("clusterSize")) {
			clusterSize = genObject.get("clusterSize").getAsInt();
		}
		if (genObject.has("numClusters")) {
			numClusters = genObject.get("numClusters").getAsInt();
		}
		if (clusterSize < 0 || numClusters <= 0) {
			log.error("Invalid cluster size or count specified in \"" + featureName + "\"");
			return null;
		}
		if (genObject.has("retrogen")) {
			retrogen = genObject.get("retrogen").getAsBoolean();
		}
		if (genObject.has("biomeRestriction")) {
			String resString = genObject.get("biomeRestriction").getAsString().toLowerCase();

			if (resString.equals("blacklist")) {
				biomeRes = GenRestriction.BLACKLIST;
			}
			if (resString.equals("whitelist")) {
				biomeRes = GenRestriction.WHITELIST;
			}
		}
		if (genObject.has("dimensionRestriction")) {
			String resString = genObject.get("dimensionRestriction").getAsString().toLowerCase();

			if (resString.equals("blacklist")) {
				dimRes = GenRestriction.BLACKLIST;
			}
			if (resString.equals("whitelist")) {
				dimRes = GenRestriction.WHITELIST;
			}
		}
		List<WeightedRandomBlock> matList = parseMaterial(genObject, log);

		FeatureBase feature = getFeature(featureName, genObject, getGenerator(genObject, log, resList, clusterSize, matList),
				matList, numClusters, biomeRes, retrogen, dimRes, log);

		if (genObject.has("chunkChance")) {
			int rarity = MathHelper.clampI(genObject.get("chunkChance").getAsInt(), 1, 1000000);
			feature.setRarity(rarity);
		}
		if (feature != null)
			addFeatureRestrictions(feature, genObject);
		return feature;
	}

	protected FeatureBase getFeature(String featureName, JsonObject genObject, WorldGenerator gen, List<WeightedRandomBlock> matList, int numClusters, GenRestriction biomeRes, boolean retrogen,
			GenRestriction dimRes, Logger log) {

		int minHeight = genObject.get("minHeight").getAsInt();
		int maxHeight = genObject.get("maxHeight").getAsInt();

		if (minHeight >= maxHeight || minHeight < 0) {
			log.error("Invalid height parameters specified in \"" + featureName + "\"");
			return null;
		}

		return new FeatureGenUniform(featureName, gen, numClusters, minHeight, maxHeight, biomeRes, retrogen, dimRes);
	}

	protected String getDefaultTemplate() {
		return "cluster";
	}

	protected List<WeightedRandomBlock> parseMaterial(JsonObject genObject, Logger log) {

		List<WeightedRandomBlock> matList = defaultMaterial;
		if (genObject.has("material")) {
			matList = new ArrayList<WeightedRandomBlock>();
			if (!FeatureParser.parseResList(genObject.get("material"), matList)) {
				log.warn("Invalid material list! Using default list.");
				matList = defaultMaterial;
			}
		}
		return matList;
	}

	protected WorldGenerator getGenerator(JsonObject genObject, Logger log, List<WeightedRandomBlock> resList, int clusterSize,
			List<WeightedRandomBlock> matList) {

		String template = getDefaultTemplate();

		JsonObject entry = genObject;
		JsonElement genElement = genObject.get("template");
		if (genElement.isJsonObject()) {
			genObject = genElement.getAsJsonObject();

			if (genObject.has("generator")) {
				template = genObject.get("generator").getAsString();
			}
		}

		if ("sparse-cluster".equals(template)) {
			return new WorldGenSparseMinableCluster(resList, clusterSize, matList);
		} else if ("large-vein".equals(template)) {
			boolean sparse = true;
			{
				sparse = genObject.has("sparse") ? genObject.get("sparse").getAsBoolean() : sparse;
			}
			return new WorldGenMinableLargeVein(resList, clusterSize, matList, sparse);
		} else if ("lake".equals(template)) {
			boolean useMaterial = false;
			{
				useMaterial = genObject.has("useMaterial") ? genObject.get("useMaterial").getAsBoolean() : useMaterial;
			}
			WorldGenAdvLakes r = new WorldGenAdvLakes(resList, useMaterial ? matList : null);
			{
				if (genObject.has("outlineWithStone"))
					r.outlineBlock = genObject.get("outlineWithStone").getAsBoolean() ?
							new WeightedRandomBlock(Blocks.stone, 0) : null;
							if (genObject.has("outlineBlock"))
								r.outlineBlock = FeatureParser.parseBlockEntry(genObject.get("outlineBlock"));
							if (genObject.has("gapBlock"))
								r.gapBlock = FeatureParser.parseBlockEntry(genObject.get("gapBlock"));
							if (genObject.has("lineWithFiller"))
								r.lineWithFiller = genObject.get("lineWithFiller").getAsBoolean();
			}
			return r;
		} else if ("geode".equals(template)) {
			ArrayList<WeightedRandomBlock> list = new ArrayList<WeightedRandomBlock>();
			if (!entry.has("crust")) {
				log.info("Entry does not specify crust for 'geode' generator. Using stone.");
				list.add(new WeightedRandomBlock(Blocks.stone));
			} else {
				if (!FeatureParser.parseResList(entry.get("crust"), list)) {
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
					if (!FeatureParser.parseResList(genObject.get("filler"), list)) {
						log.warn("Entry specifies invalid filler for 'geode' generator! Not filling!");
					} else {
						r.fillBlock = list;
					}
				}
			}
			return r;
		} else if ("decoration".equals(template)) {
			ArrayList<WeightedRandomBlock> list = new ArrayList<WeightedRandomBlock>();
			if (!entry.has("genSurface")) {
				log.info("Entry does not specify genSurface for 'decoration' generator. Using grass.");
				list.add(new WeightedRandomBlock(Blocks.grass));
			} else {
				if (!FeatureParser.parseResList(entry.get("genSurface"), list)) {
					log.warn("Entry specifies invalid genSurface for 'decoration' generator! Using grass!");
					list.clear();
					list.add(new WeightedRandomBlock(Blocks.grass));
				}
			}
			WorldGenDecoration r = new WorldGenDecoration(resList, clusterSize, matList, list);
			if (genObject.has("genSky"))
				r.seeSky = genObject.get("genSky").getAsBoolean();
			if (genObject.has("checkStay"))
				r.checkStay = genObject.get("checkStay").getAsBoolean();
			if (genObject.has("stackHeight"))
				r.stackHeight = genObject.get("stackHeight").getAsInt();
			if (genObject.has("xVariance"))
				r.stackHeight = genObject.get("xVariance").getAsInt();
			if (genObject.has("yVariance"))
				r.stackHeight = genObject.get("yVariance").getAsInt();
			if (genObject.has("zVariance"))
				r.stackHeight = genObject.get("zVariance").getAsInt();
			return r;
		} else if ("boulder".equals(template)) {
			WorldGenBoulder r = new WorldGenBoulder(resList, clusterSize, matList);
			{
				if (genObject.has("sizeVariance"))
					r.sizeVariance = genObject.get("sizeVariance").getAsInt();
				if (genObject.has("count"))
					r.clusters = genObject.get("count").getAsInt();
			}
			return r;
		} else if ("spike".equals(template)) {
			WorldGenSpike r = new WorldGenSpike(resList, matList);
			{
				if (genObject.has("largeSpikes"))
					r.largeSpikes = genObject.get("largeSpikes").getAsBoolean();
				if (genObject.has("largeSpikeChance"))
					r.largeSpikeChance = genObject.get("largeSpikeChance").getAsInt();
				if (genObject.has("minHeight"))
					r.minHeight = genObject.get("minHeight").getAsInt();
				if (genObject.has("heightVariance"))
					r.heightVariance = genObject.get("heightVariance").getAsInt();
				if (genObject.has("sizeVariance"))
					r.sizeVariance = genObject.get("sizeVariance").getAsInt();
				if (genObject.has("positionVariance"))
					r.positionVariance = genObject.get("positionVariance").getAsInt();
				if (genObject.has("minLargeSpikeHeightGain"))
					r.minLargeSpikeHeightGain = genObject.get("minLargeSpikeHeightGain").getAsInt();
				if (genObject.has("largeSpikeHeightVariance"))
					r.largeSpikeHeightVariance = genObject.get("largeSpikeHeightVariance").getAsInt();
				if (genObject.has("largeSpikeFillerSize"))
					r.largeSpikeFillerSize = genObject.get("largeSpikeFillerSize").getAsInt();
			}
			return r;
		} else if ("stalagmite".equals(template)) {
			ArrayList<WeightedRandomBlock> list = new ArrayList<WeightedRandomBlock>();
			if (!entry.has("genBody")) {
				log.info("Entry does not specify genBody for 'stalagmite' generator. Using air.");
				list.add(new WeightedRandomBlock(Blocks.air));
			} else {
				if (!FeatureParser.parseResList(entry.get("genBody"), list)) {
					log.warn("Entry specifies invalid genBody for 'stalagmite' generator! Using air!");
					list.clear();
					list.add(new WeightedRandomBlock(Blocks.air));
				}
			}
			WorldGenStalagmite r = new WorldGenStalagmite(resList, matList, list);
			{
				if (genObject.has("minHeight"))
					r.minHeight = genObject.get("minHeight").getAsInt();
				if (genObject.has("heightVariance"))
					r.heightVariance = genObject.get("heightVariance").getAsInt();
				if (genObject.has("sizeVariance"))
					r.sizeVariance = genObject.get("sizeVariance").getAsInt();
				if (genObject.has("heightMod"))
					r.heightMod = genObject.get("heightMod").getAsInt();
				if (genObject.has("genSize"))
					r.genSize = genObject.get("genSize").getAsInt();
				if (genObject.has("smooth"))
					r.smooth = genObject.get("smooth").getAsBoolean();
				if (genObject.has("fat"))
					r.fat = genObject.get("fat").getAsBoolean();
				if (genObject.has("altSinc"))
					r.altSinc = genObject.get("altSinc").getAsBoolean();
			}
			return r;
		} else if ("stalactite".equals(template)) {
			ArrayList<WeightedRandomBlock> list = new ArrayList<WeightedRandomBlock>();
			if (!entry.has("genBody")) {
				log.info("Entry does not specify genBody for 'stalactite' generator. Using air.");
				list.add(new WeightedRandomBlock(Blocks.air));
			} else {
				if (!FeatureParser.parseResList(entry.get("genBody"), list)) {
					log.warn("Entry specifies invalid genBody for 'stalactite' generator! Using air!");
					list.clear();
					list.add(new WeightedRandomBlock(Blocks.air));
				}
			}
			WorldGenStalactite r = new WorldGenStalactite(resList, matList, list);
			{
				if (genObject.has("minHeight"))
					r.minHeight = genObject.get("minHeight").getAsInt();
				if (genObject.has("heightVariance"))
					r.heightVariance = genObject.get("heightVariance").getAsInt();
				if (genObject.has("sizeVariance"))
					r.sizeVariance = genObject.get("sizeVariance").getAsInt();
				if (genObject.has("heightMod"))
					r.heightMod = genObject.get("heightMod").getAsInt();
				if (genObject.has("genSize"))
					r.genSize = genObject.get("genSize").getAsInt();
				if (genObject.has("smooth"))
					r.smooth = genObject.get("smooth").getAsBoolean();
				if (genObject.has("fat"))
					r.fat = genObject.get("fat").getAsBoolean();
				if (genObject.has("altSinc"))
					r.altSinc = genObject.get("altSinc").getAsBoolean();
			}
			return r;
		} else if ("dungeon".equals(template)) {
			ArrayList<WeightedRandomNBTTag> mobList = new ArrayList<WeightedRandomNBTTag>();
			if (entry.has("spawnEntity")) {
				if (!FeatureParser.parseEntityList(entry.get("spawnEntity"), mobList)) {
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
			if (entry.has("spawnerFloor")) {
				resList = new ArrayList<WeightedRandomBlock>();
				if (FeatureParser.parseResList(entry.get("spawnerFloor"), resList)) {
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
				if (genObject.has("maxChests"))
					r.maxChests = genObject.get("maxChests").getAsInt();

				if (genObject.has("minHoles"))
					r.minHoles = genObject.get("minHoles").getAsInt();
				if (genObject.has("maxHoles"))
					r.maxHoles = genObject.get("maxHoles").getAsInt();

				if (genObject.has("minHeight"))
					r.minHeight = genObject.get("minHeight").getAsInt();
				if (genObject.has("maxHeight"))
					r.maxHeight = genObject.get("maxHeight").getAsInt();

				if (genObject.has("minWidthX"))
					r.minWidthX = genObject.get("minWidthX").getAsInt();
				if (genObject.has("maxWidthX"))
					r.maxWidthX = genObject.get("maxWidthX").getAsInt();
				if (genObject.has("minWidthZ"))
					r.minWidthZ = genObject.get("minWidthZ").getAsInt();
				if (genObject.has("maxWidthZ"))
					r.maxWidthZ = genObject.get("maxWidthZ").getAsInt();
			}
			return r;
		}

		if (!"cluster".equals(template)) {
			log.warn("Unknown generator " + template + "! Using 'cluster'");
		}
		return new WorldGenMinableCluster(resList, clusterSize, matList);
	}

	protected static boolean addFeatureRestrictions(FeatureBase feature, JsonObject genObject) {

		if (feature.biomeRestriction != GenRestriction.NONE) {
			feature.addBiomes(FeatureParser.parseBiomeRestrictions(genObject));
		}
		if (feature.dimensionRestriction != GenRestriction.NONE && genObject.has("dimensions")) {
			JsonArray restrictionList = genObject.getAsJsonArray("dimensions");
			for (int i = 0; i < restrictionList.size(); i++) {
				feature.addDimension(restrictionList.get(i).getAsInt());
			}
		}
		return true;
	}

}
