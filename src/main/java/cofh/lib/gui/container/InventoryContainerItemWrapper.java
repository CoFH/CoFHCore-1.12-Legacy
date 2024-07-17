package cofh.lib.gui.container;

import cofh.api.item.IInventoryContainerItem;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class InventoryContainerItemWrapper implements IInventory {

	protected final IInventoryContainerItem inventoryItem;
	protected final ItemStack stack;
	protected NBTTagCompound tag;
	protected ItemStack[] inventory;
	protected boolean dirty = false;

	@Deprecated
	public InventoryContainerItemWrapper(ContainerInventoryItem gui, ItemStack stack) {

		this(stack);
	}

	public InventoryContainerItemWrapper(ItemStack itemstack) {

		stack = itemstack;
		inventoryItem = (IInventoryContainerItem) stack.getItem();
		inventory = new ItemStack[getSizeInventory()];

		loadInventory();
		markDirty();
	}

	protected void loadInventory() {

		boolean loaded = false;
		if (stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("Inventory")) {
			loaded = stack.hasTagCompound();
			if (loaded) {
				if (stack.stackTagCompound.hasKey("inventory")) {
					tag = stack.stackTagCompound.getCompoundTag("inventory");
					stack.stackTagCompound.removeTag("inventory");
				} else {
					tag = stack.stackTagCompound;
				}
				loadStacks();
				tag = new NBTTagCompound();
				saveStacks();
			} else {
				stack.setTagInfo("Inventory", new NBTTagCompound());
			}
		}
		tag = stack.stackTagCompound.getCompoundTag("Inventory");
		loadStacks();
	}

	protected void loadStacks() {

		for (int i = inventory.length; i-- > 0;) {
			if (tag.hasKey("Slot" + i)) {
				inventory[i] = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("Slot" + i));
			} else if (tag.hasKey("slot" + i)) {
				inventory[i] = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("slot" + i));
			} else {
				inventory[i] = null;
			}
		}
	}

	protected void saveStacks() {

		for (int i = inventory.length; i-- > 0;) {
			if (inventory[i] == null) {
				tag.removeTag("Slot" + i);
			} else {
				tag.setTag("Slot" + i, inventory[i].writeToNBT(new NBTTagCompound()));
			}
		}
		stack.setTagInfo("Inventory", tag);
	}

	@Override
	public void markDirty() {

		saveStacks();
		dirty = true;
	}

	public boolean getDirty() {

		boolean r = dirty;
		dirty = false;
		return r;
	}

	public Item getContainerItem() {

		return stack.getItem();
	}

	public ItemStack getContainerStack() {

		saveStacks();
		return stack;
	}

	@Override
	public int getSizeInventory() {

		return inventoryItem.getSizeInventory(stack);
	}

	@Override
	public ItemStack getStackInSlot(int i) {

		return inventory[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {

		ItemStack s = inventory[i];
		if (s == null) {
			return null;
		}
		ItemStack r = s.splitStack(j);
		if (s.stackSize <= 0) {
			inventory[i] = null;
			r.stackSize += s.stackSize;
		}
		return r;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {

		inventory[i] = itemstack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {

		return null;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		if (stack != null && stack.getItem() instanceof IInventoryContainerItem) {
			return ((IInventoryContainerItem) stack.getItem()).getSizeInventory(stack) <= 0;
		}
		return true;
	}

	@Override
	public String getInventoryName() {

		return stack.getDisplayName();
	}

	@Override
	public boolean hasCustomInventoryName() {

		return true;
	}

	@Override
	public int getInventoryStackLimit() {

		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {

		return true;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

		markDirty();
	}

}
