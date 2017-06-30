package cofh.lib.gui.container;

import cofh.lib.util.helpers.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class CustomInventoryWrapper implements IInventory {

	private final ItemStack[] inventory;

	public CustomInventoryWrapper(ICustomInventory customInv, int inventoryIndex) {

		inventory = customInv.getInventorySlots(0);
	}

	@Override
	public int getSizeInventory() {

		return inventory.length;
	}

	@Override
	public boolean isEmpty() {

		return InventoryHelper.isEmpty(inventory);
	}

	@Override
	public ItemStack getStackInSlot(int slot) {

		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		if (inventory[slot].isEmpty()) {
			return ItemStack.EMPTY;
		}
		if (inventory[slot].getCount() <= amount) {
			amount = inventory[slot].getCount();
		}
		ItemStack stack = inventory[slot].splitStack(amount);

		if (inventory[slot].getCount() <= 0) {
			inventory[slot] = ItemStack.EMPTY;
		}
		return stack;
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {

		if (inventory[slot].isEmpty()) {
			return ItemStack.EMPTY;
		}
		ItemStack stack = inventory[slot];
		inventory[slot] = ItemStack.EMPTY;
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		inventory[slot] = stack;

		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
			stack.setCount(getInventoryStackLimit());
		}
	}

	@Override
	public String getName() {

		return "container.crafting";
	}

	@Override
	public boolean hasCustomName() {

		return false;
	}

	@Override
	public ITextComponent getDisplayName() {

		return new TextComponentString("");
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

	@Override
	public int getField(int id) {

		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {

		return 0;
	}

	@Override
	public void clear() {

	}

}
