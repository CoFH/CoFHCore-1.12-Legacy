package cofh.lib.gui.slot;

import cofh.lib.gui.container.ICustomInventory;
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

		if (customInv.getInventorySlots(inventoryIndex)[getSlotIndex()].isEmpty()) {
			return ItemStack.EMPTY;
		}
		if (customInv.getInventorySlots(inventoryIndex)[getSlotIndex()].getCount() <= amount) {
			amount = customInv.getInventorySlots(inventoryIndex)[getSlotIndex()].getCount();
		}
		ItemStack stack = customInv.getInventorySlots(inventoryIndex)[getSlotIndex()].splitStack(amount);

		if (customInv.getInventorySlots(inventoryIndex)[getSlotIndex()].getCount() <= 0) {
			customInv.getInventorySlots(inventoryIndex)[getSlotIndex()] = ItemStack.EMPTY;
		}
		return stack;
	}

	@Override
	public boolean isHere(IInventory inventory, int slot) {

		return false;
	}

	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer) {

		return canTake;
	}

}
