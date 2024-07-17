package cofh.lib.world;

import static cofh.lib.world.WorldGenMinableCluster.generateBlock;

import cofh.lib.util.WeightedRandomBlock;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenMinablePlate extends WorldGenerator {

	private final List<WeightedRandomBlock> cluster;
	private final WeightedRandomBlock[] genBlock;
	private final int radius;
	public byte height = 1;
	public byte variation = 2;
	public boolean slim = false;

	public WorldGenMinablePlate(List<WeightedRandomBlock> resource, int clusterSize, List<WeightedRandomBlock> block) {

		cluster = resource;
		radius = clusterSize;
		genBlock = block.toArray(new WeightedRandomBlock[block.size()]);
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {

		++y;
		int size = radius;
		if (radius > variation + 1) {
			size = rand.nextInt(radius - variation) + variation;
		}
		final int dist = size * size;
		byte height = this.height;

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

}
