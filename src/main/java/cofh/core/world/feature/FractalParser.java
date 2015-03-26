package cofh.core.world.feature;

import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.feature.FeatureBase;
import cofh.lib.world.feature.FeatureBase.GenRestriction;
import cofh.lib.world.feature.FeatureGenLargeVein;
import com.google.gson.JsonObject;

import java.util.List;

import net.minecraft.world.gen.feature.WorldGenerator;

import org.apache.logging.log4j.Logger;

public class FractalParser extends UniformParser {

	@Override
	protected FeatureBase getFeature(String featureName, JsonObject genObject, WorldGenerator gen, List<WeightedRandomBlock> matList, int numClusters,
			GenRestriction biomeRes, boolean retrogen, GenRestriction dimRes, Logger log) {

		if (!(genObject.has("minHeight") && genObject.has("veinHeight"))) {
			log.error("Height parameters for 'fractal' template not specified in \"" + featureName + "\"");
			return null;
		}

		if (!(genObject.has("veinDiameter"))) {
			log.error("veinDiameter parameter for 'fractal' template not specified in \"" + featureName + "\"");
			return null;
		}

		if (!(genObject.has("verticalDensity") && genObject.has("horizontalDensity"))) {
			log.error("Density parameters for 'fractal' template not specified in \"" + featureName + "\"");
			return null;
		}

		int minY = genObject.get("minHeight").getAsInt();
		int h = genObject.get("veinHeight").getAsInt();
		int d = genObject.get("veinDiameter").getAsInt();
		int vD = genObject.get("verticalDensity").getAsInt();
		int hD = genObject.get("horizontalDensity").getAsInt();

		return new FeatureGenLargeVein(featureName, gen, numClusters, minY, biomeRes, retrogen, dimRes, h, d, vD, hD);
	}

	@Override
	protected String getDefaultTemplate() {

		return "large-vein";
	}

}
