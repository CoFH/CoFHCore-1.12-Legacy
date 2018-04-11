package cofh.core.util.helpers;

import cofh.core.gui.slot.SlotFalseCopy;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import java.util.List;

/**
 * This class contains helper functions related to Inventories and Inventory manipulation.
 *
 * @author King Lemming
 */
public class InventoryHelper {

	private InventoryHelper() {

	}

	/**
	 * Add an ItemStack to an inventory. Return true if the entire stack was added.
	 *
	 * @param inventory  The inventory.
	 * @param stack      ItemStack to add.
	 * @param startIndex First slot to attempt to add into. Does not loop around fully.
	 * @param endIndex   Final slot to attempt to add into. Should be at most length - 1
	 */
	public static boolean addItemStackToInventory(ItemStack[] inventory, ItemStack stack, int startIndex, int endIndex) {

		if (stack.isEmpty()) {
			return true;
		}
		int openSlot = -1;
		for (int i = startIndex; i <= endIndex; i++) {
			if (ItemHelper.itemsEqualForCrafting(stack, inventory[i]) && inventory[i].getMaxStackSize() > inventory[i].getCount()) {
				int hold = inventory[i].getMaxStackSize() - inventory[i].getCount();
				if (hold >= stack.getCount()) {
					inventory[i].grow(stack.getCount());
					stack = ItemStack.EMPTY;
					return true;
				} else {
					stack.shrink(hold);
					inventory[i].grow(hold);
				}
			} else if (inventory[i].isEmpty() && openSlot == -1) {
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

	public static ItemStack insertStackIntoInventory(IItemHandler handler, ItemStack stack, boolean simulate) {

		return insertStackIntoInventory(handler, stack, simulate, false);
	}

	public static ItemStack insertStackIntoInventory(IItemHandler handler, ItemStack stack, boolean simulate, boolean forceEmptySlot) {

		return forceEmptySlot ? ItemHandlerHelper.insertItem(handler, stack, simulate) : ItemHandlerHelper.insertItemStacked(handler, stack, simulate);
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
			while (stack.getCount() > 0 && (!r && i < length || r && i >= start)) {
				slot = slots.get(i);

				if (slot instanceof SlotFalseCopy) {
					i += iterOrder;
					continue;
				}
				existingStack = slot.getStack();

				if (!existingStack.isEmpty()) {
					int maxStack = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
					int rmv = Math.min(maxStack, stack.getCount());

					if (slot.isItemValid(ItemHelper.cloneStack(stack, rmv)) && existingStack.getItem().equals(stack.getItem()) && (!stack.getHasSubtypes() || stack.getItemDamage() == existingStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, existingStack)) {
						int existingSize = existingStack.getCount() + stack.getCount();

						if (existingSize <= maxStack) {
							stack.setCount(0);
							existingStack.setCount(existingSize);
							slot.putStack(existingStack);
							successful = true;
						} else if (existingStack.getCount() < maxStack) {
							stack.shrink(maxStack - existingStack.getCount());
							existingStack.setCount(maxStack);
							slot.putStack(existingStack);
							successful = true;
						}
					}
				}
				i += iterOrder;
			}
		}
		if (stack.getCount() > 0) {
			i = !r ? start : length - 1;
			while (stack.getCount() > 0 && (!r && i < length || r && i >= start)) {
				slot = slots.get(i);

				if (slot instanceof SlotFalseCopy) {
					i += iterOrder;
					continue;
				}
				existingStack = slot.getStack();

				if (existingStack.isEmpty()) {
					int maxStack = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
					int rmv = Math.min(maxStack, stack.getCount());

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

	public static boolean isAccessibleInput(TileEntity tile, EnumFacing side) {

		return hasItemHandlerCap(tile, side.getOpposite()) && getItemHandlerCap(tile, side.getOpposite()).getSlots() > 0;
	}

	public static boolean isAccessibleOutput(TileEntity tile, EnumFacing side) {

		return hasItemHandlerCap(tile, side.getOpposite()) && getItemHandlerCap(tile, side.getOpposite()).getSlots() > 0;
	}

	/* HELPERS */
	public static ItemStack addToInventory(TileEntity tile, EnumFacing side, ItemStack stack) {

		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		if (hasItemHandlerCap(tile, side.getOpposite())) {
			stack = insertStackIntoInventory(getItemHandlerCap(tile, side.getOpposite()), stack, false);
		}
		return stack;
	}

	public static boolean hasItemHandlerCap(TileEntity tileEntity, EnumFacing face) {

		return tileEntity != null && (tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face) || tileEntity instanceof ISidedInventory || tileEntity instanceof IInventory);
	}

	public static IItemHandler getItemHandlerCap(TileEntity tileEntity, EnumFacing face) {

		if (tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face)) {
			return tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face);
		} else if (tileEntity instanceof ISidedInventory && face != null) {
			return new SidedInvWrapper(((ISidedInventory) tileEntity), face);
		} else if (tileEntity instanceof IInventory) {
			return new InvWrapper(((IInventory) tileEntity));
		}
		return EmptyHandler.INSTANCE;
	}

	public static boolean isEmpty(ItemStack[] inventory) {

		for (ItemStack stack : inventory) {
			if (!stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

}
