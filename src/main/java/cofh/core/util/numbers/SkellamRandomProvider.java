package cofh.core.util.numbers;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

import static cofh.core.util.numbers.UniformRandomProvider.getRandomDouble;
import static cofh.core.util.numbers.UniformRandomProvider.getRandomLong;

public class SkellamRandomProvider extends ConstantProvider {

	public SkellamRandomProvider(Number value) {

		super(value);
	}

	public long longValue(World world, Random rand, BlockPos pos) {

		long val = min.longValue();
		return getRandomLong(val, rand) - getRandomLong(val, rand);
	}

	public double doubleValue(World world, Random rand, BlockPos pos) {

		double val = min.doubleValue();
		return getRandomDouble(val, rand) - getRandomDouble(val, rand);
	}

}
