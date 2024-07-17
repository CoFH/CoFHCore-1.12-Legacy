package cofh.lib.world;

import static cofh.lib.world.WorldGenMinableCluster.*;

import cofh.lib.util.WeightedRandomBlock;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenAdvLakes extends WorldGenerator {

	private static final List<WeightedRandomBlock> GAP_BLOCK = Arrays.asList(new WeightedRandomBlock(Blocks.air, 0));
	private final List<WeightedRandomBlock> cluster;
	private final WeightedRandomBlock[] genBlock;
	public List<WeightedRandomBlock> outlineBlock = null;
	public List<WeightedRandomBlock> gapBlock = GAP_BLOCK;
	public boolean solidOutline = false;
	public boolean totalOutline = false;
	public int width = 16;
	public int height = 8;

	public WorldGenAdvLakes(List<WeightedRandomBlock> resource, List<WeightedRandomBlock> block) {

		cluster = resource;
		if (block == null) {
			genBlock = null;
		} else {
			genBlock = block.toArray(new WeightedRandomBlock[block.size()]);
		}
	}

	@Override
	public boolean generate(World world, Random rand, int xStart, int yStart, int zStart) {

		int widthOff = width / 2;
		xStart -= widthOff;
		zStart -= widthOff;

		int heightOff = height / 2 + 1;

		while (yStart > heightOff && world.isAirBlock(xStart, yStart, zStart)) {
			--yStart;
		}
		--heightOff;
		if (yStart <= heightOff) {
			return false;
		}

		yStart -= heightOff;
		boolean[] spawnBlock = new boolean[width * width * height];

		int W = width - 1, H = height - 1;

		for (int i = 0, e = rand.nextInt(4) + 4; i < e; ++i) {
			double xSize = rand.nextDouble() * 6.0D + 3.0D;
			double ySize = rand.nextDouble() * 4.0D + 2.0D;
			double zSize = rand.nextDouble() * 6.0D + 3.0D;
			double xCenter = rand.nextDouble() * (width - xSize - 2.0D) + 1.0D + xSize / 2.0D;
			double yCenter = rand.nextDouble() * (height - ySize - 4.0D) + 2.0D + ySize / 2.0D;
			double zCenter = rand.nextDouble() * (width - zSize - 2.0D) + 1.0D + zSize / 2.0D;

			for (int x = 1; x < W; ++x) {
				for (int z = 1; z < W; ++z) {
					for (int y = 1; y < H; ++y) {
						double xDist = (x - xCenter) / (xSize / 2.0D);
						double yDist = (y - yCenter) / (ySize / 2.0D);
						double zDist = (z - zCenter) / (zSize / 2.0D);
						double dist = xDist * xDist + yDist * yDist + zDist * zDist;

						if (dist < 1.0D) {
							spawnBlock[(x * width + z) * height + y] = true;
						}
					}
				}
			}
		}

		int x;
		int y;
		int z;

		for (x = 0; x < width; ++x) {
			for (z = 0; z < width; ++z) {
				for (y = 0; y < height; ++y) {
					boolean flag = spawnBlock[(x * width + z) * height + y]
							|| ((x < W && spawnBlock[((x + 1) * width + z) * height + y]) || (x > 0 && spawnBlock[((x - 1) * width + z) * height + y])
									|| (z < W && spawnBlock[(x * width + (z + 1)) * height + y]) || (z > 0 && spawnBlock[(x * width + (z - 1)) * height + y])
									|| (y < H && spawnBlock[(x * width + z) * height + (y + 1)]) || (y > 0 && spawnBlock[(x * width + z) * height + (y - 1)]));

					if (flag) {
						if (y >= heightOff) {
							Material material = world.getBlock(xStart + x, yStart + y, zStart + z).getMaterial();
							if (material.isLiquid()) {
								return false;
							}
						} else {
							if (!canGenerateInBlock(world, xStart + x, yStart + y, zStart + z, genBlock)) {
								return false;
							}
						}
					}
				}
			}
		}

		for (x = 0; x < width; ++x) {
			for (z = 0; z < width; ++z) {
				for (y = 0; y < height; ++y) {
					if (spawnBlock[(x * width + z) * height + y]) {
						if (y < heightOff) {
							generateBlock(world, xStart + x, yStart + y, zStart + z, genBlock, cluster);
						} else if (canGenerateInBlock(world, xStart + x, yStart + y, zStart + z, genBlock)) {
							generateBlock(world, xStart + x, yStart + y, zStart + z, gapBlock);
						}
					}
				}
			}
		}

		for (x = 0; x < width; ++x) {
			for (z = 0; z < width; ++z) {
				for (y = 0; y < height; ++y) {
					if (spawnBlock[(x * width + z) * height + y] && world.getBlock(xStart + x, yStart + y - 1, zStart + z).equals(Blocks.dirt)
							&& world.getSavedLightValue(EnumSkyBlock.Sky, xStart + x, yStart + y, zStart + z) > 0) {
						BiomeGenBase bgb = world.getBiomeGenForCoords(xStart + x, zStart + z);
						world.setBlock(xStart + x, yStart + y - 1, zStart + z, bgb.topBlock, bgb.field_150604_aj, 2);
					}
				}
			}
		}

		if (outlineBlock != null) {
			for (x = 0; x < width; ++x) {
				for (z = 0; z < width; ++z) {
					for (y = 0; y < height; ++y) {
						boolean flag = !spawnBlock[(x * width + z) * height + y]
								&& ((x < W && spawnBlock[((x + 1) * width + z) * height + y]) || (x > 0 && spawnBlock[((x - 1) * width + z) * height + y])
										|| (z < W && spawnBlock[(x * width + (z + 1)) * height + y])
										|| (z > 0 && spawnBlock[(x * width + (z - 1)) * height + y])
										|| (y < H && spawnBlock[(x * width + z) * height + (y + 1)]) || (y > 0 && spawnBlock[(x * width + z) * height + (y - 1)]));

						if (flag && (solidOutline | y < heightOff || rand.nextInt(2) != 0)
								&& (totalOutline || world.getBlock(xStart + x, yStart + y, zStart + z).getMaterial().isSolid())) {
							generateBlock(world, xStart + x, yStart + y, zStart + z, outlineBlock);
						}
					}
				}
			}
		}

		return true;

	}
}
