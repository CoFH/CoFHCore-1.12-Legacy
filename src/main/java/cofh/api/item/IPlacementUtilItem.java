package cofh.api.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IPlacementUtilItem {

	boolean onBlockPlacement(ItemStack stack, World world, BlockPos pos, IBlockState state, EntityPlayer player);

}
