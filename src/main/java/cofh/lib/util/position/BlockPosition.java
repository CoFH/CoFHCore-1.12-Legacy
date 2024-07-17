package cofh.lib.util.position;

import cofh.lib.util.helpers.BlockHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPosition implements Comparable<BlockPosition>, Serializable {

	private static final long serialVersionUID = 8671402745765780610L;

	public int x;
	public int y;
	public int z;
	public ForgeDirection orientation;

	public BlockPosition(int x, int y, int z) {

		this.x = x;
		this.y = y;
		this.z = z;
		orientation = ForgeDirection.UNKNOWN;
	}

	public BlockPosition(int x, int y, int z, ForgeDirection orientation) {

		this.x = x;
		this.y = y;
		this.z = z;
		this.orientation = orientation;
	}

	public BlockPosition(BlockPosition p) {

		x = p.x;
		y = p.y;
		z = p.z;
		orientation = p.orientation;
	}

	public BlockPosition(NBTTagCompound tag) {

		x = tag.getInteger("bp_i");
		y = tag.getInteger("bp_j");
		z = tag.getInteger("bp_k");

		if (!tag.hasKey("bp_dir")) {
			orientation = ForgeDirection.UNKNOWN;
		} else {
			orientation = ForgeDirection.getOrientation(tag.getByte("bp_dir"));
		}
	}

	public BlockPosition(TileEntity tile) {

		x = tile.xCoord;
		y = tile.yCoord;
		z = tile.zCoord;
		if (tile instanceof IRotateableTile) {
			orientation = ((IRotateableTile) tile).getDirectionFacing();
		} else {
			orientation = ForgeDirection.UNKNOWN;
		}
	}

	public static <T extends TileEntity & IRotateableTile> BlockPosition fromRotateableTile(T te) {

		return new BlockPosition(te);
	}

	public BlockPosition copy() {

		return new BlockPosition(x, y, z, orientation);
	}

	public BlockPosition copy(ForgeDirection orientation) {

		return new BlockPosition(x, y, z, orientation);
	}

	public BlockPosition setOrientation(ForgeDirection o) {

		orientation = o;
		return this;
	}

	public BlockPosition step(int dir) {

		int[] d = BlockHelper.SIDE_COORD_MOD[dir];
		x += d[0];
		y += d[1];
		z += d[2];
		return this;
	}

	public BlockPosition step(int dir, int dist) {

		int[] d = BlockHelper.SIDE_COORD_MOD[dir];
		x += d[0] * dist;
		y += d[1] * dist;
		z += d[2] * dist;
		return this;
	}

	public BlockPosition step(ForgeDirection dir) {

		x += dir.offsetX;
		y += dir.offsetY;
		z += dir.offsetZ;
		return this;
	}

	public BlockPosition step(ForgeDirection dir, int dist) {

		x += dir.offsetX * dist;
		y += dir.offsetY * dist;
		z += dir.offsetZ * dist;
		return this;
	}

	public BlockPosition moveForwards(int step) {

		switch (orientation) {
		case UP:
			y = y + step;
			break;
		case DOWN:
			y = y - step;
			break;
		case SOUTH:
			z = z + step;
			break;
		case NORTH:
			z = z - step;
			break;
		case EAST:
			x = x + step;
			break;
		case WEST:
			x = x - step;
			break;
		default:
		}
		return this;
	}

	public BlockPosition moveBackwards(int step) {

		return moveForwards(-step);
	}

	public BlockPosition moveRight(int step) {

		switch (orientation) {
		case UP:
		case SOUTH:
			x = x - step;
			break;
		case DOWN:
		case NORTH:
			x = x + step;
			break;
		case EAST:
			z = z + step;
			break;
		case WEST:
			z = z - step;
			break;
		default:
			break;
		}
		return this;
	}

	public BlockPosition moveLeft(int step) {

		return moveRight(-step);
	}

	public BlockPosition moveUp(int step) {

		switch (orientation) {
		case EAST:
		case WEST:
		case NORTH:
		case SOUTH:
			y = y + step;
			break;
		case UP:
			z = z - step;
			break;
		case DOWN:
			z = z + step;
		default:
			break;
		}
		return this;
	}

	public BlockPosition moveDown(int step) {

		return moveUp(-step);
	}

	public void writeToNBT(NBTTagCompound tag) {

		tag.setInteger("bp_i", x);
		tag.setInteger("bp_j", y);
		tag.setInteger("bp_k", z);
		tag.setByte("bp_dir", (byte) orientation.ordinal());
	}

	@Override
	public String toString() {

		if (orientation == null) {
			return "{" + x + ", " + y + ", " + z + "}";
		}
		return "{" + x + ", " + y + ", " + z + ";" + orientation.toString() + "}";
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof BlockPosition)) {
			return false;
		}
		BlockPosition bp = (BlockPosition) obj;
		return bp.x == x & bp.y == y & bp.z == z & bp.orientation == orientation;
	}

	// so compiler will optimize
	public boolean equals(BlockPosition bp) {

		return bp != null && bp.x == x & bp.y == y & bp.z == z & bp.orientation == orientation;
	}

	@Override
	public int hashCode() {

		return (x & 0xFFF) | (y & 0xFF << 8) | (z & 0xFFF << 12);
	}

	public BlockPosition min(BlockPosition p) {

		return new BlockPosition(p.x > x ? x : p.x, p.y > y ? y : p.y, p.z > z ? z : p.z);
	}

	public BlockPosition max(BlockPosition p) {

		return new BlockPosition(p.x < x ? x : p.x, p.y < y ? y : p.y, p.z < z ? z : p.z);
	}

	public List<BlockPosition> getAdjacent(boolean includeVertical) {

		List<BlockPosition> a = new ArrayList<BlockPosition>(4 + (includeVertical ? 2 : 0));
		a.add(copy(ForgeDirection.EAST).moveForwards(1));
		a.add(copy(ForgeDirection.WEST).moveForwards(1));
		a.add(copy(ForgeDirection.SOUTH).moveForwards(1));
		a.add(copy(ForgeDirection.NORTH).moveForwards(1));
		if (includeVertical) {
			a.add(copy(ForgeDirection.UP).moveForwards(1));
			a.add(copy(ForgeDirection.DOWN).moveForwards(1));
		}
		return a;
	}

	public boolean blockExists(World world) {

		return world.blockExists(x, y, z);
	}

	public TileEntity getTileEntity(World world) {

		return world.getTileEntity(x, y, z);
	}

	public Block getBlock(World world) {

		return world.getBlock(x, y, z);
	}

	@SuppressWarnings("unchecked")
	public <T> T getTileEntity(World world, Class<T> targetClass) {

		TileEntity te = world.getTileEntity(x, y, z);
		if (targetClass.isInstance(te)) {
			return (T) te;
		} else {
			return null;
		}
	}

	public static ForgeDirection getDirection(int xS, int yS, int zS, int x, int y, int z) {

		int dir = 0;
		if (y < yS) {
			dir |= 1;
		} else if (y != yS) {
			dir |= 2;
		}
		if (z < zS) {
			dir |= 4;
		} else if (z != zS) {
			dir |= 8;
		}
		if (x < xS) {
			dir |= 16;
		} else if (x != xS) {
			dir |= 32;
		}
		switch (dir) {
		case 2:
			return ForgeDirection.UP;
		case 1:
			return ForgeDirection.DOWN;
		case 4:
			return ForgeDirection.WEST;
		case 8:
			return ForgeDirection.EAST;
		case 16:
			return ForgeDirection.NORTH;
		case 32:
			return ForgeDirection.SOUTH;
		default:
			return ForgeDirection.UNKNOWN;
		}
	}

	public static TileEntity getTileEntityRaw(World world, int x, int y, int z) {

		if (!world.blockExists(x, y, z)) {
			return null;
		}
        ChunkPosition chunkposition = new ChunkPosition(x & 15, y, z & 15);
        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        TileEntity tileentity = (TileEntity)chunk.chunkTileEntityMap.get(chunkposition);
		return tileentity == null || tileentity.isInvalid() ? null : tileentity;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getTileEntityRaw(World world, int x, int y, int z, Class<T> targetClass) {

		TileEntity te = getTileEntityRaw(world, x, y, z);
		if (targetClass.isInstance(te)) {
			return (T) te;
		} else {
			return null;
		}
	}

	public static boolean blockExists(TileEntity start, ForgeDirection dir) {

		final int x = start.xCoord + dir.offsetX, y = start.yCoord + dir.offsetY, z = start.zCoord + dir.offsetZ;
		return start.getWorldObj().blockExists(x, y, z);
	}

	public static TileEntity getAdjacentTileEntity(TileEntity start, ForgeDirection dir) {

		final int x = start.xCoord + dir.offsetX, y = start.yCoord + dir.offsetY, z = start.zCoord + dir.offsetZ;
		return getTileEntityRaw(start.getWorldObj(), x, y, z);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getAdjacentTileEntity(TileEntity start, ForgeDirection direction, Class<T> targetClass) {

		TileEntity te = getAdjacentTileEntity(start, direction);
		if (targetClass.isInstance(te)) {
			return (T) te;
		} else {
			return null;
		}
	}

	/* Comparable */
	@Override
	public int compareTo(BlockPosition other) {

		return this.x == other.x ? this.y == other.y ? this.z - other.z : this.y - other.y : this.x - other.x;
	}

}
