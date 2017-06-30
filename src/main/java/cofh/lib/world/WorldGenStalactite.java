package cofh.lib.world;

import cofh.lib.util.WeightedRandomBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

import static cofh.lib.world.WorldGenMinableCluster.canGenerateInBlock;
import static cofh.lib.world.WorldGenMinableCluster.generateBlock;

public class WorldGenStalactite extends WorldGenStalagmite {

	public WorldGenStalactite(List<WeightedRandomBlock> resource, List<WeightedRandomBlock> block, List<WeightedRandomBlock> gblock) {

		super(resource, block, gblock);
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {

		int xStart = pos.getX();
		int yStart = pos.getY();
		int zStart = pos.getZ();

		int end = world.getActualHeight();
		while (world.isAirBlock(new BlockPos(xStart, yStart, zStart)) && yStart < end) {
			++yStart;
		}

		if (!canGenerateInBlock(world, xStart, yStart--, zStart, baseBlock)) {
			return false;
		}

		int maxHeight = rand.nextInt(heightVariance) + minHeight;

		int size = genSize > 0 ? genSize : maxHeight / heightMod + rand.nextInt(sizeVariance);
		boolean r = false;
		for (int x = -size; x <= size; ++x) {
			for (int z = -size; z <= size; ++z) {
				if (!canGenerateInBlock(world, xStart + x, yStart + 1, zStart + z, baseBlock)) {
					continue;
				}
				int height = getHeight(x, z, size, rand, maxHeight);
				for (int y = 0; y < height; ++y) {
					r |= generateBlock(world, xStart + x, yStart - y, zStart + z, genBlock, cluster);
				}
			}
		}
		return r;
	}
}
