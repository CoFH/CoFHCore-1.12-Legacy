package cofh.core.gui.container;

import cofh.core.gui.slot.SlotPlayerAugment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerAugments extends ContainerCore {

	EntityPlayer thePlayer;

	public ContainerAugments(InventoryPlayer inventory) {

		thePlayer = inventory.player;
		for (int i = 0; i < 5; i++) {
			addSlotToContainer(new SlotPlayerAugment(thePlayer, i, 40 + i * 18 + (i * 2), 26));
		}
		bindPlayerInventory(inventory);
	}

	@Override
	protected int getPlayerInventoryVerticalOffset() {

		return 84;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {

		return true;
	}

	@Override
	protected int getSizeInventory() {

		return 0;
	}

}
