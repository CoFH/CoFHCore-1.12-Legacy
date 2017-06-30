package cofh.lib.inventory;

import net.minecraft.item.ItemStack;

import java.util.Map;

public interface IInventoryManager {

	boolean canAddItem(ItemStack stack, int slot);

	boolean canRemoveItem(ItemStack stack, int slot);

	ItemStack addItem(ItemStack stack);

	ItemStack removeItem(int maxRemove);

	ItemStack removeItem(int maxRemove, ItemStack type);

	ItemStack getSlotContents(int slot);

	int hasItem(ItemStack type);

	int findItem(ItemStack type);

	int[] getSlots();

	Map<Integer, ItemStack> getContents();

}
