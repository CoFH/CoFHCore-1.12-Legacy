package cofh.lib.world;

import static cofh.lib.world.WorldGenMinableCluster.*;

import cofh.lib.util.WeightedRandomBlock;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenSparseMinableCluster extends WorldGenerator {

	private final List<WeightedRandomBlock> cluster;
	private final int genClusterSize;
	private final WeightedRandomBlock[] genBlock;

	public WorldGenSparseMinableCluster(ItemStack ore, int clusterSize) {

		this(new WeightedRandomBlock(ore), clusterSize);
	}

	public WorldGenSparseMinableCluster(WeightedRandomBlock resource, int clusterSize) {

		this(fabricateList(resource), clusterSize);
	}

	public WorldGenSparseMinableCluster(List<WeightedRandomBlock> resource, int clusterSize) {

		this(resource, clusterSize, Blocks.stone);
	}

	public WorldGenSparseMinableCluster(ItemStack ore, int clusterSize, Block block) {

		this(new WeightedRandomBlock(ore, 1), clusterSize, block);
	}

	public WorldGenSparseMinableCluster(WeightedRandomBlock resource, int clusterSize, Block block) {

		this(fabricateList(resource), clusterSize, block);
	}

	public WorldGenSparseMinableCluster(List<WeightedRandomBlock> resource, int clusterSize, Block block) {

		this(resource, clusterSize, fabricateList(block));
	}

	public WorldGenSparseMinableCluster(List<WeightedRandomBlock> resource, int clusterSize, List<WeightedRandomBlock> block) {

		cluster = resource;
		genClusterSize = clusterSize > 32 ? 32 : clusterSize;
		genBlock = block.toArray(new WeightedRandomBlock[block.size()]);
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {

		int blocks = genClusterSize;
		float f = rand.nextFloat() * (float) Math.PI;
		// despite naming, these are not exactly min/max. more like direction
		float yMin = (y + rand.nextInt(3)) - 2;
		float yMax = (y + rand.nextInt(3)) - 2;
		// { HACK: at 1 and 2 no ores are ever generated. by doing it this way,
		// 3 = 1/3rd clusters gen, 2 = 1/6, 1 = 1/12 allowing for much finer
		// grained rarity than the non-sparse version
		if (blocks == 1 && yMin > yMax) {
			++blocks;
		}
		if (blocks == 2 && f > (float) Math.PI * 0.5f) {
			++blocks;
		}
		// }
		float xMin = x + 8 + (MathHelper.sin(f) * blocks) / 8F;
		float xMax = x + 8 - (MathHelper.sin(f) * blocks) / 8F;
		float zMin = z + 8 + (MathHelper.cos(f) * blocks) / 8F;
		float zMax = z + 8 - (MathHelper.cos(f) * blocks) / 8F;

		// optimization so this subtraction doesn't occur every time in the loop
		xMax -= xMin;
		yMax -= yMin;
		zMax -= zMin;

		boolean r = false;
		for (int i = 0; i <= blocks; i++) {

			float xCenter = xMin + (xMax * i) / blocks;
			float yCenter = yMin + (yMax * i) / blocks;
			float zCenter = zMin + (zMax * i) / blocks;

			// preserved as nextDouble to ensure the rand gets ticked the same amount
			float size = ((float) rand.nextDouble() * blocks) / 16f;

			float hMod = ((MathHelper.sin((i * (float) Math.PI) / blocks) + 1f) * size + 1f) * .5f;
			float vMod = ((MathHelper.sin((i * (float) Math.PI) / blocks) + 1f) * size + 1f) * .5f;

			int xStart = MathHelper.floor_float(xCenter - hMod);
			int yStart = MathHelper.floor_float(yCenter - vMod);
			int zStart = MathHelper.floor_float(zCenter - hMod);

			int xStop = MathHelper.floor_float(xCenter + hMod);
			int yStop = MathHelper.floor_float(yCenter + vMod);
			int zStop = MathHelper.floor_float(zCenter + hMod);

			for (int blockX = xStart; blockX <= xStop; blockX++) {
				float xDistSq = ((blockX + .5f) - xCenter) / hMod;
				xDistSq *= xDistSq;
				if (xDistSq >= 1f) {
					continue;
				}

				for (int blockY = yStart; blockY <= yStop; blockY++) {
					float yDistSq = ((blockY + .5f) - yCenter) / vMod;
					yDistSq *= yDistSq;
					float xyDistSq = yDistSq + xDistSq;
					if (xyDistSq >= 1f) {
						continue;
					}

					for (int blockZ = zStart; blockZ <= zStop; blockZ++) {
						float zDistSq = ((blockZ + .5f) - zCenter) / hMod;
						zDistSq *= zDistSq;
						if (zDistSq + xyDistSq >= 1f) {
							continue;
						}

						r |= generateBlock(world, blockX, blockY, blockZ, genBlock, cluster);
					}
				}
			}
		}

		return r;
	}

}
