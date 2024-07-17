package cofh.lib.util.helpers;

import cofh.api.tileentity.IRedstoneControl;
import cofh.api.tileentity.IRedstoneControl.ControlMode;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

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
			switch (stack.stackTagCompound.getByte("RSControl")) {
			case 0:
				list.add(StringHelper.localize("info.cofh.signal") + ": " + StringHelper.RED + StringHelper.localize("info.cofh.disabled") + StringHelper.END);
				return;
			case 1:
				list.add(StringHelper.localize("info.cofh.signal") + ": " + StringHelper.BRIGHT_GREEN + StringHelper.localize("info.cofh.enabled")
						+ StringHelper.LIGHT_GRAY + ", " + StringHelper.localize("info.cofh.low") + StringHelper.END);
				return;
			case 2:
				list.add(StringHelper.localize("info.cofh.signal") + ": " + StringHelper.BRIGHT_GREEN + StringHelper.localize("info.cofh.enabled")
						+ StringHelper.LIGHT_GRAY + ", " + StringHelper.localize("info.cofh.high") + StringHelper.END);
				return;
			}
		}
	}

	/* ITEM HELPERS */
	public static boolean hasRSControl(ItemStack stack) {

		return stack.stackTagCompound == null ? false : stack.stackTagCompound.hasKey("RSControl");
	}

	public static boolean setControl(ItemStack stack, ControlMode control) {

		if (stack.stackTagCompound == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.stackTagCompound.setByte("RSControl", (byte) control.ordinal());
		return true;
	}

	public static ControlMode getControl(ItemStack stack) {

		return stack.stackTagCompound == null ? ControlMode.DISABLED : ControlMode.values()[stack.stackTagCompound.getByte("RSControl")];
	}

}
