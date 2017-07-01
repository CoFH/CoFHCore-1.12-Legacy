package cofh.core.world.feature;

import cofh.core.util.WeightedRandomBlock;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.numbers.INumberProvider;
import cofh.core.world.FeatureParser;
import cofh.core.world.IFeatureGenerator;
import cofh.core.world.IFeatureParser;
import cofh.core.world.feature.FeatureBase.GenRestriction;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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

		INumberProvider numClusters = FeatureParser.parseNumberValue(genObject.getValue("cluster-count"), 0, Long.MAX_VALUE);
		boolean retrogen = false;
		if (genObject.hasPath("retrogen")) {
			retrogen = genObject.getBoolean("retrogen");
		}
		GenRestriction biomeRes = GenRestriction.NONE;
		if (genObject.hasPath("biome")) {
			ConfigValue data = genObject.getValue("biome");
			if (data.valueType() == ConfigValueType.STRING) {
				biomeRes = GenRestriction.get(genObject.getString("biome"));
				if (biomeRes != GenRestriction.NONE) {
					log.error("Invalid biome restriction %2$s on '%1$s'. Must be an object to meaningfully function", featureName, biomeRes.name().toLowerCase(Locale.US));
					return null;
				}
			} else if (data.valueType() == ConfigValueType.OBJECT) {
				biomeRes = GenRestriction.get(genObject.getString("biome.restriction"));
			}
		}
		GenRestriction dimRes = GenRestriction.NONE;
		if (genObject.hasPath("dimension")) {
			ConfigValue data = genObject.getValue("dimension");
			switch (data.valueType()) {
				case STRING:
					dimRes = GenRestriction.get(genObject.getString("dimension"));
					if (dimRes != GenRestriction.NONE) {
						log.error("Invalid dimension restriction %2$s on '%1$s'. Must be an object to meaningfully function", featureName, dimRes.name().toLowerCase(Locale.US));
						return null;
					}
					break;
				case OBJECT:
					dimRes = GenRestriction.get(genObject.getString("dimension.restriction"));
					break;
				case LIST:
				case NUMBER:
					dimRes = GenRestriction.WHITELIST;
			}
		}

		WorldGenerator generator = FeatureParser.parseGenerator(getDefaultGenerator(), genObject, defaultMaterial);
		if (generator == null) {
			log.warn("Invalid generator for '%s'!", featureName);
			return null;
		}
		FeatureBase feature = getFeature(featureName, genObject, generator, numClusters, biomeRes, retrogen, dimRes, log);

		if (feature != null) {
			if (genObject.hasPath("chunk-chance")) {
				int rarity = MathHelper.clamp(genObject.getInt("chunk-chance"), 1, 1000000000);
				feature.setRarity(rarity);
			}
			addFeatureRestrictions(feature, genObject);
			if (genObject.hasPath("in-village")) {
				feature.withVillage = genObject.getBoolean("in-village");
			}
		}
		return feature;
	}

	protected FeatureBase getFeature(String featureName, Config genObject, WorldGenerator gen, INumberProvider numClusters, GenRestriction biomeRes, boolean retrogen, GenRestriction dimRes, Logger log) {

		if (!(genObject.hasPath("min-height") && genObject.hasPath("max-height"))) {
			log.error("Height parameters for 'uniform' template not specified in \"" + featureName + "\"");
			return null;
		}

		INumberProvider minHeight = FeatureParser.parseNumberValue(genObject.root().get("min-height"));
		INumberProvider maxHeight = FeatureParser.parseNumberValue(genObject.root().get("max-height"));

		return new FeatureGenUniform(featureName, gen, numClusters, minHeight, maxHeight, biomeRes, retrogen, dimRes);
	}

	protected String getDefaultGenerator() {

		return "cluster";
	}

	protected static void addFeatureRestrictions(FeatureBase feature, Config genObject) {

		if (feature.biomeRestriction != GenRestriction.NONE) {
			feature.addBiomes(FeatureParser.parseBiomeRestrictions(genObject.getConfig("biome")));
		}
		if (feature.dimensionRestriction != GenRestriction.NONE) {
			String field = "dimension";
			ConfigValue data = genObject.getValue(field);
			ConfigList restrictionList = null;
			switch (data.valueType()) {
				case OBJECT:
					field += ".value";
				case LIST:
					restrictionList = genObject.getList(field);
					break;
				case NUMBER:
					feature.addDimension(genObject.getNumber(field).intValue());
					break;
				default:
					// unreachable
					break;
			}
			if (restrictionList != null) {
				for (int i = 0; i < restrictionList.size(); i++) {
					ConfigValue val = restrictionList.get(i);
					if (val.valueType() == ConfigValueType.NUMBER) {
						feature.addDimension(((Number) val.unwrapped()).intValue());
					}
				}
			}
		}
	}

}
