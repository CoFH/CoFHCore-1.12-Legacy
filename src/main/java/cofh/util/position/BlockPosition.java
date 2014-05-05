package cofh.util.position;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPosition {

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

	public BlockPosition(int x, int y, int z, ForgeDirection corientation) {

		this.x = x;
		this.y = y;
		this.z = z;
		orientation = corientation;
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
		orientation = ForgeDirection.UNKNOWN;
	}

	public static BlockPosition fromFactoryTile(IRotateableTile te) {

		BlockPosition bp = new BlockPosition((TileEntity) te);
		bp.orientation = te.getDirectionFacing();
		return bp;
	}

	public BlockPosition copy() {

		return new BlockPosition(x, y, z, orientation);
	}

	public void moveRight(int step) {

		switch (orientation) {
		case SOUTH:
			x = x - step;
			break;
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
	}

	public void moveLeft(int step) {

		moveRight(-step);
	}

	public void moveForwards(int step) {

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
	}

	public void moveBackwards(int step) {

		moveForwards(-step);
	}

	public void moveUp(int step) {

		switch (orientation) {
		case EAST:
		case WEST:
		case NORTH:
		case SOUTH:
			y = y + step;
			break;
		default:
			break;
		}

	}

	public void moveDown(int step) {

		moveUp(-step);
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
		return bp.x == x && bp.y == y && bp.z == z && bp.orientation == orientation;
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

		List<BlockPosition> a = new ArrayList<BlockPosition>();
		a.add(new BlockPosition(x + 1, y, z, ForgeDirection.EAST));
		a.add(new BlockPosition(x - 1, y, z, ForgeDirection.WEST));
		a.add(new BlockPosition(x, y, z + 1, ForgeDirection.SOUTH));
		a.add(new BlockPosition(x, y, z - 1, ForgeDirection.NORTH));
		if (includeVertical) {
			a.add(new BlockPosition(x, y + 1, z, ForgeDirection.UP));
			a.add(new BlockPosition(x, y - 1, z, ForgeDirection.DOWN));
		}
		return a;
	}

	public TileEntity getTileEntity(World world) {

		return world.getTileEntity(x, y, z);
	}

	public static TileEntity getAdjacentTileEntity(TileEntity start, ForgeDirection direction) {

		BlockPosition p = new BlockPosition(start);
		p.orientation = direction;
		p.moveForwards(1);
		return start.getWorldObj().getTileEntity(p.x, p.y, p.z);
	}

	public static TileEntity getAdjacentTileEntity(TileEntity start, ForgeDirection direction, Class<? extends TileEntity> targetClass) {

		TileEntity te = getAdjacentTileEntity(start, direction);
		if (targetClass.isAssignableFrom(te.getClass())) {
			return te;
		} else {
			return null;
		}
	}
}
