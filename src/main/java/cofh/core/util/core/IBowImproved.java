package cofh.core.util.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IBowImproved {

	void onBowFired(EntityPlayer player, ItemStack stack);

	float getArrowDamageMultiplier(ItemStack stack);

	float getArrowSpeedMultiplier(ItemStack stack);

}
