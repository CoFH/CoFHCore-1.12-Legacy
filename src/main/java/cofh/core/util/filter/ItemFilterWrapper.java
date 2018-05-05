package cofh.core.util.filter;

import cofh.core.init.CoreProps;
import cofh.core.util.helpers.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class ItemFilterWrapper implements IInventory {

	protected final ItemStack stack;
	protected final ItemFilter filter;
	protected boolean dirty = false;

	public ItemFilterWrapper(ItemStack stack, int size) {

		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		this.stack = stack;
		this.filter = new ItemFilter(size);
		filter.deserializeNBT(stack.getTagCompound().getCompoundTag(CoreProps.FILTER));
		markDirty();
	}

	public ItemFilter getFilter() {

		return filter;
	}

	public ItemStack getFilterStack() {

		return stack;
	}

	public Item getFilterItem() {

		return stack.getItem();
	}

	@Override
	public void markDirty() {

		stack.setTagInfo(CoreProps.FILTER, filter.serializeNBT());
		dirty = true;
	}

	public boolean getDirty() {

		boolean r = dirty;
		dirty = false;
		return r;
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

		return filter.getSize();
	}

	@Override
	public boolean isEmpty() {

		return InventoryHelper.isEmpty(filter.getItems());
	}

	@Override
	public ItemStack getStackInSlot(int index) {

		return filter.getSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {

		if (count > 0) {
			filter.setSlot(index, ItemStack.EMPTY);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {

		return decrStackSize(index, 1);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {

		filter.setSlot(index, stack);
	}

	@Override
	public int getInventoryStackLimit() {

		return 1;
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
	public boolean isItemValidForSlot(int index, ItemStack stack) {

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

		filter.clear();
	}

}
