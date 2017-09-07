package cofh.api.item;

import net.minecraft.item.ItemStack;

public interface ILeveledItem {

	int getLevel(ItemStack stack);

	ItemStack setLevel(ItemStack stack, int level);

	ItemStack setDefaultTag(ItemStack stack);

	ItemStack setDefaultTag(ItemStack stack, int level);

}
