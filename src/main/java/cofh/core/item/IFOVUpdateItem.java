package cofh.core.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Implemented on Items which update/alter FOV under certain conditions.
 */
public interface IFOVUpdateItem {

	float getFOVMod(ItemStack stack, EntityPlayer player);

}
