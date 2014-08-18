package cofh.core.world.feature;

import cofh.api.world.IFeatureGenerator;
import cofh.api.world.IFeatureParser;
import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenMinableCluster;
import cofh.lib.world.WorldGenSparseMinableCluster;
import cofh.lib.world.feature.FeatureBase;
import cofh.lib.world.feature.FeatureBase.GenRestriction;
import cofh.lib.world.feature.FeatureOreGenUniform;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.feature.WorldGenerator;

import org.apache.logging.log4j.Logger;

public class UniformParser implements IFeatureParser {

	private static List<WeightedRandomBlock> defaultMaterial;

	static {

		defaultMaterial = Arrays.asList(new WeightedRandomBlock(new ItemStack(Blocks.stone, 1, 0)));
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
		List<WeightedRandomBlock> matList = defaultMaterial;

		if (genObject.has("clusterSize")) {
			clusterSize = genObject.get("clusterSize").getAsInt();
		}
		if (genObject.has("numClusters")) {
			numClusters = genObject.get("numClusters").getAsInt();
		}
		if (clusterSize <= 0 || numClusters <= 0) {
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
		if (genObject.has("material")) {
			matList = new ArrayList<WeightedRandomBlock>();
			if (!FeatureParser.parseResList(genObject.get("material"), matList)) {
				log.warn("Invalid material list! Using default list.");
				matList = defaultMaterial;
			}
		}
		int minHeight = parseMinHeight(genObject);
		int maxHeight = parseMaxHeight(genObject);

		if (minHeight >= maxHeight || minHeight < 0) {
			log.error("Invalid height parameters specified in \"" + featureName + "\"");
			return null;
		}
		FeatureBase feature = getFeature(featureName, getGenerator(genObject, resList, clusterSize, matList), numClusters, minHeight, maxHeight, biomeRes, retrogen, dimRes);

		addFeatureRestrictions(feature, genObject);
		return feature;
	}
	
	protected FeatureBase getFeature(String name, WorldGenerator gen, int numClusters, int minHeight, int maxHeight, GenRestriction biomeRes, boolean retrogen, GenRestriction dimRes) {
		
		return new FeatureOreGenUniform(name, gen, numClusters, minHeight, maxHeight, biomeRes, retrogen, dimRes);
	}
	
	protected int parseMinHeight(JsonObject genObject) {
		
		return genObject.get("minHeight").getAsInt();
	}

	protected int parseMaxHeight(JsonObject genObject) {
		
		return genObject.get("maxHeight").getAsInt();
	}

	protected WorldGenerator getGenerator(JsonObject genObject, List<WeightedRandomBlock> resList, int clusterSize, List<WeightedRandomBlock> matList) {

		JsonElement genElement = genObject.get("template");
		if (genElement.isJsonObject()) {
			genObject = genElement.getAsJsonObject();

			String template = genObject.get("generator").getAsString();

			if ("sparse-cluster".equals(template)) {
				return new WorldGenSparseMinableCluster(resList, clusterSize, matList);
			} else if (!"cluster".equals(template)) {
				// TODO: log warning
			}
			return new WorldGenMinableCluster(resList, clusterSize, matList);
		}

		return new WorldGenMinableCluster(resList, clusterSize, matList);
	}

	protected static boolean addFeatureRestrictions(FeatureBase feature, JsonObject genObject) {

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
