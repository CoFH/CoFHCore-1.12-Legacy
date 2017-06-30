package cofh.lib.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Slot that will redirect inserts to another inventory slot (other than index), but not be visible.
 *
 * Used primarily for containers that have a larger internal inventory than external (e.g., DeepStorageUnit)
 */
public class SlotInvisible extends Slot {

	protected final int slotIndex;

	public SlotInvisible(IInventory inventory, int index, int x, int y, int slot) {

		super(inventory, index, x, y);
		slotIndex = slot;
	}

	@Override
	public void putStack(ItemStack stack) {

		this.inventory.setInventorySlotContents(slotIndex, stack);
		this.onSlotChanged();
	}

	@Override
	public ItemStack getStack() {

		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int par1) {

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canTakeStack(EntityPlayer p) {

		return false;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public boolean isEnabled() {

		return false;
	}

}
