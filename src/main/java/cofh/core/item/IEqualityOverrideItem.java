package cofh.core.item;

import net.minecraft.item.ItemStack;

/**
 * Implement this on Items which need custom equality comparisons during use. (e.g., ignore a specific NBT change.)
 *
 */
public interface IEqualityOverrideItem {

	public boolean isLastHeldItemEqual(ItemStack current, ItemStack previous);

}
