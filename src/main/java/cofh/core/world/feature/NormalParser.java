package cofh.core.world.feature;

import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.feature.FeatureBase;
import cofh.lib.world.feature.FeatureBase.GenRestriction;
import cofh.lib.world.feature.FeatureGenNormal;
import com.google.gson.JsonObject;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class NormalParser extends UniformParser {

	@Override
	protected FeatureBase getFeature(String featureName, JsonObject genObject, WorldGenerator gen, int numClusters, GenRestriction biomeRes, boolean retrogen, GenRestriction dimRes, Logger log) {

		if (!(genObject.has("meanHeight") && genObject.has("maxVariance"))) {
			log.error("Height parameters for 'normal' template not specified in \"" + featureName + "\"");
			return null;
		}
		int meanHeight = genObject.get("meanHeight").getAsInt();
		int maxVariance = genObject.get("maxVariance").getAsInt();

		return new FeatureGenNormal(featureName, gen, numClusters, meanHeight, maxVariance, biomeRes, retrogen, dimRes);
	}

}
