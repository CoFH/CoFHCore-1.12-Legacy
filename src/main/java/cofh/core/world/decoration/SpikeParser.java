package cofh.core.world.decoration;

import cofh.api.world.IGeneratorParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenSpike;
import com.google.gson.JsonObject;

import java.util.List;

import net.minecraft.world.gen.feature.WorldGenerator;

import org.apache.logging.log4j.Logger;

public class SpikeParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String generatorName, JsonObject genObject, Logger log, List<WeightedRandomBlock> resList, int clusterSize,
			List<WeightedRandomBlock> matList) {

		WorldGenSpike r = new WorldGenSpike(resList, matList);
		{
			if (genObject.has("largeSpikes")) {
				r.largeSpikes = genObject.get("largeSpikes").getAsBoolean();
			}
			if (genObject.has("largeSpikeChance")) {
				r.largeSpikeChance = genObject.get("largeSpikeChance").getAsInt();
			}
			if (genObject.has("minHeight")) {
				r.minHeight = genObject.get("minHeight").getAsInt();
			}
			if (genObject.has("heightVariance")) {
				r.heightVariance = genObject.get("heightVariance").getAsInt();
			}
			if (genObject.has("sizeVariance")) {
				r.sizeVariance = genObject.get("sizeVariance").getAsInt();
			}
			if (genObject.has("positionVariance")) {
				r.positionVariance = genObject.get("positionVariance").getAsInt();
			}
			if (genObject.has("minLargeSpikeHeightGain")) {
				r.minLargeSpikeHeightGain = genObject.get("minLargeSpikeHeightGain").getAsInt();
			}
			if (genObject.has("largeSpikeHeightVariance")) {
				r.largeSpikeHeightVariance = genObject.get("largeSpikeHeightVariance").getAsInt();
			}
			if (genObject.has("largeSpikeFillerSize")) {
				r.largeSpikeFillerSize = genObject.get("largeSpikeFillerSize").getAsInt();
			}
		}
		return r;
	}

}
