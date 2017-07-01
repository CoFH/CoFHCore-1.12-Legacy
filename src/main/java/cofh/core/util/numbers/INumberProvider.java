package cofh.core.util.numbers;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public interface INumberProvider {

	int intValue(World world, Random rand, BlockPos pos);

	long longValue(World world, Random rand, BlockPos pos);

	float floatValue(World world, Random rand, BlockPos pos);

	double doubleValue(World world, Random rand, BlockPos pos);

}
