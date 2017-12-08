package cofh.core.gui.container;

import cofh.api.item.IInventoryContainerItem;
import cofh.core.util.helpers.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.Arrays;

public class InventoryContainerItemWrapper implements IInventory {

	protected final IInventoryContainerItem inventoryItem;
	protected final ItemStack stack;
	protected NBTTagCompound tag;
	protected ItemStack[] inventory;
	protected boolean dirty = false;

	public InventoryContainerItemWrapper(ItemStack itemstack) {

		stack = itemstack;
		inventoryItem = (IInventoryContainerItem) stack.getItem();
		inventory = new ItemStack[getSizeInventory()];
		Arrays.fill(inventory, ItemStack.EMPTY);

		loadInventory();
		markDirty();
	}

	protected void loadInventory() {

		boolean loaded;
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("Inventory")) {
			loaded = stack.hasTagCompound();
			if (loaded) {
				if (stack.getTagCompound().hasKey("inventory")) {
					tag = stack.getTagCompound().getCompoundTag("inventory");
					stack.getTagCompound().removeTag("inventory");
				} else {
					tag = stack.getTagCompound();
				}
				loadStacks();
				tag = new NBTTagCompound();
				saveStacks();
			} else {
				stack.setTagInfo("Inventory", new NBTTagCompound());
			}
		}
		tag = stack.getTagCompound().getCompoundTag("Inventory");
		loadStacks();
	}

	protected void loadStacks() {

		for (int i = inventory.length; i-- > 0; ) {
			if (tag.hasKey("Slot" + i)) {
				inventory[i] = new ItemStack(tag.getCompoundTag("Slot" + i));
			} else if (tag.hasKey("slot" + i)) {
				inventory[i] = new ItemStack(tag.getCompoundTag("slot" + i));
			} else {
				inventory[i] = ItemStack.EMPTY;
			}
		}
	}

	protected void saveStacks() {

		for (int i = inventory.length; i-- > 0; ) {
			if (inventory[i].isEmpty()) {
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

	/* IWorldNameable */
	@Override
	public String getName() {

		return stack.getDisplayName();
	}

	@Override
	public boolean hasCustomName() {

		return stack.hasDisplayName();
	}

	@Override
	public ITextComponent getDisplayName() {

		return new TextComponentString(stack.getDisplayName());
	}

	/* IInventory */
	@Override
	public int getSizeInventory() {

		return inventoryItem.getSizeInventory(stack);
	}

	@Override
	public boolean isEmpty() {

		return InventoryHelper.isEmpty(inventory);
	}

	@Override
	public ItemStack getStackInSlot(int i) {

		return inventory[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {

		ItemStack s = inventory[i];
		if (s.isEmpty()) {
			return ItemStack.EMPTY;
		}
		ItemStack r = s.splitStack(j);
		if (s.getCount() <= 0) {
			inventory[i] = ItemStack.EMPTY;
			r.grow(s.getCount());
		}
		return r;
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {

		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {

		inventory[i] = itemstack;
	}

	@Override
	public int getInventoryStackLimit() {

		return 64;
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

		markDirty();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return stack.isEmpty() || !(stack.getItem() instanceof IInventoryContainerItem) || ((IInventoryContainerItem) stack.getItem()).getSizeInventory(stack) <= 0;
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
