package cofh.lib.world;

import static cofh.lib.world.WorldGenMinableCluster.*;

import cofh.lib.util.WeightedRandomBlock;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenSmallTree extends WorldGenerator {

	private final List<WeightedRandomBlock> leaves;
	private final List<WeightedRandomBlock> trunk;
	private final WeightedRandomBlock[] genBlock;

	public WeightedRandomBlock[] genSurface = null;
	public int minHeight = 5;
	public int heightVariance = 3;
	public boolean treeChecks = true;
	public boolean leafVariance = true;
	public boolean relaxedGrowth = false;
	public boolean waterLoving = false;

	public WorldGenSmallTree(List<WeightedRandomBlock> resource, List<WeightedRandomBlock> leaf, List<WeightedRandomBlock> block) {

		trunk = resource;
		leaves = leaf;
		genBlock = block.toArray(new WeightedRandomBlock[block.size()]);
	}

	protected int getLeafRadius(int height, int level, boolean check) {

		if (check) {
			if (level >= 1 + height - 2) {
				return 2;
			} else {
				return relaxedGrowth ? 0 : 1;
			}
		}

		if (level >= 1 + height - 4) {
			return 1 - ((level - height) / 2);
		} else {
			return 0;
		}
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {

		int treeHeight = (heightVariance <= 1 ? 0 : rand.nextInt(heightVariance)) + minHeight;
		int worldHeight = world.getHeight();
		Block block;

		if (y + treeHeight + 1 <= worldHeight) {
			int xOffset;
			int yOffset;
			int zOffset;

			if (!canGenerateInBlock(world, x, y - 1, z, genSurface)) {
				return false;
			}

			if (y < worldHeight - treeHeight - 1) {
				if (treeChecks) {
					for (yOffset = y; yOffset <= y + 1 + treeHeight; ++yOffset) {

						int radius = getLeafRadius(treeHeight, yOffset - y, true);

						if (yOffset >= 0 & yOffset < worldHeight) {
							if (radius == 0) {
								block = world.getBlock(x, yOffset, z);
								if (!(block.isLeaves(world, x, yOffset, z) || block.isAir(world, x, yOffset, z) || block.isReplaceable(world, x, yOffset, z)
										|| block.canBeReplacedByLeaves(world, x, yOffset, z) || canGenerateInBlock(world, x, yOffset, z, genBlock))) {
									return false;
								}

								if (!waterLoving && yOffset >= y + 1) {
									radius = 1;
									for (xOffset = x - radius; xOffset <= x + radius; ++xOffset) {
										for (zOffset = z - radius; zOffset <= z + radius; ++zOffset) {
											block = world.getBlock(xOffset, yOffset, zOffset);

											if (block.getMaterial().isLiquid()) {
												return false;
											}
										}
									}
								}
							} else {
								for (xOffset = x - radius; xOffset <= x + radius; ++xOffset) {
									for (zOffset = z - radius; zOffset <= z + radius; ++zOffset) {
										block = world.getBlock(xOffset, yOffset, zOffset);

										if (!(block.isLeaves(world, xOffset, yOffset, zOffset) || block.isAir(world, xOffset, yOffset, zOffset)
												|| block.canBeReplacedByLeaves(world, xOffset, yOffset, zOffset) || canGenerateInBlock(world, xOffset, yOffset,
													zOffset, genBlock))) {
											return false;
										}
									}
								}
							}
						} else {
							return false;
						}
					}

					if (genSurface != null && !canGenerateInBlock(world, x, y - 1, z, genSurface)) {
						return false;
					}
					block = world.getBlock(x, y - 1, z);
					block.onPlantGrow(world, x, y - 1, z, x, y, z);
				}

				boolean r = false;

				for (yOffset = y; yOffset <= y + treeHeight; ++yOffset) {

					int var12 = yOffset - (y + treeHeight);
					int radius = getLeafRadius(treeHeight, yOffset - y, false);
					if (radius <= 0) {
						continue;
					}

					for (xOffset = x - radius; xOffset <= x + radius; ++xOffset) {
						int xPos = xOffset - x, t;
						xPos = (xPos + (t = xPos >> 31)) ^ t;

						for (zOffset = z - radius; zOffset <= z + radius; ++zOffset) {
							int zPos = zOffset - z;
							zPos = (zPos + (t = zPos >> 31)) ^ t;

							block = world.getBlock(xOffset, yOffset, zOffset);

							if (((xPos != radius | zPos != radius) || (!leafVariance || (rand.nextInt(2) != 0 && var12 != 0)))
									&& ((treeChecks ? block.isLeaves(world, xOffset, yOffset, zOffset) || block.isAir(world, xOffset, yOffset, zOffset)
											|| block.canBeReplacedByLeaves(world, xOffset, yOffset, zOffset) : false) || canGenerateInBlock(world, xOffset,
												yOffset, zOffset, genBlock))) {
								r |= generateBlock(world, xOffset, yOffset, zOffset, leaves);
							}
						}
					}
				}

				for (yOffset = 0; yOffset < treeHeight; ++yOffset) {
					block = world.getBlock(x, y + yOffset, z);

					if ((treeChecks ? block.isAir(world, x, y + yOffset, z) || block.isLeaves(world, x, y + yOffset, z)
							|| block.isReplaceable(world, x, y + yOffset, z) : false)
							|| canGenerateInBlock(world, x, yOffset + y, z, genBlock)) {
						r |= generateBlock(world, x, yOffset + y, z, trunk);
					}
				}

				return r;
			}
		}
		return false;
	}

}
