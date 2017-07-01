package cofh.core.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

public class InventoryCraftingCustom extends InventoryCrafting {

	public IInventory masterInv;
	public int invOffset = 0;
	public int invSize = 0;
	/**
	 * the width of the crafting inventory
	 */
	public final int inventoryWidth;

	/**
	 * Class containing the callbacks for the events onGUIClosed and onCraftMatrixChanged.
	 */
	public final Container eventHandler;

	public InventoryCraftingCustom(Container container, int rows, int columns, IInventory master, int startingInventoryIndex) {

		super(container, rows, columns);
		invSize = rows * columns;
		this.eventHandler = container;
		this.inventoryWidth = rows;
		invOffset = startingInventoryIndex;
		masterInv = master;
	}

	@Override
	public int getSizeInventory() {

		return invSize;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {

		return slot >= this.getSizeInventory() ? ItemStack.EMPTY : masterInv.getStackInSlot(invOffset + slot);
	}

	@Override
	public ItemStack getStackInRowAndColumn(int row, int column) {

		if (row >= 0 && row < this.inventoryWidth) {
			int k = row + column * this.inventoryWidth;
			return this.getStackInSlot(k);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {

		if (!masterInv.getStackInSlot(invOffset + slot).isEmpty()) {
			ItemStack stack = masterInv.getStackInSlot(invOffset + slot);
			masterInv.setInventorySlotContents(invOffset + slot, ItemStack.EMPTY);
			return stack;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		if (!masterInv.getStackInSlot(invOffset + slot).isEmpty()) {
			ItemStack stack;

			if (masterInv.getStackInSlot(invOffset + slot).getCount() <= amount) {
				stack = masterInv.getStackInSlot(invOffset + slot);
				masterInv.setInventorySlotContents(invOffset + slot, ItemStack.EMPTY);
				this.eventHandler.onCraftMatrixChanged(this);
				return stack;
			} else {
				stack = masterInv.getStackInSlot(invOffset + slot).splitStack(amount);

				if (masterInv.getStackInSlot(invOffset + slot).getCount() <= 0) {
					masterInv.setInventorySlotContents(invOffset + slot, ItemStack.EMPTY);
				}
				this.eventHandler.onCraftMatrixChanged(this);
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		masterInv.setInventorySlotContents(invOffset + slot, stack);
		this.eventHandler.onCraftMatrixChanged(this);
	}

	@Override
	public int getInventoryStackLimit() {

		return 64;
	}

	@Override
	public void markDirty() {

	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {

		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return true;
	}

}
