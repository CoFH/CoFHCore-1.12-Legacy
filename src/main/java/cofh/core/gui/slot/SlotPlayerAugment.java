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

		return stack != null;
	}

	@Override
	public ItemStack getStack() {

		if (thePlayer.getEntityData().hasKey(AUGMENT_KEY + getSlotIndex())) {
			return ItemStack.loadItemStackFromNBT(thePlayer.getEntityData().getCompoundTag("cofhAugment" + getSlotIndex()));
		}

		return null;
	}

	@Override
	public void putStack(ItemStack newStack) {

		if (newStack == null || newStack.stackSize <= 0) {
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
		if (tempStack != null) {
			ItemStack itemstack;

			if (tempStack.stackSize <= amt) {
				putStack(null);
				return tempStack;
			} else {
				itemstack = tempStack.splitStack(amt);

				if (tempStack.stackSize == 0) {
					putStack(null);
				} else {
					putStack(tempStack);
				}

				return itemstack;
			}
		} else {
			return null;
		}
	}

}
