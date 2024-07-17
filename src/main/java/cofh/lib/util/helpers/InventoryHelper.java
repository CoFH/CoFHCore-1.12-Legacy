package cofh.lib.util.helpers;

import cofh.api.transport.IItemDuct;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * This class contains helper functions related to Inventories and Inventory manipulation.
 *
 * @author King Lemming
 *
 */
public class InventoryHelper {

	private InventoryHelper() {

	}

	/**
	 * Copy an entire inventory. Best to avoid doing this often.
	 */
	public static ItemStack[] cloneInventory(ItemStack[] inventory) {

		ItemStack[] inventoryCopy = new ItemStack[inventory.length];
		for (int i = 0; i < inventory.length; i++) {
			inventoryCopy[i] = inventory[i] == null ? null : inventory[i].copy();
		}
		return inventoryCopy;
	}

	/**
	 * Add an ItemStack to an inventory. Return true if the entire stack was added.
	 *
	 * @param inventory
	 *            The inventory.
	 * @param stack
	 *            ItemStack to add.
	 * @param startIndex
	 *            First slot to attempt to add into. Does not loop around fully.
	 * @param endIndex
	 *            Final slot to attempt to add into. Should be at most length - 1
	 */
	public static boolean addItemStackToInventory(ItemStack[] inventory, ItemStack stack, int startIndex, int endIndex) {

		if (stack == null) {
			return true;
		}
		int openSlot = -1;
		for (int i = startIndex; i <= endIndex; i++) {
			if (ItemHelper.itemsEqualForCrafting(stack, inventory[i]) && inventory[i].getMaxStackSize() > inventory[i].stackSize) {
				int hold = inventory[i].getMaxStackSize() - inventory[i].stackSize;
				if (hold >= stack.stackSize) {
					inventory[i].stackSize += stack.stackSize;
					stack = null;
					return true;
				} else {
					stack.stackSize -= hold;
					inventory[i].stackSize += hold;
				}
			} else if (inventory[i] == null && openSlot == -1) {
				openSlot = i;
			}
		}
		if (openSlot > -1) {
			inventory[openSlot] = stack;
		} else {
			return false;
		}
		return true;
	}

	/**
	 * Shortcut method for above, assumes ending slot is length - 1
	 */
	public static boolean addItemStackToInventory(ItemStack[] inventory, ItemStack stack, int startIndex) {

		return addItemStackToInventory(inventory, stack, startIndex, inventory.length - 1);
	}

	/**
	 * Shortcut method for above, assumes starting slot is 0.
	 */
	public static boolean addItemStackToInventory(ItemStack[] inventory, ItemStack stack) {

		return addItemStackToInventory(inventory, stack, 0);
	}

	/* IInventoryHandler Interaction */

	// IInventoryHandler is not currently implemented or used. Possibly in the future.

	/* IInventory Interaction */
	public static ItemStack extractItemStackFromInventory(IInventory inventory, int side) {

		if (inventory == null) {
			return null;
		}
		ItemStack retStack = null;

		if (inventory instanceof ISidedInventory) {
			ISidedInventory sidedInv = (ISidedInventory) inventory;
			int slots[] = sidedInv.getAccessibleSlotsFromSide(side);
			for (int i = 0; i < slots.length && retStack == null; i++) {
				if (sidedInv.getStackInSlot(i) != null && sidedInv.canExtractItem(i, sidedInv.getStackInSlot(i), side)) {
					retStack = sidedInv.getStackInSlot(i).copy();
					sidedInv.setInventorySlotContents(i, null);
				}
			}
		} else {
			for (int i = 0; i < inventory.getSizeInventory() && retStack == null; i++) {
				if (inventory.getStackInSlot(i) != null) {
					retStack = inventory.getStackInSlot(i).copy();
					inventory.setInventorySlotContents(i, null);
				}
			}
		}
		if (retStack != null) {
			inventory.markDirty();
		}
		return retStack;
	}

