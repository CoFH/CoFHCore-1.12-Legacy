package cofh.core.world.feature;

import cofh.lib.world.feature.FeatureBase;
import cofh.lib.world.feature.FeatureBase.GenRestriction;
import cofh.lib.world.feature.FeatureOreGenNormal;
import com.google.gson.JsonObject;

import net.minecraft.world.gen.feature.WorldGenerator;

public class NormalParser extends UniformParser {

	@Override
	protected FeatureBase getFeature(String name, WorldGenerator gen, int numClusters, int minHeight, int maxHeight, GenRestriction biomeRes, boolean retrogen,
			GenRestriction dimRes) {

		return new FeatureOreGenNormal(name, gen, numClusters, minHeight, maxHeight, biomeRes, retrogen, dimRes);
	}

	@Override
	protected int parseMinHeight(JsonObject genObject) {

		return genObject.get("meanHeight").getAsInt();
	}

	@Override
	protected int parseMaxHeight(JsonObject genObject) {

		return genObject.get("maxVariance").getAsInt();
	}

	@Override
	protected boolean verifyHeight(int minHeight, int maxHeight) {

		return false;
	}

}
