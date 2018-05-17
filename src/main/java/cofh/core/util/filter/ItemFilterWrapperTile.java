package cofh.core.util.filter;

import cofh.core.util.helpers.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;

public class ItemFilterWrapperTile implements IInventory {

	protected final TileEntity tile;
	protected final ItemFilter filter;

	public ItemFilterWrapperTile(TileEntity tile, ItemFilter filter) {

		this.tile = tile;
		this.filter = filter;
	}

	public ItemFilter getFilter() {

		return filter;
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
	public void markDirty() {

		tile.markDirty();
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

	/* IWorldNameable */
	@Override
	public String getName() {

		return null;
	}

	@Override
	public boolean hasCustomName() {

		return false;
	}

	@Override
	public ITextComponent getDisplayName() {

		return null;
	}

}
