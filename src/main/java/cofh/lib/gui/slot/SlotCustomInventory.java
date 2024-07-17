package cofh.lib.gui.slot;

import cofh.api.core.ICustomInventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCustomInventory extends Slot {

	ICustomInventory customInv;
	int inventoryIndex = 0;
	boolean canTake = true;

	public SlotCustomInventory(ICustomInventory tile, int invIndex, IInventory inventory, int slotIndex, int x, int y, boolean lootable) {

		super(inventory, slotIndex, x, y);
		customInv = tile;
		inventoryIndex = invIndex;
		canTake = lootable;
	}

	@Override
	public ItemStack getStack() {

		return customInv.getInventorySlots(inventoryIndex)[getSlotIndex()];
	}

	@Override
	public void putStack(ItemStack stack) {

		customInv.getInventorySlots(inventoryIndex)[getSlotIndex()] = stack;
		onSlotChanged();
	}

	@Override
	public void onSlotChanged() {

		customInv.onSlotUpdate();
	}

	@Override
	public int getSlotStackLimit() {

		return customInv.getSlotStackLimit(getSlotIndex());
	}

	@Override
	public ItemStack decrStackSize(int amount) {

		if (customInv.getInventorySlots(inventoryIndex)[getSlotIndex()] == null) {
			return null;
		}
		if (customInv.getInventorySlots(inventoryIndex)[getSlotIndex()].stackSize <= amount) {
			amount = customInv.getInventorySlots(inventoryIndex)[getSlotIndex()].stackSize;
		}
		ItemStack stack = customInv.getInventorySlots(inventoryIndex)[getSlotIndex()].splitStack(amount);

		if (customInv.getInventorySlots(inventoryIndex)[getSlotIndex()].stackSize <= 0) {
			customInv.getInventorySlots(inventoryIndex)[getSlotIndex()] = null;
		}
		return stack;
	}

	@Override
	public boolean isSlotInInventory(IInventory inventory, int slot) {

		return false;
	}

	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer) {

		return canTake;
	}

}
