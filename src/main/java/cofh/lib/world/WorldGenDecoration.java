package cofh.lib.world;

import static cofh.lib.world.WorldGenMinableCluster.*;

import cofh.lib.util.WeightedRandomBlock;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenDecoration extends WorldGenerator {

	private final List<WeightedRandomBlock> cluster;
	private final WeightedRandomBlock[] genBlock;
	private final WeightedRandomBlock[] onBlock;
	private final int clusterSize;
	public boolean seeSky = true;
	public boolean checkStay = true;
	public int stackHeight = 1;
	public int xVar = 8;
	public int yVar = 4;
	public int zVar = 8;

	public WorldGenDecoration(List<WeightedRandomBlock> blocks, int count, List<WeightedRandomBlock> material, List<WeightedRandomBlock> on) {

		cluster = blocks;
		clusterSize = count;
		genBlock = material == null ? null : material.toArray(new WeightedRandomBlock[material.size()]);
		onBlock = on == null ? null : on.toArray(new WeightedRandomBlock[on.size()]);
	}

	@Override
	public boolean generate(World world, Random rand, int xStart, int yStart, int zStart) {

		boolean r = false;
		for (int l = clusterSize; l-- > 0;) {
			int x = xStart + rand.nextInt(xVar) - rand.nextInt(xVar);
			int y = yStart + (yVar > 1 ? rand.nextInt(yVar) - rand.nextInt(yVar) : 0);
			int z = zStart + rand.nextInt(zVar) - rand.nextInt(zVar);

			if (!world.blockExists(x, y, z)) {
				++l;
				continue;
			}

			if ((!seeSky || world.canBlockSeeTheSky(x, y, z)) && canGenerateInBlock(world, x, y - 1, z, onBlock)
					&& canGenerateInBlock(world, x, y, z, genBlock)) {

				WeightedRandomBlock block = selectBlock(world, cluster);
				int stack = stackHeight > 1 ? rand.nextInt(stackHeight) : 0;
				do {
					if (!checkStay || block.block.canBlockStay(world, x, y, z)) {
						r |= world.setBlock(x, y, z, block.block, block.metadata, 2);
					} else {
						break;
					}
					++y;
					if (!canGenerateInBlock(world, x, y, z, genBlock)) {
						break;
					}
				} while (stack-- > 0);
			}
		}
		return r;
	}

}
