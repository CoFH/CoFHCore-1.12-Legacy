package cofh.lib.util;

import net.minecraft.util.WeightedRandom;
import net.minecraft.world.gen.feature.WorldGenerator;

public final class WeightedRandomWorldGenerator extends WeightedRandom.Item {

	public final WorldGenerator generator;

	public WeightedRandomWorldGenerator(WorldGenerator worldgen) {

		this(worldgen, 100);
	}

	public WeightedRandomWorldGenerator(WorldGenerator worldgen, int weight) {

		super(weight);
		generator = worldgen;
	}

}
