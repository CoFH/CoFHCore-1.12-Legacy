package cofh.core.world.feature;

import cofh.lib.world.feature.FeatureBase;
import cofh.lib.world.feature.FeatureBase.GenRestriction;
import cofh.lib.world.feature.FeatureGenGaussian;
import com.google.gson.JsonObject;
import com.typesafe.config.Config;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

public class GaussianParser extends UniformParser {

	@Override
	protected FeatureBase getFeature(String featureName, Config genObject, WorldGenerator gen, int numClusters, GenRestriction biomeRes, boolean retrogen, GenRestriction dimRes, Logger log) {

		if (!(genObject.hasPath("centerHeight") && genObject.hasPath("spread"))) {
			log.error("Height parameters for 'normal' template not specified in \"" + featureName + "\"");
			return null;
		}
		int centerHeight = genObject.getInt("centerHeight");
		int spread = genObject.getInt("spread");
		int rolls = genObject.hasPath("smoothness") ? genObject.getInt("smoothness") : 2;

		return new FeatureGenGaussian(featureName, gen, numClusters, rolls, centerHeight, spread, biomeRes, retrogen, dimRes);
	}

}
