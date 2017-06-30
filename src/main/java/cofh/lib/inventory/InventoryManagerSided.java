package cofh.lib.inventory;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class InventoryManagerSided extends InventoryManagerStandard {

	private final ISidedInventory _sidedInv;

	public InventoryManagerSided(ISidedInventory inventory, EnumFacing targetSide) {

		super(inventory, targetSide);
		_sidedInv = inventory;
	}

	@Override
	public boolean canAddItem(ItemStack stack, int slot) {

		return super.canAddItem(stack, slot) && _sidedInv.canInsertItem(slot, stack, _targetSide);
	}

	@Override
	public boolean canRemoveItem(ItemStack stack, int slot) {

		return _sidedInv.canExtractItem(slot, stack, _targetSide);
	}

	@Override
	public int[] getSlots() {

		return _sidedInv.getSlotsForFace(_targetSide);
	}

}
