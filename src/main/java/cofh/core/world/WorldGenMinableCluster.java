package cofh.core.world;

import cofh.core.util.WeightedRandomBlock;
import cofh.core.util.numbers.ConstantProvider;
import cofh.core.util.numbers.INumberProvider;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGenMinableCluster extends WorldGenerator {

	public static List<WeightedRandomBlock> fabricateList(WeightedRandomBlock resource) {

		List<WeightedRandomBlock> list = new ArrayList<>();
		list.add(resource);
		return list;
	}

	public static List<WeightedRandomBlock> fabricateList(Block resource) {

		List<WeightedRandomBlock> list = new ArrayList<>();
		list.add(new WeightedRandomBlock(new ItemStack(resource, 1, 0)));
		return list;
	}

	private final List<WeightedRandomBlock> cluster;
	private final INumberProvider genClusterSize;
	private final WeightedRandomBlock[] genBlock;

	public WorldGenMinableCluster(ItemStack ore, int clusterSize) {

		this(new WeightedRandomBlock(ore), clusterSize);
	}

	public WorldGenMinableCluster(WeightedRandomBlock resource, int clusterSize) {

		this(fabricateList(resource), clusterSize);
	}

	public WorldGenMinableCluster(List<WeightedRandomBlock> resource, int clusterSize) {

		this(resource, clusterSize, Blocks.STONE);
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

		this(resource, new ConstantProvider(clusterSize), block);
	}

	public WorldGenMinableCluster(List<WeightedRandomBlock> resource, INumberProvider clusterSize, List<WeightedRandomBlock> block) {

		cluster = resource;
		genClusterSize = clusterSize;
		genBlock = block.toArray(new WeightedRandomBlock[block.size()]);
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		int blocks = MathHelper.clamp(genClusterSize.intValue(world, rand, pos), 1, 42);
		if (blocks < 4) { // HACK: at 1 and 2 no ores are ever generated. at 3 only 1/3 veins generate
			return generateTiny(world, rand, blocks, x, y, z);
		}
		float f = rand.nextFloat() * (float) Math.PI;
		// despite naming, these are not exactly min/max. more like direction
		float xMin = x + (MathHelper.sin(f) * blocks) / 8F;
		float xMax = x - (MathHelper.sin(f) * blocks) / 8F;
		float zMin = z + (MathHelper.cos(f) * blocks) / 8F;
		float zMax = z - (MathHelper.cos(f) * blocks) / 8F;
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
			float size = ((float) rand.nextDouble() * blocks) / 16F;

			float hMod = ((MathHelper.sin((i * (float) Math.PI) / blocks) + 1F) * size + 1F) * 0.5F;
			float vMod = ((MathHelper.sin((i * (float) Math.PI) / blocks) + 1F) * size + 1F) * 0.5F;

			int xStart = MathHelper.floor(xCenter - hMod);
			int yStart = MathHelper.floor(yCenter - vMod);
			int zStart = MathHelper.floor(zCenter - hMod);

			int xStop = MathHelper.floor(xCenter + hMod);
			int yStop = MathHelper.floor(yCenter + vMod);
			int zStop = MathHelper.floor(zCenter + hMod);

			for (int blockX = xStart; blockX <= xStop; blockX++) {
				float xDistSq = ((blockX + .5F) - xCenter) / hMod;
				xDistSq *= xDistSq;
				if (xDistSq >= 1F) {
					continue;
				}
				for (int blockY = yStart; blockY <= yStop; blockY++) {
					float yDistSq = ((blockY + .5F) - yCenter) / vMod;
					yDistSq *= yDistSq;
					float xyDistSq = yDistSq + xDistSq;
					if (xyDistSq >= 1F) {
						continue;
					}
					for (int blockZ = zStart; blockZ <= zStop; blockZ++) {
						float zDistSq = ((blockZ + .5F) - zCenter) / hMod;
						zDistSq *= zDistSq;
						if (zDistSq + xyDistSq >= 1F) {
							continue;
						}
						r |= generateBlock(world, blockX, blockY, blockZ, genBlock, cluster);
					}
				}
			}
		}
		return r;
	}

	public boolean generateTiny(World world, Random random, int clusterSize, int x, int y, int z) {

		boolean r = generateBlock(world, x, y, z, genBlock, cluster);
		// not <=; generating up to clusterSize blocks
		for (int i = 1; i < clusterSize; i++) {
			int d0 = x + random.nextInt(2);
			int d1 = y + random.nextInt(2);
			int d2 = z + random.nextInt(2);

			r |= generateBlock(world, d0, d1, d2, genBlock, cluster);
		}
		return r;
	}

	@Deprecated
	public static boolean canGenerateInBlock(World world, int x, int y, int z, WeightedRandomBlock[] mat) {

		return canGenerateInBlock(world, new BlockPos(x, y, z), mat);
	}

	public static boolean canGenerateInBlock(World world, BlockPos pos, WeightedRandomBlock[] mat) {

		if (mat == null || mat.length == 0) {
			return true;
		}

		IBlockState state = world.getBlockState(pos);
		for (int j = 0, e = mat.length; j < e; ++j) {
			WeightedRandomBlock genBlock = mat[j];
			if ((-1 == genBlock.metadata || genBlock.metadata == state.getBlock().getMetaFromState(state)) && (state.getBlock().isReplaceableOreGen(state, world, pos, BlockMatcher.forBlock(genBlock.block)) || state.getBlock().isAssociatedBlock(genBlock.block))) {
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
		return ore != null && world.setBlockState(new BlockPos(x, y, z), ore.getState(), 2);
	}

	public static WeightedRandomBlock selectBlock(World world, List<WeightedRandomBlock> o) {

		int size = o.size();
		if (size == 0) {
			return null;
		}
		if (size > 1) {
			return WeightedRandom.getRandomItem(world.rand, o);
		}
		return o.get(0);
	}

}
