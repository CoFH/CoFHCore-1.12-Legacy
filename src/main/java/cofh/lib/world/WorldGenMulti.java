package cofh.lib.world;

import cofh.lib.util.WeightedRandomWorldGenerator;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenMulti extends WorldGenerator {

	private final WeightedRandomWorldGenerator[] generators;

	public WorldGenMulti(ArrayList<WeightedRandomWorldGenerator> values) {

		generators = values.toArray(new WeightedRandomWorldGenerator[values.size()]);
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {

		WeightedRandomWorldGenerator gen = (WeightedRandomWorldGenerator) WeightedRandom.getRandomItem(random, generators);
		return gen.generator.generate(world, random, x, y, z);
	}

}
