package cofh.core.world.feature;

import cofh.core.world.FeatureParser;
import cofh.lib.util.numbers.INumberProvider;
import cofh.lib.world.feature.FeatureBase;
import cofh.lib.world.feature.FeatureBase.GenRestriction;
import cofh.lib.world.feature.FeatureGenLargeVein;
import com.google.gson.JsonObject;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

public class FractalParser extends UniformParser {

	@Override
	protected FeatureBase getFeature(String featureName, Config genObject, WorldGenerator gen, INumberProvider numClusters, GenRestriction biomeRes, boolean retrogen, GenRestriction dimRes, Logger log) {

		if (!(genObject.hasPath("minHeight") && genObject.hasPath("veinHeight"))) {
			log.error("Height parameters for 'fractal' template not specified in \"" + featureName + "\"");
			return null;
		}
		if (!(genObject.hasPath("veinDiameter"))) {
			log.error("veinDiameter parameter for 'fractal' template not specified in \"" + featureName + "\"");
			return null;
		}
		if (!(genObject.hasPath("verticalDensity") && genObject.hasPath("horizontalDensity"))) {
			log.error("Density parameters for 'fractal' template not specified in \"" + featureName + "\"");
			return null;
		}
		ConfigObject genData = genObject.root();
		INumberProvider minY = FeatureParser.parseNumberValue(genData.get("minHeight"));
		INumberProvider h = FeatureParser.parseNumberValue(genData.get("veinHeight"));
		INumberProvider d = FeatureParser.parseNumberValue(genData.get("veinDiameter"));
		INumberProvider vD = FeatureParser.parseNumberValue(genData.get("verticalDensity"));
		INumberProvider hD = FeatureParser.parseNumberValue(genData.get("horizontalDensity"));

		return new FeatureGenLargeVein(featureName, gen, numClusters, minY, biomeRes, retrogen, dimRes, h, d, vD, hD);
	}

	@Override
	protected String getDefaultGenerator() {

		return "large-vein";
	}

}
