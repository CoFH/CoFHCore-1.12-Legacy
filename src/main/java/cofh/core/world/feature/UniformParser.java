package cofh.core.world.feature;

import cofh.api.world.IFeatureGenerator;
import cofh.api.world.IFeatureParser;
import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.numbers.INumberProvider;
import cofh.lib.world.feature.FeatureBase;
import cofh.lib.world.feature.FeatureBase.GenRestriction;
import cofh.lib.world.feature.FeatureGenUniform;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

public class UniformParser implements IFeatureParser {

	protected final List<WeightedRandomBlock> defaultMaterial;

	public UniformParser() {

		defaultMaterial = generateDefaultMaterial();
	}

	protected List<WeightedRandomBlock> generateDefaultMaterial() {

		return Arrays.asList(new WeightedRandomBlock(Blocks.STONE, -1));
	}

	@Override
	public IFeatureGenerator parseFeature(String featureName, Config genObject, Logger log) {

		INumberProvider numClusters = FeatureParser.parseNumberValue(genObject.root().get("clusterCount"), 0, Long.MAX_VALUE);
		boolean retrogen = false;
		if (genObject.hasPath("retrogen")) {
			retrogen = genObject.getBoolean("retrogen");
		}
		GenRestriction biomeRes = GenRestriction.NONE;
		if (genObject.hasPath("biomeRestriction")) {
			biomeRes = GenRestriction.get(genObject.getString("biomeRestriction"));
		}
		GenRestriction dimRes = GenRestriction.NONE;
		if (genObject.hasPath("dimensionRestriction")) {
			dimRes = GenRestriction.get(genObject.getString("dimensionRestriction"));
		}

		WorldGenerator generator = FeatureParser.parseGenerator(getDefaultGenerator(), genObject, defaultMaterial);
		if (generator == null) {
			log.warn("Invalid generator for '%s'!", featureName);
			return null;
		}
		FeatureBase feature = getFeature(featureName, genObject, generator, numClusters, biomeRes, retrogen, dimRes, log);

		if (feature != null) {
			if (genObject.hasPath("chunkChance")) {
				int rarity = MathHelper.clamp(genObject.getInt("chunkChance"), 1, 1000000);
				feature.setRarity(rarity);
			}
			addFeatureRestrictions(feature, genObject);
		}
		return feature;
	}

	protected FeatureBase getFeature(String featureName, Config genObject, WorldGenerator gen, INumberProvider numClusters, GenRestriction biomeRes, boolean retrogen, GenRestriction dimRes, Logger log) {

		if (!(genObject.hasPath("minHeight") && genObject.hasPath("maxHeight"))) {
			log.error("Height parameters for 'uniform' template not specified in \"" + featureName + "\"");
			return null;
		}

		INumberProvider minHeight = FeatureParser.parseNumberValue(genObject.root().get("minHeight"));
		INumberProvider maxHeight = FeatureParser.parseNumberValue(genObject.root().get("maxHeight"));

		// TODO: er... well, we need this
		//if (minHeight >= maxHeight || minHeight < 0)
		{
		//	log.error("Invalid height parameters specified in \"" + featureName + "\"");
		//	return null;
		}
		return new FeatureGenUniform(featureName, gen, numClusters, minHeight, maxHeight, biomeRes, retrogen, dimRes);
	}

	protected String getDefaultGenerator() {

		return "cluster";
	}

	protected static boolean addFeatureRestrictions(FeatureBase feature, Config genObject) {

		if (feature.biomeRestriction != GenRestriction.NONE) {
			feature.addBiomes(FeatureParser.parseBiomeRestrictions(genObject));
		}
		if (feature.dimensionRestriction != GenRestriction.NONE && genObject.hasPath("dimensions")) {
			ConfigList restrictionList = genObject.getList("dimensions");
			for (int i = 0; i < restrictionList.size(); i++) {
				ConfigValue val = restrictionList.get(i);
				if (val.valueType() == ConfigValueType.NUMBER) {
					feature.addDimension(((Number)val.unwrapped()).intValue());
				}
			}
		}
		return true;
	}

}
