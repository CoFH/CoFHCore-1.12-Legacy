package cofh.core.world.decoration;

import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.IGeneratorParser;
import cofh.lib.world.WorldGenSpike;
import com.typesafe.config.Config;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class SpikeParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String generatorName, Config genObject, Logger log, List<WeightedRandomBlock> resList, List<WeightedRandomBlock> matList) {

		WorldGenSpike r = new WorldGenSpike(resList, matList);
		{
			if (genObject.hasPath("min-height")) {
				r.minHeight = genObject.getInt("min-height");
			}
			if (genObject.hasPath("height-variance")) {
				r.heightVariance = genObject.getInt("height-variance");
			}
			if (genObject.hasPath("size-variance")) {
				r.sizeVariance = genObject.getInt("size-variance");
			}
			if (genObject.hasPath("position-variance")) {
				r.positionVariance = genObject.getInt("position-variance");
			}
			// TODO: these fields need addressed. combined into a sub-object?
			if (genObject.hasPath("large-spikes")) {
				r.largeSpikes = genObject.getBoolean("large-spikes");
			}
			if (genObject.hasPath("large-spike-chance")) {
				r.largeSpikeChance = genObject.getInt("large-spike-chance");
			}
			if (genObject.hasPath("min-large-spike-height-gain")) {
				r.minLargeSpikeHeightGain = genObject.getInt("min-large-spike-height-gain");
			}
			if (genObject.hasPath("large-spike-height-variance")) {
				r.largeSpikeHeightVariance = genObject.getInt("large-spike-height-variance");
			}
			if (genObject.hasPath("large-spike-filler-size")) {
				r.largeSpikeFillerSize = genObject.getInt("large-spike-filler-size");
			}
		}
		return r;
	}

}
