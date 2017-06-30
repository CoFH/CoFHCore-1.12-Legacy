package cofh.lib.world;

import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.numbers.ConstantProvider;
import cofh.lib.util.numbers.INumberProvider;
import cofh.lib.util.numbers.UniformRandomProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.List;
import java.util.Random;

import static cofh.lib.world.WorldGenMinableCluster.generateBlock;

public class WorldGenMinablePlate extends WorldGenerator {

	private final List<WeightedRandomBlock> cluster;
	private final WeightedRandomBlock[] genBlock;
	private final INumberProvider radius;
	private INumberProvider height;
	private boolean slim;

	public WorldGenMinablePlate(List<WeightedRandomBlock> resource, int clusterSize, List<WeightedRandomBlock> block) {

		this(resource, new UniformRandomProvider(clusterSize, clusterSize + 2), block);
	}

	public WorldGenMinablePlate(List<WeightedRandomBlock> resource, INumberProvider clusterSize, List<WeightedRandomBlock> block) {

		cluster = resource;
		radius = clusterSize;
		genBlock = block.toArray(new WeightedRandomBlock[block.size()]);
		setHeight(1).setSlim(false);
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		++y;
		int size = radius.intValue(world, rand, pos);
		final int dist = size * size;
		int height = this.height.intValue(world, rand, pos);

		boolean r = false;
		for (int posX = x - size; posX <= x + size; ++posX) {
			int xDist = posX - x;
			xDist *= xDist;
			for (int posZ = z - size; posZ <= z + size; ++posZ) {
				int zSize = posZ - z;

				if (zSize * zSize + xDist <= dist) {
					for (int posY = y - height; slim ? posY < y + height : posY <= y + height; ++posY) {
						r |= generateBlock(world, posX, posY, posZ, genBlock, cluster);
					}
				}
			}
		}

		return r;
	}

	public WorldGenMinablePlate setSlim(boolean slim) {

		this.slim = slim;
		return this;
	}

	public WorldGenMinablePlate setHeight(int height) {

		this.height = new ConstantProvider(height);
		return this;
	}

	public WorldGenMinablePlate setHeight(INumberProvider height) {

		this.height = height;
		return this;
	}

}
