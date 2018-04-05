package cofh.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Implement this interface on subclasses of Item to have that item work as a bow for CoFH mods.
 */
public interface IToolBow {

	void onBowFired(EntityPlayer player, ItemStack item);

	float getArrowDamageMultiplier(ItemStack item);

	float getArrowSpeedMultiplier(ItemStack item);

}
