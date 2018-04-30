package cofh.api.item;

import net.minecraft.item.ItemStack;

public interface ILeveledItem {

	int getMaxLevel(ItemStack stack);

	default int getLevel(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			setDefaultTag(stack);
		}
		return stack.getTagCompound().getByte("Level");
	}

	default ItemStack setLevel(ItemStack stack, int level) {

		if (stack.getTagCompound() == null) {
			return setDefaultTag(stack, level);
		}
		stack.getTagCompound().setByte("Level", (byte) level);
		return stack;
	}

	default ItemStack setDefaultTag(ItemStack stack) {

		return setDefaultTag(stack, 0);
	}

	ItemStack setDefaultTag(ItemStack stack, int level);

}
