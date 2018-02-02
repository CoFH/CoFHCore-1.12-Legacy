package cofh.core.util.filter;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemSetFilter implements INBTSerializable<NBTTagCompound> {

    private ItemFilter filter;
    private int[] stackCounts;

    public ItemSetFilter(int size) {

        filter = new ItemFilter(size);
        stackCounts = new int[size];
        Arrays.fill(stackCounts, 0);
    }

    public ItemFilter getFilter() {

        return filter;
    }

    public void setCount(int index, int count) {

        stackCounts[index] = count;
    }

    public int getCount(int index) {

        return stackCounts[index];
    }

    public void clear() {

        filter.clear();
        Arrays.fill(stackCounts, 0);
    }

    public int getSize() {

        return stackCounts.length;
    }

    public boolean matches(List<ItemStack> items) {

        for(int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if(!filter.matches(stack)) {
                return false;
            }
            if(stack.getCount() < stackCounts[i]) {
                return false;
            }
        }

        return true;
    }

    public List<ItemStack> takeSet(List<ItemStack> items) {

        List<ItemStack> ret = new ArrayList<>(stackCounts.length);

        for(int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            ItemStack remainder = item.splitStack(stackCounts[i]);
            ret.add(remainder);
        }

        return ret;
    }

    /* INBTSerializable */
    @Override
    public NBTTagCompound serializeNBT() {

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Filter", filter.serializeNBT());
        nbt.setIntArray("StackCounts", stackCounts);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {

        filter.deserializeNBT(nbt.getCompoundTag("Filter"));
        stackCounts = nbt.getIntArray("StackCounts");
    }
}
