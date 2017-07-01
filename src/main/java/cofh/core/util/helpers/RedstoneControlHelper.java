package cofh.core.util.helpers;

import cofh.api.tileentity.IRedstoneControl;
import cofh.api.tileentity.IRedstoneControl.ControlMode;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class RedstoneControlHelper {

	private RedstoneControlHelper() {

	}

	/* NBT TAG HELPERS */
	public static NBTTagCompound setItemStackTagRS(NBTTagCompound tag, IRedstoneControl tile) {

		if (tile == null) {
			return null;
		}
		if (tag == null) {
			tag = new NBTTagCompound();
		}
		tag.setByte("RSControl", (byte) tile.getControl().ordinal());
		return tag;
	}

	public static ControlMode getControlFromNBT(NBTTagCompound tag) {

		return tag == null ? ControlMode.DISABLED : ControlMode.values()[tag.getByte("RSControl")];
	}

	/**
	 * Adds Redstone Control information to ItemStacks.
	 */
	public static void addRSControlInformation(ItemStack stack, List<String> list) {

		if (hasRSControl(stack)) {
			switch (stack.getTagCompound().getByte("RSControl")) {
				case 0:
					list.add(StringHelper.localize("info.cofh.signalDisabled"));
					return;
				case 1:
					list.add(StringHelper.localize("info.cofh.signalEnabledLow"));
					return;
				case 2:
					list.add(StringHelper.localize("info.cofh.signalEnabledHigh"));
					return;
			}
		}
	}

	/* ITEM HELPERS */
	public static boolean hasRSControl(ItemStack stack) {

		return stack.getTagCompound() != null && stack.getTagCompound().hasKey("RSControl");
	}

	public static boolean setControl(ItemStack stack, ControlMode control) {

		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setByte("RSControl", (byte) control.ordinal());
		return true;
	}

	public static ControlMode getControl(ItemStack stack) {

		return stack.getTagCompound() == null ? ControlMode.DISABLED : ControlMode.values()[stack.getTagCompound().getByte("RSControl")];
	}

}
