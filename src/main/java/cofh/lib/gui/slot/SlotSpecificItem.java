package cofh.lib.gui.slot;

import cofh.lib.inventory.ComparableItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Slot which is restricted to a specific item and maximum amount.
 *
 * @author King Lemming
 */
public class SlotSpecificItem extends Slot {

	protected final ComparableItemStack stack;
	protected ComparableItemStack query = new ComparableItemStack(new ItemStack(Blocks.STONE));
	protected int slotStackLimit = -1;

	public SlotSpecificItem(IInventory inventory, int index, int x, int y, ItemStack stack) {

		super(inventory, index, x, y);

		this.stack = new ComparableItemStack(stack);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return this.stack.isItemEqual(query.set(stack));
	}

	public SlotSpecificItem setSlotStackLimit(int slotStackLimit) {

		this.slotStackLimit = slotStackLimit;
		return this;
	}

	@Override
	public int getSlotStackLimit() {

		return slotStackLimit <= 0 ? inventory.getInventoryStackLimit() : slotStackLimit;
	}

}
