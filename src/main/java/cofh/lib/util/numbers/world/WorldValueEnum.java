package cofh.lib.util.numbers.world;

import cofh.lib.util.helpers.BlockHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public enum WorldValueEnum {

	WORLD_HEIGHT {
		@Override
		public long getValue(World world, Random rand, BlockPos pos) {

			return world.getActualHeight();
		}
	}, SEA_LEVEL {
		@Override
		public long getValue(World world, Random rand, BlockPos pos) {

			return world.getSeaLevel();
		}
	}, GROUND_LEVEL {
		@Override
		public long getValue(World world, Random rand, BlockPos pos) {

			return world.provider.getAverageGroundLevel();
		}
	}, RAIN_HEIGHT {
		@Override
		public long getValue(World world, Random rand, BlockPos pos) {

			return world.getPrecipitationHeight(pos).getY();
		}
	}, HEIGHT_MAP {
		@Override
		public long getValue(World world, Random rand, BlockPos pos) {

			return world.getHeight(pos.getX(), pos.getZ());
		}
	}, HIGHEST_BLOCK {
		@Override
		public long getValue(World world, Random rand, BlockPos pos) {

			return BlockHelper.getTopBlockY(world, pos.getX(), pos.getY());
		}
	}, SURFACE_BLOCK {
		@Override
		public long getValue(World world, Random rand, BlockPos pos) {

			return BlockHelper.getSurfaceBlockY(world, pos.getX(), pos.getZ());
		}
	}, CURRENT_Y {
		@Override
		public long getValue(World world, Random rand, BlockPos pos) {

			return pos.getY();
		}
	}, LOWEST_CHUNK_HORIZON {
		@Override
		public long getValue(World world, Random rand, BlockPos pos) {

			return world.getChunksLowestHorizon(pos.getX(), pos.getZ());
		}
	};

	public abstract long getValue(World world, Random rand, BlockPos pos);

}
