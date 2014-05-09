package cofh.pcc.inventory;

import cofh.util.ItemHelper;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class InventoryManagerStandard implements IInventoryManager {

	private IInventory _inv;
	protected ForgeDirection _targetSide;

	public InventoryManagerStandard(IInventory inventory, ForgeDirection targetSide) {

		_inv = inventory;
		_targetSide = targetSide;
	}

	@Override
	public boolean canAddItem(ItemStack stack, int slot) {

		return _inv.isItemValidForSlot(slot, stack);
	}

	@Override
	public boolean canRemoveItem(ItemStack stack, int slot) {

		return true;
	}

	@Override
	public ItemStack addItem(ItemStack stack) {

		if (stack == null) {
			return null;
		}

		int quantitytoadd = stack.stackSize;
		ItemStack remaining = stack.copy();
		int[] slots = getSlots();
		if (slots == null) {
			return remaining;
		}

		for (int i : slots) {
			int maxStackSize = Math.min(_inv.getInventoryStackLimit(), stack.getMaxStackSize());
			ItemStack s = getSlotContents(i);
			if (s == null) {
				ItemStack add = stack.copy();
				add.stackSize = Math.min(quantitytoadd, maxStackSize);

				if (canAddItem(add, i)) {
					quantitytoadd -= add.stackSize;
					_inv.setInventorySlotContents(i, add);
					_inv.markDirty();
				}
			} else if (ItemHelper.itemsEqualWithMetadata(s, stack)) {
				ItemStack add = stack.copy();
				add.stackSize = Math.min(quantitytoadd, maxStackSize - s.stackSize);

				if (add.stackSize > 0 && canAddItem(add, i)) {
					s.stackSize += add.stackSize;
					quantitytoadd -= add.stackSize;
					_inv.setInventorySlotContents(i, s);
					_inv.markDirty();
				}
			}
			if (quantitytoadd == 0) {
				break;
			}
		}

		remaining.stackSize = quantitytoadd;
		if (remaining.stackSize == 0) {
			return null;
		} else {
			return remaining;
		}
	}

	@Override
	public ItemStack removeItem(int maxRemove) {

		if (maxRemove <= 0) {
			return null;
		}

		int[] slots = getSlots();
		if (slots == null) {
			return null;
		}

		for (int i : slots) {
			ItemStack s = getSlotContents(i);
			if (s != null && canRemoveItem(s, i)) {
				int toRemove = Math.min(s.stackSize, maxRemove);
				s.stackSize -= toRemove;
				ItemStack removed = s.copy();
				removed.stackSize = toRemove;
				if (s.stackSize > 0) {
					_inv.setInventorySlotContents(i, s);
				} else {
					_inv.setInventorySlotContents(i, null);
				}
				_inv.markDirty();
				return removed;
			}
		}
		return null;
	}

	@Override
	public ItemStack removeItem(int maxRemove, ItemStack type) {

		if (maxRemove <= 0) {
			return null;
		}

		int[] slots = getSlots();
		if (slots == null) {
			return null;
		}

		for (int i : slots) {
			ItemStack s = getSlotContents(i);
			if (ItemHelper.itemsEqualWithMetadata(s, type) && canRemoveItem(s, i)) {
				int toRemove = Math.min(s.stackSize, maxRemove);
				s.stackSize -= toRemove;
				ItemStack removed = s.copy();
				removed.stackSize = toRemove;
				if (s.stackSize > 0) {
					_inv.setInventorySlotContents(i, s);
				} else {
					_inv.setInventorySlotContents(i, null);
				}
				return removed;
			}
		}
		return null;
	}

	@Override
	public ItemStack getSlotContents(int slot) {

		return _inv.getStackInSlot(slot);
	}

	@Override
	public int hasItem(ItemStack type) {

		int quantity = 0;
		for (ItemStack s : getContents().values()) {
			if (ItemHelper.itemsEqualWithMetadata(s, type)) {
				quantity += s.stackSize;
			}
		}
		return quantity;
	}

	@Override
	public int findItem(ItemStack type) {

		int[] slots = getSlots();
		if (slots == null) {
			return -1;
		}

		for (int i : slots) {
			ItemStack s = _inv.getStackInSlot(i);
			if (ItemHelper.itemsEqualWithMetadata(s, type)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int[] getSlots() {

		int[] slots = new int[_inv.getSizeInventory()];
		for (int i = 0; i < slots.length; i++) {
			slots[i] = i;
		}
		return slots;
	}

	@Override
	public Map<Integer, ItemStack> getContents() {

		Map<Integer, ItemStack> contents = new HashMap<Integer, ItemStack>();
		for (int i : getSlots()) {
			contents.put(i, _inv.getStackInSlot(i));
		}
		return contents;
	}
}
