package cofh.core.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SlotPlayerAugment extends Slot {

	public static final String AUGMENT_KEY = "cofhAugment";
	EntityPlayer thePlayer;

	public SlotPlayerAugment(EntityPlayer thePlayer, int slotIndex, int x, int y) {

		super(null, slotIndex, x, y);
		this.thePlayer = thePlayer;

	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return !stack.isEmpty();
	}

	@Override
	public ItemStack getStack() {

		if (thePlayer.getEntityData().hasKey(AUGMENT_KEY + getSlotIndex())) {
			return new ItemStack(thePlayer.getEntityData().getCompoundTag("cofhAugment" + getSlotIndex()));
		}

		return ItemStack.EMPTY;
	}

	@Override
	public void putStack(ItemStack newStack) {

		if (newStack.isEmpty() || newStack.getCount() <= 0) {
			thePlayer.getEntityData().removeTag(AUGMENT_KEY + getSlotIndex());
		} else {
			NBTTagCompound temp = new NBTTagCompound();
			newStack.writeToNBT(temp);
			thePlayer.getEntityData().setTag(AUGMENT_KEY + getSlotIndex(), temp);
		}
		this.onSlotChanged();
	}

	@Override
	public void onSlotChanged() {

	}

	@Override
	public int getSlotStackLimit() {

		return 1;
	}

	@Override
	public ItemStack decrStackSize(int amt) {

		ItemStack tempStack = getStack();
		if (!tempStack.isEmpty()) {
			ItemStack itemstack;

			if (tempStack.getCount() <= amt) {
				putStack(ItemStack.EMPTY);
				return tempStack;
			} else {
				itemstack = tempStack.splitStack(amt);

				if (tempStack.getCount() == 0) {
					putStack(ItemStack.EMPTY);
				} else {
					putStack(tempStack);
				}

				return itemstack;
			}
		} else {
			return ItemStack.EMPTY;
		}
	}

}
