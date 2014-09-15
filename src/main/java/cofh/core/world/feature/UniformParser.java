package cofh.core.world.feature;

import cofh.api.world.IFeatureGenerator;
import cofh.api.world.IFeatureParser;
import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenMinableCluster;
import cofh.lib.world.WorldGenMinableLargeVein;
import cofh.lib.world.WorldGenSparseMinableCluster;
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
import net.minecraft.world.gen.feature.WorldGenerator;

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
			if (!parseMaterial(genObject, matList)) {
				log.warn("Invalid material list! Using default list.");
				matList = defaultMaterial;
			}
		}

		FeatureBase feature = getFeature(featureName, genObject, getGenerator(genObject, log, resList, clusterSize, matList),
				matList, numClusters, biomeRes, retrogen, dimRes, log);

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

	protected boolean parseMaterial(JsonObject genObject, List<WeightedRandomBlock> matList) {

		return FeatureParser.parseResList(genObject.get("material"), matList);
	}

	protected WorldGenerator getGenerator(JsonObject genObject, Logger log, List<WeightedRandomBlock> resList, int clusterSize,
			List<WeightedRandomBlock> matList) {

		String template = getDefaultTemplate();

		JsonElement genElement = genObject.get("template");
		if (genElement.isJsonObject()) {
			genObject = genElement.getAsJsonObject();

			if (genObject.has("generator")) {
				template = genObject.get("generator").getAsString();
			}
		}

		if ("sparse-cluster".equals(template)) {
			return new WorldGenSparseMinableCluster(resList, clusterSize, matList);
		} else if ("fractal".equals(template)) {
			return new WorldGenMinableLargeVein(resList, clusterSize, matList);
		} else if (!"cluster".equals(template)) {
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
