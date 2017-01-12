package cofh.core.item;

import net.minecraft.item.ItemStack;

public interface IEqualityOverrideItem {

	boolean isLastHeldItemEqual(ItemStack current, ItemStack previous);

}
