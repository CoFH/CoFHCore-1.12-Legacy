package cofh.core.util.numbers;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class ConstantProvider implements INumberProvider {

	protected Number min;

	public ConstantProvider(Number value) {

		if (value == null) {
			throw new IllegalArgumentException("Null value not allowed");
		}
		this.min = value;
	}

	public int intValue(World world, Random rand, BlockPos pos) {

		return (int) longValue(world, rand, pos);
	}

	public long longValue(World world, Random rand, BlockPos pos) {

		return min.longValue();
	}

	public float floatValue(World world, Random rand, BlockPos pos) {

		return (float) doubleValue(world, rand, pos);
	}

	public double doubleValue(World world, Random rand, BlockPos pos) {

		return min.doubleValue();
	}

}
