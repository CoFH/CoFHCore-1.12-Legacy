package cofh.core.gui.slot;

import cofh.core.gui.container.ICustomInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCustomInventory extends Slot {

	protected ICustomInventory customInv;
	protected int inventoryIndex = 0;
	protected boolean canTake = true;

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

		customInv.onSlotUpdate(getSlotIndex());
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
	public boolean canTakeStack(EntityPlayer player) {

		return canTake;
	}

}
