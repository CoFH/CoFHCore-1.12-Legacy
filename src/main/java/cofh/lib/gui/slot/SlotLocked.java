package cofh.lib.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * A slot that can only be used to display an item and disallows editing.
 */
public class SlotLocked extends Slot {

	public SlotLocked(IInventory inventory, int index, int x, int y) {

		super(inventory, index, x, y);
	}

	@Override
	public boolean canTakeStack(EntityPlayer player) {

		return false;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return false;
	}

}
