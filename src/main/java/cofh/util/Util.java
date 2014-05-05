package cofh.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cofh.util.position.BlockPosition;

public class Util {

	public static boolean isBlockUnbreakable(World world, int x, int y, int z) {

		Block b = world.getBlock(x, y, z);
		return b instanceof BlockLiquid || b.getBlockHardness(world, x, y, z) < 0;
	}

	public static boolean isRedstonePowered(World world, int x, int y, int z) {

		if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
			return true;
		}
		for (BlockPosition bp : new BlockPosition(x, y, z).getAdjacent(false)) {
			Block block = world.getBlock(bp.x, bp.y, bp.z);
			if (block.equals(Blocks.redstone_wire) && block.isProvidingStrongPower(world, bp.x, bp.y, bp.z, 1) > 0) {
				return true;
			}
		}
		return false;
	}

	public static boolean isRedstonePowered(TileEntity te) {

		return isRedstonePowered(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
	}
}
