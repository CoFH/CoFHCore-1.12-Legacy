package cofh.pcc.inventory;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class InventoryManagerSided extends InventoryManagerStandard {

	private ISidedInventory _sidedInv;

	public InventoryManagerSided(ISidedInventory inventory, ForgeDirection targetSide) {

		super(inventory, targetSide);
		_sidedInv = inventory;
	}

	@Override
	protected boolean canAddItem(ItemStack stack, int slot) {

		return _sidedInv.canInsertItem(slot, stack, _targetSide.ordinal());
	}

	@Override
	protected boolean canRemoveItem(ItemStack stack, int slot) {

		return _sidedInv.canExtractItem(slot, stack, _targetSide.ordinal());
	}

	@Override
	public int[] getSlots() {

		return _sidedInv.getAccessibleSlotsFromSide(_targetSide.ordinal());
	}
}
