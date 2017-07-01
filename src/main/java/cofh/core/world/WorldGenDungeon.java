//package cofh.lib.world;
//TODO The entire loot system changed in 1.10
//
//import static cofh.lib.world.WorldGenMinableCluster.*;
//import static java.lang.Math.abs;
//
//import cofh.core.util.WeightedRandomBlock;
//import cofh.core.util.WeightedRandomNBTTag;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Random;
//
//import net.minecraft.init.Blocks;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.tileentity.TileEntityChest;
//import net.minecraft.tileentity.TileEntityMobSpawner;
//import net.minecraft.util.WeightedRandom;
//import net.minecraft.util.WeightedRandomChestContent;
//import net.minecraft.world.World;
//import net.minecraft.world.gen.feature.WorldGenerator;
//import net.minecraftforge.common.ChestGenHooks;
//import net.minecraftforge.common.DungeonHooks.DungeonMob;
//
//public class WorldGenDungeon extends WorldGenerator {
//
//	private final List<WeightedRandomBlock> walls;
//	private final WeightedRandomBlock[] genBlock;
//	private final WeightedRandomNBTTag[] spawners;
//	public int minWidthX = 2, maxWidthX = 3;
//	public int minWidthZ = 2, maxWidthZ = 3;
//	public int minHeight = 3, maxHeight = 3;
//	public int minHoles = 1, maxHoles = 5;
//	public int maxChests = 2, maxChestTries = 3;
//	public List<DungeonMob> lootTables = Arrays.asList(new DungeonMob(100, ChestGenHooks.DUNGEON_CHEST));
//	public List<WeightedRandomBlock> floor;
//
//	public WorldGenDungeon(List<WeightedRandomBlock> blocks, List<WeightedRandomBlock> material, List<WeightedRandomNBTTag> mobs) {
//
//		walls = blocks;
//		floor = walls;
//		spawners = mobs.toArray(new WeightedRandomNBTTag[mobs.size()]);
//		genBlock = material.toArray(new WeightedRandomBlock[material.size()]);
//	}
//
//	@Override
//	public boolean generate(World world, Random rand, int xStart, int yStart, int zStart) {
//
//		if (yStart <= 2) {
//			return false;
//		}
//
//		int height = nextInt(rand, maxHeight - minHeight + 1) + minHeight;
//		int xWidth = nextInt(rand, maxWidthX - minWidthX + 1) + minWidthX;
//		int zWidth = nextInt(rand, maxWidthZ - minWidthZ + 1) + minWidthZ;
//		int holes = 0;
//		int x, y, z;
//
//		int floor = yStart - 1, ceiling = yStart + height + 1;
//
//		for (x = xStart - xWidth - 1; x <= xStart + xWidth + 1; ++x) {
//			for (z = zStart - zWidth - 1; z <= zStart + zWidth + 1; ++z) {
//				for (y = floor; y <= ceiling; ++y) {
//
//					if (y == floor && !canGenerateInBlock(world, x, y, z, genBlock)) {
//						return false;
//					}
//
//					if (y == ceiling && !canGenerateInBlock(world, x, y, z, genBlock)) {
//						return false;
//					}
//
//					if ((abs(x - xStart) == xWidth + 1 || abs(z - zStart) == zWidth + 1) && y == yStart && world.isAirBlock(x, y, z)
//							&& world.isAirBlock(x, y + 1, z)) {
//						++holes;
//					}
//				}
//			}
//		}
//
//		if (holes < minHoles || holes > maxHoles) {
//			return false;
//		}
//
//		NBTTagCompound tag = (NBTTagCompound) ((WeightedRandomNBTTag) WeightedRandom.getRandomItem(rand, spawners)).tag;
//		ChestGenHooks table = ChestGenHooks.getInfo(((DungeonMob) WeightedRandom.getRandomItem(rand, lootTables)).type);
//
//		for (x = xStart - xWidth - 1; x <= xStart + xWidth + 1; ++x) {
//			for (z = zStart - zWidth - 1; z <= zStart + zWidth + 1; ++z) {
//				for (y = yStart + height; y >= floor; --y) {
//
//					l: if (y != floor) {
//						if ((abs(x - xStart) != xWidth + 1 && abs(z - zStart) != zWidth + 1)) {
//							world.setBlockToAir(x, y, z);
//						} else if (y >= 0 && !canGenerateInBlock(world, x, y - 1, z, genBlock)) {
//							world.setBlockToAir(x, y, z);
//						} else {
//							break l;
//						}
//						continue;
//					}
//					if (canGenerateInBlock(world, x, y, z, genBlock)) {
//						if (y == floor) {
//							generateBlock(world, x, y, z, this.floor);
//						} else {
//							generateBlock(world, x, y, z, walls);
//						}
//					}
//				}
//			}
//		}
//
//		for (int i = maxChests; i-- > 0;) {
//			for (int j = maxChestTries; j-- > 0;) {
//				x = xStart + nextInt(rand, xWidth * 2 + 1) - xWidth;
//				z = zStart + nextInt(rand, zWidth * 2 + 1) - zWidth;
//
//				if (world.isAirBlock(x, yStart, z)) {
//					int walls = 0;
//
//					if (isWall(world, x - 1, yStart, z)) {
//						++walls;
//					}
//
//					if (isWall(world, x + 1, yStart, z)) {
//						++walls;
//					}
//
//					if (isWall(world, x, yStart, z - 1)) {
//						++walls;
//					}
//
//					if (isWall(world, x, yStart, z + 1)) {
//						++walls;
//					}
//
//					if (walls >= 1 && walls <= 2) {
//						world.setBlock(x, yStart, z, Blocks.chest, 0, 2);
//						TileEntityChest chest = (TileEntityChest) world.getTileEntity(x, yStart, z);
//
//						if (chest != null) {
//							WeightedRandomChestContent.generateChestContents(rand, table.getItems(rand), chest, table.getCount(rand));
//						}
//
//						break;
//					}
//				}
//			}
//		}
//
//		world.setBlock(xStart, yStart, zStart, Blocks.mob_spawner, 0, 2);
//		TileEntityMobSpawner spawner = (TileEntityMobSpawner) world.getTileEntity(xStart, yStart, zStart);
//
//		if (spawner != null) {
//			spawner.func_145881_a().readFromNBT(tag);
//		} else {
//			System.err.println("Failed to fetch mob spawner entity at (" + xStart + ", " + yStart + ", " + zStart + ")");
//		}
//
//		return true;
//	}
//
//	private static int nextInt(Random rand, int v) {
//
//		if (v <= 1) {
//			return 0;
//		}
//		return rand.nextInt(v);
//	}
//
//	private boolean isWall(World world, int x, int y, int z) {
//
//		int metadata = world.getBlockMetadata(x, y, z);
//		return WeightedRandomBlock.isBlockContained(world.getBlock(x, y, z), metadata, walls);
//	}
//
//}
