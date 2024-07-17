package cofh.lib.util.helpers;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Contains various helper functions to assist with {@link Block} and Block-related manipulation and interaction.
 *
 * @author King Lemming
 *
 */
public final class BlockHelper {

	private BlockHelper() {

	}

	public static int MAX_ID = 1024;
	public static byte[] rotateType = new byte[MAX_ID];
	public static final int[][] SIDE_COORD_MOD = { { 0, -1, 0 }, { 0, 1, 0 }, { 0, 0, -1 }, { 0, 0, 1 }, { -1, 0, 0 }, { 1, 0, 0 } };
	public static float[][] SIDE_COORD_AABB = { { 1, -2, 1 }, { 1, 2, 1 }, { 1, 1, 1 }, { 1, 1, 2 }, { 1, 1, 1 }, { 2, 1, 1 } };
	public static final byte[] SIDE_LEFT = { 4, 5, 5, 4, 2, 3 };
	public static final byte[] SIDE_RIGHT = { 5, 4, 4, 5, 3, 2 };
	public static final byte[] SIDE_OPPOSITE = { 1, 0, 3, 2, 5, 4 };
	public static final byte[] SIDE_ABOVE = { 3, 2, 1, 1, 1, 1 };
	public static final byte[] SIDE_BELOW = { 2, 3, 0, 0, 0, 0 };

	// These assume facing is towards negative - looking AT side 1, 3, or 5.
	public static final byte[] ROTATE_CLOCK_Y = { 0, 1, 4, 5, 3, 2 };
	public static final byte[] ROTATE_CLOCK_Z = { 5, 4, 2, 3, 0, 1 };
	public static final byte[] ROTATE_CLOCK_X = { 2, 3, 1, 0, 4, 5 };

	public static final byte[] ROTATE_COUNTER_Y = { 0, 1, 5, 4, 2, 3 };
	public static final byte[] ROTATE_COUNTER_Z = { 4, 5, 2, 3, 1, 0 };
	public static final byte[] ROTATE_COUNTER_X = { 3, 2, 0, 1, 4, 5 };

	public static final byte[] INVERT_AROUND_Y = { 0, 1, 3, 2, 5, 4 };
	public static final byte[] INVERT_AROUND_Z = { 1, 0, 2, 3, 5, 4 };
	public static final byte[] INVERT_AROUND_X = { 1, 0, 3, 2, 4, 5 };

	// Map which gives relative Icon to use on a block which can be placed on any side.
	public static final byte[][] ICON_ROTATION_MAP = new byte[6][];

	static {
		ICON_ROTATION_MAP[0] = new byte[] { 0, 1, 2, 3, 4, 5 };
		ICON_ROTATION_MAP[1] = new byte[] { 1, 0, 2, 3, 4, 5 };
		ICON_ROTATION_MAP[2] = new byte[] { 3, 2, 0, 1, 4, 5 };
		ICON_ROTATION_MAP[3] = new byte[] { 3, 2, 1, 0, 5, 4 };
		ICON_ROTATION_MAP[4] = new byte[] { 3, 2, 5, 4, 0, 1 };
		ICON_ROTATION_MAP[5] = new byte[] { 3, 2, 4, 5, 1, 0 };
	}

	public static final class RotationType {

		public static final int PREVENT = -1;
		public static final int FOUR_WAY = 1;
		public static final int SIX_WAY = 2;
		public static final int RAIL = 3;
		public static final int PUMPKIN = 4;
		public static final int STAIRS = 5;
		public static final int REDSTONE = 6;
		public static final int LOG = 7;
		public static final int SLAB = 8;
		public static final int CHEST = 9;
		public static final int LEVER = 10;
		public static final int SIGN = 11;
		
		private RotationType() {
			
		}
	}

