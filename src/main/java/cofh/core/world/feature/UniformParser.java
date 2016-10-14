package cofh.core.world.feature;

import cofh.api.world.IFeatureGenerator;
import cofh.api.world.IFeatureParser;
import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.world.feature.FeatureBase;
import cofh.lib.world.feature.FeatureBase.GenRestriction;
import cofh.lib.world.feature.FeatureGenUniform;
import com.google.gson.JsonArray;
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

		return Arrays.asList(new WeightedRandomBlock(new ItemStack(Blocks.STONE, 1, -1)));
	}

	@Override
	public IFeatureGenerator parseFeature(String featureName, JsonObject genObject, Logger log) {

		List<WeightedRandomBlock> resList = new ArrayList<WeightedRandomBlock>();
		if (!FeatureParser.parseResList(genObject.get("block"), resList, true)) {
			return null;
		}
		int clusterSize = 0;
		if (genObject.has("clusterSize")) {
			clusterSize = genObject.get("clusterSize").getAsInt();
		}
		int numClusters = 0;
		if (genObject.has("numClusters")) {
			numClusters = genObject.get("numClusters").getAsInt();
		}
		if (clusterSize < 0 || numClusters <= 0) {
			log.error("Invalid cluster size or count specified in \"" + featureName + "\"");
			return null;
		}
		boolean retrogen = false;
		if (genObject.has("retrogen")) {
			retrogen = genObject.get("retrogen").getAsBoolean();
		}
		GenRestriction biomeRes = GenRestriction.NONE;
		if (genObject.has("biomeRestriction")) {
			biomeRes = GenRestriction.get(genObject.get("biomeRestriction").getAsString());
		}
		GenRestriction dimRes = GenRestriction.NONE;
		if (genObject.has("dimensionRestriction")) {
			dimRes = GenRestriction.get(genObject.get("dimensionRestriction").getAsString());
		}
		List<WeightedRandomBlock> matList = parseMaterial(genObject, log);

		WorldGenerator generator = FeatureParser.parseGenerator(getDefaultTemplate(), genObject, resList, clusterSize, matList);
		FeatureBase feature = getFeature(featureName, genObject, generator, matList, numClusters, biomeRes, retrogen, dimRes, log);

		if (feature != null) {
			if (genObject.has("chunkChance")) {
				int rarity = MathHelper.clamp(genObject.get("chunkChance").getAsInt(), 1, 1000000);
				feature.setRarity(rarity);
			}
			addFeatureRestrictions(feature, genObject);
		}
		return feature;
	}

	protected FeatureBase getFeature(String featureName, JsonObject genObject, WorldGenerator gen, List<WeightedRandomBlock> matList, int numClusters,
			GenRestriction biomeRes, boolean retrogen, GenRestriction dimRes, Logger log) {

		if (!(genObject.has("minHeight") && genObject.has("maxHeight"))) {
			log.error("Height parameters for 'uniform' template not specified in \"" + featureName + "\"");
			return null;
		}

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
			if (!FeatureParser.parseResList(genObject.get("material"), matList, false)) {
				log.warn("Invalid material list! Using default list.");
				matList = defaultMaterial;
			}
		}
		return matList;
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
