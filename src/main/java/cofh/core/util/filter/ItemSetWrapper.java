package cofh.core.util.filter;

import cofh.core.util.helpers.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;

public class ItemSetWrapper implements IInventory {

    protected final TileEntity tile;
    protected final ItemSetFilter setFilter;

    public ItemSetWrapper(TileEntity tile, int size) {

        this.tile = tile;
        this.setFilter = new ItemSetFilter(size);
        setFilter.deserializeNBT(tile.getTileData().getCompoundTag("Filter"));
        markDirty();
    }

    public ItemSetFilter getSetFilter() {

        return setFilter;
    }

    /* IInventory */
    @Override
    public int getSizeInventory() {

        return setFilter.getSize();
    }

    @Override
    public boolean isEmpty() {

        return InventoryHelper.isEmpty(setFilter.getFilter().getItems());
    }

    @Override
    public ItemStack getStackInSlot(int index) {

        ItemStack stack = setFilter.getFilter().getSlot(index);
        stack.setCount(setFilter.getCount(index));
        return stack;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {

        if(count <= 0) {
            return ItemStack.EMPTY;
        }

        int stackCount = setFilter.getCount(index);
        if(stackCount - count <= 0) {
            ItemStack stack = setFilter.getFilter().getSlot(index);
            stack.setCount(setFilter.getCount(index));

            setFilter.getFilter().setSlot(index, ItemStack.EMPTY);
            setFilter.setCount(index, 0);

            return stack;
        }

        ItemStack stack = setFilter.getFilter().getSlot(index);
        stack.setCount(setFilter.getCount(index));
        ItemStack remainder = stack.splitStack(count);
        setFilter.setCount(index, remainder.getCount());
        return remainder;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {

        ItemStack stack = setFilter.getFilter().getSlot(index);
        stack.setCount(setFilter.getCount(index));

        setFilter.getFilter().setSlot(index, ItemStack.EMPTY);
        setFilter.setCount(index, 0);

        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {

        setFilter.getFilter().setSlot(index, stack);
        setFilter.setCount(index, stack.getCount());
    }

    @Override
    public int getInventoryStackLimit() {

        return 64;
    }

    @Override
    public void markDirty() {

        tile.getTileData().setTag("Filter", setFilter.serializeNBT());
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

        setFilter.clear();
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

        return tile.getDisplayName();
    }
}
