package cofh.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Implement this interface on Item classes which have multiple modes - what that means is completely up to you. This just provides a uniform way of dealing
 * with them.
 *
 * @author King Lemming
 */
public interface IMultiModeItem {

	/**
	 * Get the current mode of an item.
	 */
	default int getMode(ItemStack stack) {

		return !stack.hasTagCompound() ? 0 : stack.getTagCompound().getInteger("Mode");
	}

	/**
	 * Attempt to set the empowered state of the item.
	 *
	 * @param stack ItemStack to set the mode on.
	 * @param mode  Desired mode.
	 * @return TRUE if the operation was successful, FALSE if it was not.
	 */
	default boolean setMode(ItemStack stack, int mode) {

		if (getNumModes(stack) <= 1) {
			return false;
		}
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		if (mode < getNumModes(stack)) {
			stack.getTagCompound().setInteger("Mode", mode);
			return true;
		}
		return false;
	}

	/**
	 * Increment the current mode of an item.
	 */
	default boolean incrMode(ItemStack stack) {

		if (getNumModes(stack) <= 1) {
			return false;
		}
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		int curMode = getMode(stack);
		curMode++;
		if (curMode >= getNumModes(stack)) {
			curMode = 0;
		}
		stack.getTagCompound().setInteger("Mode", curMode);
		return true;
	}

	/**
	 * Decrement the current mode of an item.
	 */
	default boolean decrMode(ItemStack stack) {

		if (getNumModes(stack) <= 1) {
			return false;
		}
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		int curMode = getMode(stack);
		curMode--;
		if (curMode <= 0) {
			curMode = getNumModes(stack) - 1;
		}
		stack.getTagCompound().setInteger("Mode", curMode);
		return true;
	}

	/**
	 * Returns the number of possible modes.
	 */
	default int getNumModes(ItemStack stack) {

		return 2;
	}

	/**
	 * Callback method for reacting to a state change. Useful in KeyBinding handlers.
	 *
	 * @param player Player holding the item, if applicable.
	 * @param stack  The item being held.
	 */
	default void onModeChange(EntityPlayer player, ItemStack stack) {

	}

}
