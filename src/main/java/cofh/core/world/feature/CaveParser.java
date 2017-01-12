package cofh.core.world.feature;

import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.feature.FeatureBase;
import cofh.lib.world.feature.FeatureBase.GenRestriction;
import cofh.lib.world.feature.FeatureGenCave;
import com.google.gson.JsonObject;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class CaveParser extends UniformParser {

	@Override
	protected FeatureBase getFeature(String featureName, JsonObject genObject, WorldGenerator gen, List<WeightedRandomBlock> matList, int numClusters, GenRestriction biomeRes, boolean retrogen, GenRestriction dimRes, Logger log) {

		boolean ceiling = genObject.has("ceiling") && genObject.get("ceiling").getAsBoolean();
		return new FeatureGenCave(featureName, gen, ceiling, numClusters, biomeRes, retrogen, dimRes);
	}

}
