package cofh.api.item;

import net.minecraft.item.ItemStack;

public interface ICreativeItem extends ILeveledItem {

	boolean isCreative(ItemStack stack);

	ItemStack setCreativeTag(ItemStack stack);

}
