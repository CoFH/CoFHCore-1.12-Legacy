package cofh.core.world.feature;

import cofh.core.util.numbers.INumberProvider;
import cofh.core.world.FeatureParser;
import cofh.core.world.feature.FeatureBase.GenRestriction;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

public class FractalParser extends UniformParser {

	@Override
	protected FeatureBase getFeature(String featureName, Config genObject, WorldGenerator gen, INumberProvider numClusters, GenRestriction biomeRes, boolean retrogen, GenRestriction dimRes, Logger log) {

		if (!(genObject.hasPath("min-height") && genObject.hasPath("vein-height"))) {
			log.error("Height parameters for 'fractal' template not specified in \"" + featureName + "\"");
			return null;
		}
		if (!(genObject.hasPath("vein-diameter"))) {
			log.error("veinDiameter parameter for 'fractal' template not specified in \"" + featureName + "\"");
			return null;
		}
		if (!(genObject.hasPath("vertical-density") && genObject.hasPath("horizontal-density"))) {
			log.error("Density parameters for 'fractal' template not specified in \"" + featureName + "\"");
			return null;
		}
		ConfigObject genData = genObject.root();
		INumberProvider minY = FeatureParser.parseNumberValue(genData.get("min-height"));
		INumberProvider h = FeatureParser.parseNumberValue(genData.get("vein-height"));
		INumberProvider d = FeatureParser.parseNumberValue(genData.get("vein-diameter"));
		INumberProvider vD = FeatureParser.parseNumberValue(genData.get("vertical-density"), 0, 100);
		INumberProvider hD = FeatureParser.parseNumberValue(genData.get("horizontal-density"), 0, 100);

		return new FeatureGenLargeVein(featureName, gen, numClusters, minY, biomeRes, retrogen, dimRes, h, d, vD, hD);
	}

	@Override
	protected String getDefaultGenerator() {

		return "large-vein";
	}

}
