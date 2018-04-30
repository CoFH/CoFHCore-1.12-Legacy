package cofh.api.item;

import net.minecraft.item.ItemStack;

public interface ICreativeItem extends ILeveledItem {

	default boolean isCreative(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			setDefaultTag(stack);
		}
		return stack.getTagCompound().getBoolean("Creative");
	}

	default ItemStack setCreativeTag(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			setDefaultTag(stack, getMaxLevel(stack));
		}
		stack.getTagCompound().setBoolean("Creative", true);
		return stack;
	}

}
