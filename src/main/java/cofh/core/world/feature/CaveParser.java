package cofh.core.world.feature;

import cofh.lib.util.numbers.INumberProvider;
import cofh.lib.world.feature.FeatureBase;
import cofh.lib.world.feature.FeatureBase.GenRestriction;
import cofh.lib.world.feature.FeatureGenCave;
import com.typesafe.config.Config;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

public class CaveParser extends UniformParser {

	@Override
	protected FeatureBase getFeature(String featureName, Config genObject, WorldGenerator gen, INumberProvider numClusters, GenRestriction biomeRes, boolean retrogen, GenRestriction dimRes, Logger log) {

		boolean ceiling = genObject.hasPath("ceiling") && genObject.getBoolean("ceiling");
		return new FeatureGenCave(featureName, gen, ceiling, numClusters, biomeRes, retrogen, dimRes);
	}

}
