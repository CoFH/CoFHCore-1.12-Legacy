package cofh.lib.inventory;

import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import java.util.HashMap;
import java.util.Map;

public class InventoryManagerStandard implements IInventoryManager {

	private final IInventory _inv;
	protected EnumFacing _targetSide;
	protected int _cachedSize;
	protected int[] _cachedSlots = new int[] {};

	public InventoryManagerStandard(IInventory inventory, EnumFacing targetSide) {

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

		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		int quantitytoadd = stack.getCount();
		ItemStack remaining = stack.copy();
		int[] slots = getSlots();
		if (slots == null) {
			return remaining;
		}

		for (int i : slots) {
			int maxStackSize = Math.min(_inv.getInventoryStackLimit(), stack.getMaxStackSize());
			ItemStack s = getSlotContents(i);
			if (s.isEmpty()) {
				ItemStack add = stack.copy();
				add.setCount(Math.min(quantitytoadd, maxStackSize));

				if (canAddItem(add, i)) {
					quantitytoadd -= add.getCount();
					_inv.setInventorySlotContents(i, add);
					_inv.markDirty();
				}
			} else if (ItemHelper.itemsEqualWithMetadata(s, stack, true)) {
				ItemStack add = stack.copy();
				add.setCount(Math.min(quantitytoadd, maxStackSize - s.getCount()));

				if (add.getCount() > 0 && canAddItem(add, i)) {
					s.grow(add.getCount());
					quantitytoadd -= add.getCount();
					_inv.setInventorySlotContents(i, s);
					_inv.markDirty();
				}
			}
			if (quantitytoadd == 0) {
				break;
			}
		}

		remaining.setCount(quantitytoadd);
		if (remaining.getCount() == 0) {
			return ItemStack.EMPTY;
		} else {
			return remaining;
		}
	}

	@Override
	public ItemStack removeItem(int maxRemove) {

		if (maxRemove <= 0) {
			return ItemStack.EMPTY;
		}

		int[] slots = getSlots();
		if (slots == null) {
			return ItemStack.EMPTY;
		}

		for (int i : slots) {
			ItemStack s = getSlotContents(i);
			if (!s.isEmpty() && canRemoveItem(s, i)) {
				int toRemove = Math.min(s.getCount(), maxRemove);
				s.shrink(toRemove);
				ItemStack removed = s.copy();
				removed.setCount(toRemove);
				if (s.getCount() > 0) {
					_inv.setInventorySlotContents(i, s);
				} else {
					_inv.setInventorySlotContents(i, ItemStack.EMPTY);
				}
				_inv.markDirty();
				return removed;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int maxRemove, ItemStack type) {

		if (maxRemove <= 0) {
			return ItemStack.EMPTY;
		}

		int[] slots = getSlots();
		if (slots == null) {
			return ItemStack.EMPTY;
		}

		for (int i : slots) {
			ItemStack s = getSlotContents(i);
			if (ItemHelper.itemsEqualWithMetadata(s, type, true) && canRemoveItem(s, i)) {
				int toRemove = Math.min(s.getCount(), maxRemove);
				s.shrink(toRemove);
				ItemStack removed = s.copy();
				removed.setCount(toRemove);
				if (s.getCount() > 0) {
					_inv.setInventorySlotContents(i, s);
				} else {
					_inv.setInventorySlotContents(i, ItemStack.EMPTY);
				}
				return removed;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getSlotContents(int slot) {

		return _inv.getStackInSlot(slot);
	}

	@Override
	public int hasItem(ItemStack type) {

		int quantity = 0;
		for (ItemStack s : getContents().values()) {
			if (ItemHelper.itemsEqualWithMetadata(s, type, true)) {
				quantity += s.getCount();
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
			if (ItemHelper.itemsEqualWithMetadata(s, type, true)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int[] getSlots() {

		if (_inv.getSizeInventory() != _cachedSize) {
			_cachedSize = _inv.getSizeInventory();
			_cachedSlots = new int[_cachedSize];
			for (int i = 0; i < _cachedSize; i++) {
				_cachedSlots[i] = i;
			}
		}
		return _cachedSlots;
	}

	@Override
	public Map<Integer, ItemStack> getContents() {

		Map<Integer, ItemStack> contents = new HashMap<>();
		for (int i : getSlots()) {
			contents.put(i, _inv.getStackInSlot(i));
		}
		return contents;
	}

}
