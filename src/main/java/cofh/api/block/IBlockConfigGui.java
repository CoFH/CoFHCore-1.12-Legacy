package cofh.api.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Implement this interface on blocks which have a GUI that needs a tool (e.g., Multimeter) to open.
 */
public interface IBlockConfigGui {

	/**
	 * This function will open a GUI if the player has permission.
	 *
	 * @param world  Reference to the world.
	 * @param pos    Coordinates of the block.
	 * @param side   The side of the block.
	 * @param player Player doing the configuring.
	 * @return True if the GUI was opened.
	 */
	boolean openConfigGui(IBlockAccess world, BlockPos pos, EnumFacing side, EntityPlayer player);

}
