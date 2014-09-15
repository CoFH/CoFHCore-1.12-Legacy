package cofh.core.world.feature;

import org.apache.logging.log4j.Logger;

import java.util.List;

import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.feature.FeatureBase;
import cofh.lib.world.feature.FeatureBase.GenRestriction;
import cofh.lib.world.feature.FeatureGenNormal;
import com.google.gson.JsonObject;

import net.minecraft.world.gen.feature.WorldGenerator;

public class NormalParser extends UniformParser {

	@Override
	protected FeatureBase getFeature(String featureName, JsonObject genObject, WorldGenerator gen, List<WeightedRandomBlock> matList, int numClusters, GenRestriction biomeRes, boolean retrogen,
			GenRestriction dimRes, Logger log) {

		int meanHeight = genObject.get("meanHeight").getAsInt();
		int maxVariance = genObject.get("maxVariance").getAsInt();

		return new FeatureGenNormal(featureName, gen, numClusters, meanHeight, maxVariance, biomeRes, retrogen, dimRes);
	}

}
