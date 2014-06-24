package cofh.gui.slot;

import cofh.api.core.IAugmentable;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class SlotAugment extends Slot {

	IAugmentable myTile;

	public SlotAugment(IAugmentable tile, IInventory inventory, int slotIndex, int x, int y) {

		super(inventory, slotIndex, x, y);
		myTile = tile;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return super.isItemValid(stack);
	}

	@Override
	public ItemStack getStack() {

		return myTile.getAugmentSlots()[getSlotIndex()];
	}

	@Override
	public void putStack(ItemStack stack) {

		myTile.getAugmentSlots()[getSlotIndex()] = stack;
		onSlotChanged();
	}

	@Override
	public void onSlotChanged() {

		myTile.installAugments();
		((TileEntity) myTile).markDirty();
	}

	@Override
	public int getSlotStackLimit() {

		return 1;
	}

	@Override
	public ItemStack decrStackSize(int amount) {

		if (myTile.getAugmentSlots()[getSlotIndex()] == null) {
			return null;
		}
		ItemStack stack = myTile.getAugmentSlots()[getSlotIndex()].splitStack(1);
		myTile.getAugmentSlots()[getSlotIndex()] = null;

		return stack;
	}

	@Override
	public boolean isSlotInInventory(IInventory inventory, int slot) {

		return false;
	}

}