	public static ItemStack insertItemStackIntoInventory(IInventory inventory, ItemStack stack, int side) {

		if (stack == null || inventory == null) {
			return null;
		}
		int stackSize = stack.stackSize;

		if (inventory instanceof ISidedInventory) {
			ISidedInventory sidedInv = (ISidedInventory) inventory;
			int slots[] = sidedInv.getAccessibleSlotsFromSide(side);

			if (slots == null) {
				return stack;
			}
			for (int i = 0; i < slots.length && stack != null; i++) {
				if (sidedInv.canInsertItem(slots[i], stack, side)) {
					ItemStack existingStack = inventory.getStackInSlot(slots[i]);
					if (ItemHelper.itemsEqualWithMetadata(stack, existingStack, true)) {
						stack = addToOccupiedInventorySlot(sidedInv, slots[i], stack, existingStack);
					}
				}
			}
			for (int i = 0; i < slots.length && stack != null; i++) {
				if (inventory.getStackInSlot(slots[i]) == null && sidedInv.canInsertItem(slots[i], stack, side)) {
					stack = addToEmptyInventorySlot(sidedInv, slots[i], stack);
				}
			}
		} else {
			int invSize = inventory.getSizeInventory();
			for (int i = 0; i < invSize && stack != null; i++) {
				ItemStack existingStack = inventory.getStackInSlot(i);
				if (ItemHelper.itemsEqualWithMetadata(stack, existingStack, true)) {
					stack = addToOccupiedInventorySlot(inventory, i, stack, existingStack);
				}
			}
			for (int i = 0; i < invSize && stack != null; i++) {
				if (inventory.getStackInSlot(i) == null) {
					stack = addToEmptyInventorySlot(inventory, i, stack);
				}
			}
		}
		if (stack == null || stack.stackSize != stackSize) {
			inventory.markDirty();
		}
		return stack;
	}

	public static ItemStack simulateInsertItemStackIntoInventory(IInventory inventory, ItemStack stack, int side) {

		if (stack == null || inventory == null) {
			return null;
		}
		if (inventory instanceof ISidedInventory) {
			ISidedInventory sidedInv = (ISidedInventory) inventory;
			int slots[] = sidedInv.getAccessibleSlotsFromSide(side);

			if (slots == null) {
				return stack;
			}
			for (int i = 0; i < slots.length && stack != null; i++) {
				if (sidedInv.canInsertItem(slots[i], stack, side)) {
					ItemStack existingStack = inventory.getStackInSlot(slots[i]);
					if (ItemHelper.itemsEqualWithMetadata(stack, existingStack, true)) {
						stack = simulateAddToOccupiedInventorySlot(sidedInv, slots[i], stack, existingStack);
					}
				}
			}
			for (int i = 0; i < slots.length && stack != null; i++) {
				if (inventory.getStackInSlot(slots[i]) == null && sidedInv.canInsertItem(slots[i], stack, side)) {
					stack = simulateAddToEmptyInventorySlot(sidedInv, slots[i], stack);
				}
			}
		} else {
			int invSize = inventory.getSizeInventory();
			for (int i = 0; i < invSize && stack != null; i++) {
				ItemStack existingStack = inventory.getStackInSlot(i);
				if (ItemHelper.itemsEqualWithMetadata(stack, existingStack, true)) {
					stack = simulateAddToOccupiedInventorySlot(inventory, i, stack, existingStack);
				}
			}
			for (int i = 0; i < invSize && stack != null; i++) {
				if (inventory.getStackInSlot(i) == null) {
					stack = simulateAddToEmptyInventorySlot(inventory, i, stack);
				}
			}
		}
		return stack;
	}

	/* Slot Interaction */
	public static ItemStack addToEmptyInventorySlot(IInventory inventory, int slot, ItemStack stack) {

		if (!inventory.isItemValidForSlot(slot, stack)) {
			return stack;
		}
		int stackLimit = inventory.getInventoryStackLimit();
		inventory.setInventorySlotContents(slot, ItemHelper.cloneStack(stack, Math.min(stack.stackSize, stackLimit)));
		return stackLimit >= stack.stackSize ? null : stack.splitStack(stack.stackSize - stackLimit);
	}

	public static ItemStack addToOccupiedInventorySlot(IInventory inventory, int slot, ItemStack stack) {

		int stackLimit = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());
		ItemStack stackInSlot = inventory.getStackInSlot(slot);

