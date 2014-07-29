package cofh.world;

import cofh.CoFHCore;
import cofh.core.CoFHProps;
import cofh.util.CoreUtils;
import cofh.util.MathHelper;
import cofh.util.WeightedRandomBlock;
import cofh.world.feature.FeatureBase;
import cofh.world.feature.FeatureBase.GenRestriction;
import cofh.world.feature.FeatureOreGenNormal;
import cofh.world.feature.FeatureOreGenUniform;
import cofh.world.feature.WorldGenMinableCluster;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cpw.mods.fml.common.registry.GameRegistry;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class FeatureParser {

	private static File worldGenFolder;
	private static File vanillaGen;
	private static final String vanillaGenInternal = "assets/cofh/world/Vanilla.json";

	private static File[] worldGenList;

	private FeatureParser() {

	}

	public static void initialize() {

		worldGenFolder = new File(CoFHProps.configDir, "/cofh/world/");

		if (!worldGenFolder.exists()) {
			try {
				worldGenFolder.mkdir();
			} catch (Throwable t) {
				// pokemon!
			}
		}
		vanillaGen = new File(CoFHProps.configDir, "/cofh/world/Vanilla.json");

		try {
			if (vanillaGen.createNewFile()) {
				CoreUtils.copyFileUsingStream(vanillaGenInternal, vanillaGen);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static void parseGenerationFile() {

		JsonParser parser = new JsonParser();
		JsonObject genList;

		worldGenList = worldGenFolder.listFiles();

		if (worldGenList == null) {
			CoFHCore.log.error("There are no World Generation files present in the configuration directory.");
			return;
		}
		for (File genFile : worldGenList) {
			try {
				if (!WorldHandler.genReplaceVanilla && genFile.equals(vanillaGen)) {
					continue;
				}
				genList = (JsonObject) parser.parse(new FileReader(genFile));
			} catch (Throwable t) {
				CoFHCore.log.error("Critical error reading from a world generation file: " + genFile + " > Please be sure the file is correct!");
				t.printStackTrace();
				continue;
			}
			CoFHCore.log.info("Reading world generation info from: " + genFile + ":");
			for (Entry<String, JsonElement> genEntry : genList.entrySet()) {
				if (parseGenerationEntry(genEntry.getKey(), genEntry.getValue())) {
					CoFHCore.log.info("Generation entry successfully parsed: \"" + genEntry.getKey() + "\"");
				} else {
					CoFHCore.log.error("Error parsing generation entry: \"" + genEntry.getKey() + "\" > Please check the parameters.");
				}
			}
		}
	}

	public static boolean parseGenerationEntry(String featureName, JsonElement genEntry) {

		JsonObject genObject = genEntry.getAsJsonObject();
		String template = genObject.get("template").getAsString().toLowerCase();

		if (template.equals("uniform")) {
			return addUniformTemplate(featureName, genObject);
		} else if (template.equals("normal")) {
			return addNormalTemplate(featureName, genObject);
		}
		return false;
	}

	private static boolean addUniformTemplate(String featureName, JsonObject genObject) {

		List<WeightedRandomBlock> resList = new ArrayList<WeightedRandomBlock>();

		if (!parseResList(genObject, resList)) {
			return false;
		}
		int clusterSize = 0;
		int numClusters = 0;
		boolean retrogen = false;
		GenRestriction biomeRes = GenRestriction.NONE;
		GenRestriction dimRes = GenRestriction.NONE;
		Block block = Blocks.stone;

		if (genObject.has("clusterSize")) {
			clusterSize = genObject.get("clusterSize").getAsInt();
		}
		if (genObject.has("numClusters")) {
			numClusters = genObject.get("numClusters").getAsInt();
		}
		if (clusterSize <= 0 || numClusters <= 0) {
			CoFHCore.log.error("Invalid cluster size or count specified in \"" + featureName + "\"");
			return false;
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
		if (genObject.has("material")) {
			String b = genObject.get("material").getAsString();
			block = Block.getBlockFromName(b);
			if (block == Blocks.air) {
				block = Blocks.stone;
			}
		}
		int minHeight = genObject.get("minHeight").getAsInt();
		int maxHeight = genObject.get("maxHeight").getAsInt();

		if (minHeight >= maxHeight || minHeight < 0) {
			CoFHCore.log.error("Invalid height parameters specified in \"" + featureName + "\"");
			return false;
		}
		FeatureBase feature = new FeatureOreGenUniform(featureName, new WorldGenMinableCluster(resList, clusterSize, block), numClusters, minHeight, maxHeight,
				biomeRes, retrogen, dimRes);

		addFeatureRestrictions(feature, genObject);
		WorldHandler.addFeature(feature);
		return true;
	}

	private static boolean addNormalTemplate(String featureName, JsonObject genObject) {

		List<WeightedRandomBlock> resList = new ArrayList<WeightedRandomBlock>();

		if (!parseResList(genObject, resList)) {
			return false;
		}
		int clusterSize = 0;
		int numClusters = 0;
		boolean retrogen = false;
		GenRestriction biomeRes = GenRestriction.NONE;
		GenRestriction dimRes = GenRestriction.NONE;
		Block block = Blocks.stone;

		if (genObject.has("clusterSize")) {
			clusterSize = genObject.get("clusterSize").getAsInt();
		}
		if (genObject.has("numClusters")) {
			numClusters = genObject.get("numClusters").getAsInt();
		}
		if (clusterSize <= 0 || numClusters <= 0) {
			CoFHCore.log.error("Invalid cluster size or count specified in \"" + featureName + "\"");
			return false;
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
		if (genObject.has("material")) {
			String b = genObject.get("material").getAsString();
			block = Block.getBlockFromName(b);
			if (block == Blocks.air) {
				block = Blocks.stone;
			}
		}
		int meanHeight = genObject.get("meanHeight").getAsInt();
		int maxVariance = genObject.get("maxVariance").getAsInt();

		if (meanHeight <= 0 || maxVariance <= 0) {
			CoFHCore.log.error("Invalid height parameters specified in \"" + featureName + "\"");
			return false;
		}
		FeatureBase feature = new FeatureOreGenNormal(featureName, new WorldGenMinableCluster(resList, clusterSize, block), numClusters, meanHeight,
				maxVariance, biomeRes, retrogen, dimRes);
		addFeatureRestrictions(feature, genObject);
		WorldHandler.addFeature(feature);
		return true;
	}

	private static boolean parseResList(JsonObject genObject, List<WeightedRandomBlock> resList) {

		if (genObject.get("block").isJsonArray()) {
			JsonArray blockList = genObject.getAsJsonArray("block");

			if (!genObject.get("metadata").isJsonArray()) {
				CoFHCore.log.error("Invalid metadata array.");
				return false;
			}
			if (!genObject.get("weight").isJsonArray()) {
				CoFHCore.log.error("Invalid block weight array.");
				return false;
			}
			JsonArray metaList = genObject.getAsJsonArray("metadata");
			if (metaList.size() != blockList.size()) {
				CoFHCore.log.error("Block and metadata array sizes are inconsistent.");
				return false;
			}
			JsonArray weightList = genObject.getAsJsonArray("weight");
			if (weightList.size() != blockList.size()) {
				CoFHCore.log.error("Block and weight array sizes are inconsistent.");
				return false;
			}
			for (int i = 0; i < blockList.size(); i++) {
				String blockRaw = blockList.get(i).getAsString();
				String[] blockTokens = blockRaw.split(":");
				Block block;

				switch (blockTokens.length) {
				case 1:
					block = Block.getBlockFromName(blockTokens[0]);
					if (block == null) {
						CoFHCore.log.error("Null block entry!");
						return false;
					}
					break;
				case 2:
					block = GameRegistry.findBlock(blockTokens[0], blockTokens[1]);
					if (block == null) {
						CoFHCore.log.error("Null block entry!");
						return false;
					}
					break;
				default:
					CoFHCore.log.error("Invalid block entry!");
					return false;
				}
				int metadata = MathHelper.clampI(metaList.get(i).getAsInt(), 0, 15);
				int weight = MathHelper.clampI(weightList.get(i).getAsInt(), 1, 1000000);
				resList.add(new WeightedRandomBlock(new ItemStack(block, 1, metadata), weight));
			}
		} else {
			String blockRaw = genObject.get("block").getAsString();
			String[] blockTokens = blockRaw.split(":");
			Block block;

			switch (blockTokens.length) {
			case 1:
				block = Block.getBlockFromName(blockTokens[0]);
				if (block == null) {
					CoFHCore.log.error("Null block entry!");
					return false;
				}
				break;
			case 2:
				block = GameRegistry.findBlock(blockTokens[0], blockTokens[1]);
				if (block == null) {
					CoFHCore.log.error("Null block entry!");
					return false;
				}
				break;
			default:
				CoFHCore.log.error("Invalid block entry!");
				return false;
			}
			int metadata = 0;

			if (genObject.has("metadata")) {
				metadata = MathHelper.clampI(genObject.get("metadata").getAsInt(), 0, 15);
			}
			resList.add(new WeightedRandomBlock(new ItemStack(block, 1, metadata)));
		}
		return true;
	}

	private static boolean addFeatureRestrictions(FeatureBase feature, JsonObject genObject) {

		if (feature.biomeRestriction != GenRestriction.NONE && genObject.has("biomes")) {
			JsonArray restrictionList = genObject.getAsJsonArray("biomes");
			for (int i = 0; i < restrictionList.size(); i++) {
				feature.addBiome(restrictionList.get(i).getAsString());
			}
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
