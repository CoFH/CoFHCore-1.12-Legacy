package cofh.core.util.helpers;

import cofh.api.core.IAugmentable;
import cofh.api.item.IAugmentItem;
import cofh.api.item.IAugmentItem.AugmentType;
import cofh.api.item.IUpgradeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class AugmentHelper {

	private AugmentHelper() {

	}

	/* NBT TAG HELPERS */
	public static NBTTagCompound setItemStackTagAugments(NBTTagCompound tag, IAugmentable tile) {

		if (tile == null) {
			return null;
		}
		if (tag == null) {
			tag = new NBTTagCompound();
		}
		writeAugmentsToNBT(tag, tile.getAugmentSlots());
		return tag;
	}

	public static void writeAugmentsToNBT(NBTTagCompound nbt, ItemStack[] augments) {

		if (augments.length <= 0) {
			return;
		}
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < augments.length; i++) {
			if (augments[i] != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("Slot", i);
				augments[i].writeToNBT(tag);
				list.appendTag(tag);
			}
		}
		nbt.setTag("Augments", list);
	}

	/* ITEM HELPERS */
	public static boolean isAugmentItem(ItemStack stack) {

		return stack != null && stack.getItem() instanceof IAugmentItem;
	}

	public static AugmentType getAugmentType(ItemStack stack) {

		return ((IAugmentItem) stack.getItem()).getAugmentType(stack);
	}

	public static String getAugmentIdentifier(ItemStack stack) {

		return ((IAugmentItem) stack.getItem()).getAugmentIdentifier(stack);
	}

	public static boolean isUpgradeItem(ItemStack stack) {

		return stack != null && stack.getItem() instanceof IUpgradeItem;
	}

}
