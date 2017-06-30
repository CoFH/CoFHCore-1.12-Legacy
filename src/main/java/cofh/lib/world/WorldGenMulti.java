package cofh.lib.world;

import cofh.lib.util.WeightedRandomWorldGenerator;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGenMulti extends WorldGenerator {

	private final List<WeightedRandomWorldGenerator> generators;

	public WorldGenMulti(ArrayList<WeightedRandomWorldGenerator> values) {

		generators = values;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos pos) {

		WeightedRandomWorldGenerator gen = WeightedRandom.getRandomItem(random, generators);
		return gen.generator.generate(world, random, pos);
	}

}