		if (stack.stackSize + stackInSlot.stackSize > stackLimit) {
			int stackDiff = stackLimit - stackInSlot.stackSize;
			stackInSlot.stackSize = stackLimit;
			stack.stackSize -= stackDiff;
			inventory.setInventorySlotContents(slot, stackInSlot);
			return stack;
		}
		stackInSlot.stackSize += Math.min(stack.stackSize, stackLimit);
		inventory.setInventorySlotContents(slot, stackInSlot);
		return stackLimit >= stack.stackSize ? null : stack.splitStack(stack.stackSize - stackLimit);
	}

	public static ItemStack addToOccupiedInventorySlot(IInventory inventory, int slot, ItemStack stack, ItemStack existingStack) {

		int stackLimit = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());

		if (existingStack.stackSize >= stackLimit) {
			return stack;
		}
		if (stack.stackSize + existingStack.stackSize > stackLimit) {
			int stackDiff = stackLimit - existingStack.stackSize;
			existingStack.stackSize = stackLimit;
			stack.stackSize -= stackDiff;
			inventory.setInventorySlotContents(slot, existingStack);
			return stack;
		}
		existingStack.stackSize += stack.stackSize;
		inventory.setInventorySlotContents(slot, existingStack);
		return stackLimit >= stack.stackSize ? null : stack.splitStack(stack.stackSize - stackLimit);
	}

	public static ItemStack simulateAddToEmptyInventorySlot(IInventory inventory, int slot, ItemStack stack) {

		if (!inventory.isItemValidForSlot(slot, stack)) {
			return stack;
		}
		int stackLimit = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());
		return stackLimit >= stack.stackSize ? null : stack.splitStack(stack.stackSize - stackLimit);
	}

	public static ItemStack simulateAddToOccupiedInventorySlot(IInventory inventory, int slot, ItemStack stack) {

		int stackLimit = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());
		ItemStack stackInSlot = inventory.getStackInSlot(slot);

		if (stack.stackSize + stackInSlot.stackSize > stackLimit) {
			stack.stackSize -= stackLimit - stackInSlot.stackSize;
			return stack;
		}
		return stackLimit >= stack.stackSize ? null : stack.splitStack(stack.stackSize - stackLimit);
	}

	public static ItemStack simulateAddToOccupiedInventorySlot(IInventory inventory, int slot, ItemStack stack, ItemStack existingStack) {

		int stackLimit = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());

		if (existingStack.stackSize >= stackLimit) {
			return stack;
		}
		if (stack.stackSize + existingStack.stackSize > stackLimit) {
			stack.stackSize -= stackLimit - existingStack.stackSize;
			return stack;
		}
		return stackLimit >= stack.stackSize ? null : stack.splitStack(stack.stackSize - stackLimit);
	}

	public static boolean mergeItemStack(List<Slot> slots, ItemStack stack, int start, int length, boolean reverse) {

		return mergeItemStack(slots, stack, start, length, reverse, true);
	}

	public static boolean mergeItemStack(List<Slot> slots, ItemStack stack, int start, int length, boolean r, boolean limit) {

		boolean successful = false;
		int i = !r ? start : length - 1;
		int iterOrder = !r ? 1 : -1;

		Slot slot;
		ItemStack existingStack;

		if (stack.isStackable()) {
			while (stack.stackSize > 0 && (!r && i < length || r && i >= start)) {
				slot = slots.get(i);
				existingStack = slot.getStack();

				if (existingStack != null) {
					int maxStack = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
					int rmv = Math.min(maxStack, stack.stackSize);

					if (slot.isItemValid(ItemHelper.cloneStack(stack, rmv)) && existingStack.getItem().equals(stack.getItem())
							&& (!stack.getHasSubtypes() || stack.getItemDamage() == existingStack.getItemDamage())
							&& ItemStack.areItemStackTagsEqual(stack, existingStack)) {
						int existingSize = existingStack.stackSize + stack.stackSize;

						if (existingSize <= maxStack) {
							stack.stackSize = 0;
							existingStack.stackSize = existingSize;
							slot.putStack(existingStack);
							successful = true;
						} else if (existingStack.stackSize < maxStack) {
							stack.stackSize -= maxStack - existingStack.stackSize;
							existingStack.stackSize = maxStack;
							slot.putStack(existingStack);
							successful = true;
						}
					}
				}

				i += iterOrder;
			}
		}

		if (stack.stackSize > 0) {
			i = !r ? start : length - 1;

			while (stack.stackSize > 0 && (!r && i < length || r && i >= start)) {
				slot = slots.get(i);
				existingStack = slot.getStack();

				if (existingStack == null) {
					int maxStack = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
					int rmv = Math.min(maxStack, stack.stackSize);

					if (slot.isItemValid(ItemHelper.cloneStack(stack, rmv))) {
						existingStack = stack.splitStack(rmv);
						slot.putStack(existingStack);
						successful = true;
					}
				}

				i += iterOrder;
			}
		}

		return successful;
	}

	/* HELPERS */
	public static ItemStack addToInsertion(Object tile, int side, ItemStack stack) {

		if (stack == null) {
			return null;
		}
		if (tile instanceof IInventory) {
			stack = insertItemStackIntoInventory((IInventory) tile, stack, BlockHelper.SIDE_OPPOSITE[side]);
		} else {
			stack = ((IItemDuct) tile).insertItem(ForgeDirection.VALID_DIRECTIONS[side ^ 1], stack);
		}
		return stack;
	}

	public static boolean isInventory(TileEntity tile) {

		return tile instanceof IInventory;
	}

	public static boolean isInsertion(Object tile) {

		return tile instanceof IInventory || tile instanceof IItemDuct;
	}

}
