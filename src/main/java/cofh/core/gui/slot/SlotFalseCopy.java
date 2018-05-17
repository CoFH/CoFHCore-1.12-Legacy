package cofh.core.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Slot which copies an ItemStack when clicked on, does not decrement the ItemStack on the cursor.
 *
 * @author King Lemming
 */
public class SlotFalseCopy extends Slot {

	public int slotIndex;

	public SlotFalseCopy(IInventory inventory, int index, int x, int y) {

		super(inventory, index, x, y);
		slotIndex = index;
	}

	@Override
	public boolean canTakeStack(EntityPlayer player) {

		return false;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return true;
	}

	@Override
	public void putStack(ItemStack stack) {

		if (!isItemValid(stack)) {
			return;
		}
		if (!stack.isEmpty()) {
			stack.setCount(1);
		}
		inventory.setInventorySlotContents(this.slotIndex, stack);
		onSlotChanged();
	}

}
