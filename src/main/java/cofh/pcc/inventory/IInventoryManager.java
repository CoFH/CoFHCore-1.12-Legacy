package cofh.pcc.inventory;

import java.util.Map;

import net.minecraft.item.ItemStack;

public interface IInventoryManager {

	public ItemStack addItem(ItemStack stack);

	public ItemStack removeItem(int maxRemove);

	public ItemStack removeItem(int maxRemove, ItemStack type);

	public ItemStack getSlotContents(int slot);

	public int hasItem(ItemStack type);

	public int findItem(ItemStack type);

	public int[] getSlots();

	public Map<Integer, ItemStack> getContents();
}
