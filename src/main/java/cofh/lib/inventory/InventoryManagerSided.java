package cofh.lib.inventory;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class InventoryManagerSided extends InventoryManagerStandard {

	private final ISidedInventory _sidedInv;

	public InventoryManagerSided(ISidedInventory inventory, ForgeDirection targetSide) {

		super(inventory, targetSide);
		_sidedInv = inventory;
	}

	@Override
	public boolean canAddItem(ItemStack stack, int slot) {

		return super.canAddItem(stack, slot) && _sidedInv.canInsertItem(slot, stack, _targetSide.ordinal());
	}

	@Override
	public boolean canRemoveItem(ItemStack stack, int slot) {

		return _sidedInv.canExtractItem(slot, stack, _targetSide.ordinal());
	}

	@Override
	public int[] getSlots() {

		return _sidedInv.getAccessibleSlotsFromSide(_targetSide.ordinal());
	}

}
