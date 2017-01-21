package cofh.core.world.decoration;

import cofh.api.world.IGeneratorParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenSpike;
import com.google.gson.JsonObject;
import com.typesafe.config.Config;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class SpikeParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String generatorName, Config genObject, Logger log, List<WeightedRandomBlock> resList, int clusterSize, List<WeightedRandomBlock> matList) {

		WorldGenSpike r = new WorldGenSpike(resList, matList);
		{
			if (genObject.hasPath("largeSpikes")) {
				r.largeSpikes = genObject.getBoolean("largeSpikes");
			}
			if (genObject.hasPath("largeSpikeChance")) {
				r.largeSpikeChance = genObject.getInt("largeSpikeChance");
			}
			if (genObject.hasPath("minHeight")) {
				r.minHeight = genObject.getInt("minHeight");
			}
			if (genObject.hasPath("heightVariance")) {
				r.heightVariance = genObject.getInt("heightVariance");
			}
			if (genObject.hasPath("sizeVariance")) {
				r.sizeVariance = genObject.getInt("sizeVariance");
			}
			if (genObject.hasPath("positionVariance")) {
				r.positionVariance = genObject.getInt("positionVariance");
			}
			if (genObject.hasPath("minLargeSpikeHeightGain")) {
				r.minLargeSpikeHeightGain = genObject.getInt("minLargeSpikeHeightGain");
			}
			if (genObject.hasPath("largeSpikeHeightVariance")) {
				r.largeSpikeHeightVariance = genObject.getInt("largeSpikeHeightVariance");
			}
			if (genObject.hasPath("largeSpikeFillerSize")) {
				r.largeSpikeFillerSize = genObject.getInt("largeSpikeFillerSize");
			}
		}
		return r;
	}

}