	static { // TODO: review which of these can be removed in favor of the vanilla handler
		rotateType[Block.getIdFromBlock(Blocks.bed)] = RotationType.PREVENT;

		rotateType[Block.getIdFromBlock(Blocks.stone_slab)] = RotationType.SLAB;
		rotateType[Block.getIdFromBlock(Blocks.wooden_slab)] = RotationType.SLAB;

		rotateType[Block.getIdFromBlock(Blocks.rail)] = RotationType.RAIL;
		rotateType[Block.getIdFromBlock(Blocks.golden_rail)] = RotationType.RAIL;
		rotateType[Block.getIdFromBlock(Blocks.detector_rail)] = RotationType.RAIL;
		rotateType[Block.getIdFromBlock(Blocks.activator_rail)] = RotationType.RAIL;

		rotateType[Block.getIdFromBlock(Blocks.pumpkin)] = RotationType.PUMPKIN;
		rotateType[Block.getIdFromBlock(Blocks.lit_pumpkin)] = RotationType.PUMPKIN;

		rotateType[Block.getIdFromBlock(Blocks.furnace)] = RotationType.FOUR_WAY;
		rotateType[Block.getIdFromBlock(Blocks.lit_furnace)] = RotationType.FOUR_WAY;
		rotateType[Block.getIdFromBlock(Blocks.ender_chest)] = RotationType.FOUR_WAY;

		rotateType[Block.getIdFromBlock(Blocks.trapped_chest)] = RotationType.CHEST;
		rotateType[Block.getIdFromBlock(Blocks.chest)] = RotationType.CHEST;

		rotateType[Block.getIdFromBlock(Blocks.dispenser)] = RotationType.SIX_WAY;
		rotateType[Block.getIdFromBlock(Blocks.sticky_piston)] = RotationType.SIX_WAY;
		rotateType[Block.getIdFromBlock(Blocks.piston)] = RotationType.SIX_WAY;
		rotateType[Block.getIdFromBlock(Blocks.hopper)] = RotationType.SIX_WAY;
		rotateType[Block.getIdFromBlock(Blocks.dropper)] = RotationType.SIX_WAY;

		rotateType[Block.getIdFromBlock(Blocks.unpowered_repeater)] = RotationType.REDSTONE;
		rotateType[Block.getIdFromBlock(Blocks.unpowered_comparator)] = RotationType.REDSTONE;
		rotateType[Block.getIdFromBlock(Blocks.powered_repeater)] = RotationType.REDSTONE;
		rotateType[Block.getIdFromBlock(Blocks.powered_comparator)] = RotationType.REDSTONE;

		rotateType[Block.getIdFromBlock(Blocks.lever)] = RotationType.LEVER;

		rotateType[Block.getIdFromBlock(Blocks.standing_sign)] = RotationType.SIGN;

		rotateType[Block.getIdFromBlock(Blocks.oak_stairs)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.stone_stairs)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.brick_stairs)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.stone_brick_stairs)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.nether_brick_stairs)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.sandstone_stairs)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.spruce_stairs)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.birch_stairs)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.jungle_stairs)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.quartz_stairs)] = RotationType.STAIRS;
	}

	public static int getMicroBlockAngle(int side, float hitX, float hitY, float hitZ) {

		int direction = side ^ 1;
		float degreeCenter = 0.32f / 2;

		float x = 0, y = 0;
		switch (side >> 1) {
		case 0:
			x = hitX;
			y = hitZ;
			break;
		case 1:
			x = hitX;
			y = hitY;
			break;
		case 2:
			x = hitY;
			y = hitZ;
			break;
		}
		x -= .5f;
		y -= .5f;

		if (x * x + y * y > degreeCenter * degreeCenter) {

			int a = (int) ((Math.atan2(x, y) + Math.PI) * 4 / Math.PI);
			a = ++a & 7;
			switch (a >> 1) {
			case 0:
			case 4:
				direction = 2;
				break;
			case 1:
				direction = 4;
				break;
			case 2:
				direction = 3;
				break;
			case 3:
				direction = 5;
				break;
			}
		}
		return direction;
	}

	public static ForgeDirection getMicroBlockAngle(ForgeDirection side, float hitX, float hitY, float hitZ) {

		return ForgeDirection.VALID_DIRECTIONS[getMicroBlockAngle(side.ordinal(), hitX, hitY, hitZ)];
	}

	public static int getHighestY(World world, int x, int z) {

		return world.getChunkFromBlockCoords(x, z).getTopFilledSegment() + 16;
	}

	public static int getSurfaceBlockY(World world, int x, int z) {

		int y = world.getChunkFromBlockCoords(x, z).getTopFilledSegment() + 16;

		Block block;
		do {
			if (--y < 0) {
				break;
			}
			block = world.getBlock(x, y, z);
		} while (block.isAir(world, x, y, z) || block.isReplaceable(world, x, y, z) || block.isLeaves(world, x, y, z) || block.isFoliage(world, x, y, z)
				|| block.canBeReplacedByLeaves(world, x, y, z));
		return y;
	}

	public static int getTopBlockY(World world, int x, int z) {

		int y = world.getChunkFromBlockCoords(x, z).getTopFilledSegment() + 16;

		Block block;
		do {
			if (--y < 0) {
				break;
			}
			block = world.getBlock(x, y, z);
		} while (block.isAir(world, x, y, z));
		return y;
	}

	public static MovingObjectPosition getCurrentMovingObjectPosition(EntityPlayer player, double distance, boolean fluid) {

		Vec3 posVec = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
		Vec3 lookVec = player.getLook(1);
		posVec.yCoord += player.getEyeHeight();
		lookVec = posVec.addVector(lookVec.xCoord * distance, lookVec.yCoord * distance, lookVec.zCoord * distance);
		return player.worldObj.rayTraceBlocks(posVec, lookVec, fluid);
	}

	public static MovingObjectPosition getCurrentMovingObjectPosition(EntityPlayer player, double distance) {

		return getCurrentMovingObjectPosition(player, distance, false);
	}

	public static MovingObjectPosition getCurrentMovingObjectPosition(EntityPlayer player, boolean fluid) {

		return getCurrentMovingObjectPosition(player, player.capabilities.isCreativeMode ? 5.0F : 4.5F, fluid);
	}

	public static MovingObjectPosition getCurrentMovingObjectPosition(EntityPlayer player) {

		return getCurrentMovingObjectPosition(player, player.capabilities.isCreativeMode ? 5.0F : 4.5F, false);
	}

	public static int getCurrentMousedOverSide(EntityPlayer player) {

		MovingObjectPosition mouseOver = getCurrentMovingObjectPosition(player);
		return mouseOver == null ? 0 : mouseOver.sideHit;
	}

	public static int determineXZPlaceFacing(EntityLivingBase living) {

		int quadrant = MathHelper.floor_double(living.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		switch (quadrant) {
		case 0:
			return 2;
		case 1:
			return 5;
		case 2:
			return 3;
		case 3:
			return 4;
		}
		return 3;
	}

	public static boolean isEqual(Block blockA, Block blockB) {

		if (blockA == blockB) {
			return true;
		}
		if (blockA == null | blockB == null) {
			return false;
		}
		return blockA.equals(blockB) || blockA.isAssociatedBlock(blockB);
	}

	/* UNSAFE Tile Entity Retrieval */
	// public static TileEntity getAdjacentTileEntityUnsafe(World world, int x, int y, int z, ForgeDirection dir) {
	//
	// if (world == null) {
	// return null;
	// }
	// Chunk chunk = world.getChunkFromBlockCoords(x + dir.offsetX, z + dir.offsetZ);
	// return chunk == null ? null : chunk.getChunkBlockTileEntityUnsafe((x + dir.offsetX) & 0xF, y + dir.offsetY, (z + dir.offsetZ) & 0xF);
	// }
	//
	// public static TileEntity getAdjacentTileEntityUnsafe(World world, int x, int y, int z, int side) {
	//
	// return world == null ? null : getAdjacentTileEntityUnsafe(world, x, y, z, ForgeDirection.values()[side]);
	// }
	//
	// public static TileEntity getAdjacentTileEntityUnsafe(TileEntity refTile, ForgeDirection dir) {
	//
	// return refTile == null ? null : getAdjacentTileEntityUnsafe(refTile.worldObj, refTile.xCoord, refTile.yCoord, refTile.zCoord, dir);
	// }
	//
	// public static TileEntity getAdjacentTileEntityUnsafe(TileEntity refTile, int side) {
	//
	// return refTile == null ? null : getAdjacentTileEntityUnsafe(refTile.worldObj, refTile.xCoord, refTile.yCoord, refTile.zCoord,
	// ForgeDirection.values()[side]);
	// }

	public static Block getAdjacentBlock(World world, int x, int y, int z, ForgeDirection dir) {

		x += dir.offsetX;
		y += dir.offsetY;
		z += dir.offsetZ;
		return world == null || !world.blockExists(x, y, z) ? Blocks.air : world.getBlock(x, y, z);
	}

	public static Block getAdjacentBlock(World world, int x, int y, int z, int side) {

		return world == null ? Blocks.air : getAdjacentBlock(world, x, y, z, ForgeDirection.getOrientation(side));
	}

	/* Safe Tile Entity Retrieval */
	public static TileEntity getAdjacentTileEntity(World world, int x, int y, int z, ForgeDirection dir) {

		x += dir.offsetX;
		y += dir.offsetY;
		z += dir.offsetZ;
		return world == null || !world.blockExists(x, y, z) ? null : world.getTileEntity(x, y, z);
	}

	public static TileEntity getAdjacentTileEntity(World world, int x, int y, int z, int side) {

		return world == null ? null : getAdjacentTileEntity(world, x, y, z, ForgeDirection.getOrientation(side));
	}

	public static TileEntity getAdjacentTileEntity(TileEntity refTile, ForgeDirection dir) {

		return refTile == null ? null : getAdjacentTileEntity(refTile.getWorldObj(), refTile.xCoord, refTile.yCoord, refTile.zCoord, dir);
	}

	public static TileEntity getAdjacentTileEntity(TileEntity refTile, int side) {

		return refTile == null ? null : getAdjacentTileEntity(refTile.getWorldObj(), refTile.xCoord, refTile.yCoord, refTile.zCoord,
				ForgeDirection.values()[side]);
	}

	public static int determineAdjacentSide(TileEntity refTile, int x, int y, int z) {

		return y > refTile.yCoord ? 1 : y < refTile.yCoord ? 0 : z > refTile.zCoord ? 3 : z < refTile.zCoord ? 2 : x > refTile.xCoord ? 5 : 4;
	}

	/* COORDINATE TRANSFORM */
	public static int[] getAdjacentCoordinatesForSide(MovingObjectPosition pos) {

		return getAdjacentCoordinatesForSide(pos.blockX, pos.blockY, pos.blockZ, pos.sideHit);
	}

	public static int[] getAdjacentCoordinatesForSide(int x, int y, int z, int side) {

		return new int[] { x + SIDE_COORD_MOD[side][0], y + SIDE_COORD_MOD[side][1], z + SIDE_COORD_MOD[side][2] };
	}

	public static AxisAlignedBB getAdjacentAABBForSide(MovingObjectPosition pos) {

		return getAdjacentAABBForSide(pos.blockX, pos.blockY, pos.blockZ, pos.sideHit);
	}

	public static AxisAlignedBB getAdjacentAABBForSide(int x, int y, int z, int side) {

		return AxisAlignedBB.getBoundingBox(x + SIDE_COORD_MOD[side][0], y + SIDE_COORD_MOD[side][1], z + SIDE_COORD_MOD[side][2],
				x + SIDE_COORD_AABB[side][0], y + SIDE_COORD_AABB[side][1], z + SIDE_COORD_AABB[side][2]);
	}

	public static int getLeftSide(int side) {

		return SIDE_LEFT[side];
	}

	public static int getRightSide(int side) {

		return SIDE_RIGHT[side];
	}

	public static int getOppositeSide(int side) {

		return SIDE_OPPOSITE[side];
	}

	public static int getAboveSide(int side) {

		return SIDE_ABOVE[side];
	}

	public static int getBelowSide(int side) {

		return SIDE_BELOW[side];
	}

	/* BLOCK ROTATION */
	public static boolean canRotate(Block block) {

		int bId = Block.getIdFromBlock(block);
		return bId < MAX_ID ? rotateType[Block.getIdFromBlock(block)] != 0 : false;
	}

	public static int rotateVanillaBlock(World world, Block block, int x, int y, int z) {

		int bId = Block.getIdFromBlock(block), bMeta = world.getBlockMetadata(x, y, z);
		switch (rotateType[bId]) {
		case RotationType.FOUR_WAY:
			return SIDE_LEFT[bMeta];
		case RotationType.SIX_WAY:
			if (bMeta < 6) {
				return ++bMeta % 6;
			}
			return bMeta;
		case RotationType.RAIL:
			if (bMeta < 2) {
				return ++bMeta % 2;
			}
			return bMeta;
		case RotationType.PUMPKIN:
			return ++bMeta % 4;
		case RotationType.STAIRS:
			return ++bMeta % 8;
		case RotationType.REDSTONE:
			int upper = bMeta & 0xC;
			int lower = bMeta & 0x3;
			return upper + ++lower % 4;
		case RotationType.LOG:
			return (bMeta + 4) % 12;
		case RotationType.SLAB:
			return (bMeta + 8) % 16;
		case RotationType.CHEST:
			int coords[] = new int[3];
			for (int i = 2; i < 6; i++) {
				coords = getAdjacentCoordinatesForSide(x, y, z, i);
				if (isEqual(world.getBlock(coords[0], coords[1], coords[2]), block)) {
					world.setBlockMetadataWithNotify(coords[0], coords[1], coords[2], SIDE_OPPOSITE[bMeta], 1);
					return SIDE_OPPOSITE[bMeta];
				}
			}
			return SIDE_LEFT[bMeta];
		case RotationType.LEVER:
			int shift = 0;
			if (bMeta > 7) {
				bMeta -= 8;
				shift = 8;
			}
			if (bMeta == 5) {
				return 6 + shift;
			} else if (bMeta == 6) {
				return 5 + shift;
			} else if (bMeta == 7) {
				return 0 + shift;
			} else if (bMeta == 0) {
				return 7 + shift;
			}
			return bMeta + shift;
		case RotationType.SIGN:
			return ++bMeta % 16;
		case RotationType.PREVENT:
		default:
			return bMeta;
		}
	}

	public static int rotateVanillaBlockAlt(World world, Block block, int x, int y, int z) {

		int bId = Block.getIdFromBlock(block), bMeta = world.getBlockMetadata(x, y, z);
		switch (rotateType[bId]) {
		case RotationType.FOUR_WAY:
			return SIDE_RIGHT[bMeta];
		case RotationType.SIX_WAY:
			if (bMeta < 6) {
				return (bMeta + 5) % 6;
			}
			return bMeta;
		case RotationType.RAIL:
			if (bMeta < 2) {
				return ++bMeta % 2;
			}
			return bMeta;
		case RotationType.PUMPKIN:
			return (bMeta + 3) % 4;
		case RotationType.STAIRS:
			return (bMeta + 7) % 8;
		case RotationType.REDSTONE:
			int upper = bMeta & 0xC;
			int lower = bMeta & 0x3;
			return upper + (lower + 3) % 4;
		case RotationType.LOG:
			return (bMeta + 8) % 12;
		case RotationType.SLAB:
			return (bMeta + 8) % 16;
		case RotationType.CHEST:
			int coords[] = new int[3];
			for (int i = 2; i < 6; i++) {
				coords = getAdjacentCoordinatesForSide(x, y, z, i);
				if (isEqual(world.getBlock(coords[0], coords[1], coords[2]), block)) {
					world.setBlockMetadataWithNotify(coords[0], coords[1], coords[2], SIDE_OPPOSITE[bMeta], 1);
					return SIDE_OPPOSITE[bMeta];
				}
			}
			return SIDE_RIGHT[bMeta];
		case RotationType.LEVER:
			int shift = 0;
			if (bMeta > 7) {
				bMeta -= 8;
				shift = 8;
			}
			if (bMeta == 5) {
				return 6 + shift;
			} else if (bMeta == 6) {
				return 5 + shift;
			} else if (bMeta == 7) {
				return 0 + shift;
			} else if (bMeta == 0) {
				return 7 + shift;
			}
		case RotationType.SIGN:
			return ++bMeta % 16;
		case RotationType.PREVENT:
		default:
			return bMeta;
		}
	}

	public static List<ItemStack> breakBlock(World worldObj, int x, int y, int z, Block block, int fortune, boolean doBreak, boolean silkTouch) {

		return breakBlock(worldObj, null, x, y, z, block, fortune, doBreak, silkTouch);
	}

	public static List<ItemStack> breakBlock(World worldObj, EntityPlayer player, int x, int y, int z, Block block, int fortune, boolean doBreak,
			boolean silkTouch) {

		if (block.getBlockHardness(worldObj, x, y, z) == -1) {
			return new LinkedList<ItemStack>();
		}
		int meta = worldObj.getBlockMetadata(x, y, z);
		List<ItemStack> stacks = null;
		if (silkTouch && block.canSilkHarvest(worldObj, player, x, y, z, meta)) {
			stacks = new LinkedList<ItemStack>();
			stacks.add(createStackedBlock(block, meta));
		} else {
			stacks = block.getDrops(worldObj, x, y, z, meta, fortune);
		}
		if (!doBreak) {
			return stacks;
		}
		worldObj.playAuxSFXAtEntity(player, 2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));
		worldObj.setBlockToAir(x, y, z);

		List<EntityItem> result = worldObj.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(x - 2, y - 2, z - 2, x + 3, y + 3, z + 3));
		for (int i = 0; i < result.size(); i++) {
			EntityItem entity = result.get(i);
			if (entity.isDead || entity.getEntityItem().stackSize <= 0) {
				continue;
			}
			stacks.add(entity.getEntityItem());
			entity.worldObj.removeEntity(entity);
		}
		return stacks;
	}

	public static ItemStack createStackedBlock(Block block, int bMeta) {

		Item item = Item.getItemFromBlock(block); // not all blocks that can be silked have an item. other method?
		if (item != null && item.getHasSubtypes()) {
			return new ItemStack(item, 1, bMeta);
		}
		return new ItemStack(item, 1, 0);
	}

}
