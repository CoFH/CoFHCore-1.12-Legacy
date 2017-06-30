package cofh.api.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * Implemented on Blocks which have some method of being instantly dismantled.
 *
 * @author King Lemming
 */
public interface IDismantleable {

	/**
	 * Dismantles the block. If returnDrops is true, the drop(s) should be placed into the player's inventory.
	 */
	ArrayList<ItemStack> dismantleBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player, boolean returnDrops);

	/**
	 * Return true if the block can be dismantled. The criteria for this is entirely up to the block.
	 */
	boolean canDismantle(World world, BlockPos pos, IBlockState state, EntityPlayer player);

}
