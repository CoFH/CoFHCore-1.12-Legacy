package cofh.lib.world;

import cofh.lib.util.WeightedRandomBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenMinableCluster extends WorldGenerator {

	public static final List<WeightedRandomBlock> fabricateList(WeightedRandomBlock resource) {

		List<WeightedRandomBlock> list = new ArrayList<WeightedRandomBlock>();
		list.add(resource);
		return list;
	}

	public static final List<WeightedRandomBlock> fabricateList(Block resource) {

		List<WeightedRandomBlock> list = new ArrayList<WeightedRandomBlock>();
		list.add(new WeightedRandomBlock(new ItemStack(resource, 1, 0)));
		return list;
	}

	private final List<WeightedRandomBlock> cluster;
	private final int genClusterSize;
	private final WeightedRandomBlock[] genBlock;

	public WorldGenMinableCluster(ItemStack ore, int clusterSize) {

		this(new WeightedRandomBlock(ore), clusterSize);
	}

	public WorldGenMinableCluster(WeightedRandomBlock resource, int clusterSize) {

		this(fabricateList(resource), clusterSize);
	}

	public WorldGenMinableCluster(List<WeightedRandomBlock> resource, int clusterSize) {

		this(resource, clusterSize, Blocks.stone);
	}

	public WorldGenMinableCluster(ItemStack ore, int clusterSize, Block block) {

		this(new WeightedRandomBlock(ore, 1), clusterSize, block);
	}

	public WorldGenMinableCluster(WeightedRandomBlock resource, int clusterSize, Block block) {

		this(fabricateList(resource), clusterSize, block);
	}

	public WorldGenMinableCluster(List<WeightedRandomBlock> resource, int clusterSize, Block block) {

		this(resource, clusterSize, fabricateList(block));
	}

	public WorldGenMinableCluster(List<WeightedRandomBlock> resource, int clusterSize, List<WeightedRandomBlock> block) {

		cluster = resource;
		genClusterSize = clusterSize > 32 ? 32 : clusterSize;
		genBlock = block.toArray(new WeightedRandomBlock[block.size()]);
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {

		int blocks = genClusterSize;
		if (blocks < 4) { // HACK: at 1 and 2 no ores are ever generated. at 3 only 1/3 veins generate
			return generateTiny(world, rand, x, y, z);
		}
		float f = rand.nextFloat() * (float) Math.PI;
		// despite naming, these are not exactly min/max. more like direction
		float xMin = x + 8 + (MathHelper.sin(f) * blocks) / 8F;
		float xMax = x + 8 - (MathHelper.sin(f) * blocks) / 8F;
		float zMin = z + 8 + (MathHelper.cos(f) * blocks) / 8F;
		float zMax = z + 8 - (MathHelper.cos(f) * blocks) / 8F;
		float yMin = (y + rand.nextInt(3)) - 2;
		float yMax = (y + rand.nextInt(3)) - 2;

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

	public boolean generateTiny(World world, Random random, int x, int y, int z) {

		boolean r = false;
		// not <=; generating up to clusterSize blocks
		for (int i = 0; i < genClusterSize; i++) {
			int d0 = x + random.nextInt(2);
			int d1 = y + random.nextInt(2);
			int d2 = z + random.nextInt(2);

			r |= generateBlock(world, d0, d1, d2, genBlock, cluster);
		}
		return r;
	}

	public static boolean canGenerateInBlock(World world, int x, int y, int z, WeightedRandomBlock[] mat) {

		if (mat == null || mat.length == 0) {
			return true;
		}

		Block block = world.getBlock(x, y, z);
		for (int j = 0, e = mat.length; j < e; ++j) {
			WeightedRandomBlock genBlock = mat[j];
			if ((-1 == genBlock.metadata || genBlock.metadata == world.getBlockMetadata(x, y, z))
					&& (block.isReplaceableOreGen(world, x, y, z, genBlock.block) || block.isAssociatedBlock(genBlock.block))) {
				return true;
			}
		}
		return false;
	}

	public static boolean generateBlock(World world, int x, int y, int z, WeightedRandomBlock[] mat, List<WeightedRandomBlock> o) {

		if (mat == null || mat.length == 0) {
			return generateBlock(world, x, y, z, o);
		}

		if (canGenerateInBlock(world, x, y, z, mat)) {
			return generateBlock(world, x, y, z, o);
		}
		return false;
	}

	public static boolean generateBlock(World world, int x, int y, int z, List<WeightedRandomBlock> o) {

		WeightedRandomBlock ore = selectBlock(world, o);
		if (ore == null) {
			return false;
		}
		return world.setBlock(x, y, z, ore.block, ore.metadata, 2);
	}

	public static WeightedRandomBlock selectBlock(World world, List<WeightedRandomBlock> o) {

		int size = o.size();
		if (size == 0) {
			return null;
		}
		if (size > 1) {
			return (WeightedRandomBlock) WeightedRandom.getRandomItem(world.rand, o);
		}
		return o.get(0);
	}

}
