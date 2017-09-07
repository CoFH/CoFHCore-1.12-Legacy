package cofh.core.util.helpers;

import cofh.api.core.IAugmentable;
import cofh.api.item.IAugmentItem;
import cofh.api.item.IAugmentItem.AugmentType;
import cofh.api.item.ICreativeItem;
import cofh.api.item.ILeveledItem;
import cofh.api.item.IUpgradeItem;
import cofh.api.item.IUpgradeItem.UpgradeType;
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
			if (!augments[i].isEmpty()) {
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

		return !stack.isEmpty() && stack.getItem() instanceof IAugmentItem;
	}

	public static AugmentType getAugmentType(ItemStack stack) {

		return ((IAugmentItem) stack.getItem()).getAugmentType(stack);
	}

	public static String getAugmentIdentifier(ItemStack stack) {

		return ((IAugmentItem) stack.getItem()).getAugmentIdentifier(stack);
	}

	public static boolean isUpgradeItem(ItemStack stack) {

		return !stack.isEmpty() && stack.getItem() instanceof IUpgradeItem;
	}

	/* CRAFTING HELPERS */
	public static int getLevel(ItemStack stack) {

		if (stack.getItem() instanceof ILeveledItem) {
			return ((ILeveledItem) stack.getItem()).getLevel(stack);
		}
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("Level")) {
			return -1;
		}
		return stack.getTagCompound().getByte("Level");
	}

	public static ItemStack setLevel(ItemStack stack, int level) {

		if (stack.getItem() instanceof ILeveledItem) {
			return ((ILeveledItem) stack.getItem()).setLevel(stack, level);
		}
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setByte("Level", (byte) level);
		return stack;
	}

	public static boolean isCreative(ItemStack stack) {

		if (stack.getItem() instanceof ICreativeItem) {
			return ((ICreativeItem) stack.getItem()).isCreative(stack);
		}
		return false;
	}

	public static ItemStack setCreative(ItemStack stack) {

		if (stack.getItem() instanceof ICreativeItem) {
			return ((ICreativeItem) stack.getItem()).setCreativeTag(stack);
		}
		return stack;
	}

	/* IUpgradeItem HELPERS */
	public static UpgradeType getUpgradeType(ItemStack stack) {

		if (!(stack.getItem() instanceof IUpgradeItem)) {
			return UpgradeType.INCREMENTAL;
		}
		return ((IUpgradeItem) stack.getItem()).getUpgradeType(stack);
	}

	public static byte getUpgradeLevel(ItemStack stack) {

		if (!(stack.getItem() instanceof IUpgradeItem)) {
			return -1;
		}
		return ((IUpgradeItem) stack.getItem()).getUpgradeLevel(stack);
	}

}
