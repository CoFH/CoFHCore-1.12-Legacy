package cofh.core.item;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface IAOEBreakItem {

	ImmutableList<BlockPos> getAOEBlocks(ItemStack stack, BlockPos pos, EntityPlayer player);

	default float getReachDistance(ItemStack stack) {

		return 4.5F;
	}

}
