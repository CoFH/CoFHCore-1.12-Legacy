package cofh.core.item;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

public interface IEnchantableItem {

	/**
	 * Simple boolean to determine if an enchantment applies to an ItemStack.
	 */
	boolean canEnchant(ItemStack stack, Enchantment enchantment);

}
