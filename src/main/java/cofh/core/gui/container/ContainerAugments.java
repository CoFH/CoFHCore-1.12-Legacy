package cofh.core.gui.container;

import cofh.core.gui.slot.SlotPlayerAugment;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class ContainerAugments extends Container {

	EntityPlayer thePlayer;

	public ContainerAugments(InventoryPlayer inventory) {

		thePlayer = inventory.player;
		addPlayerInventory(inventory);
		for (int i = 0; i < 5; i++) {
			addSlotToContainer(new SlotPlayerAugment(thePlayer, i, 40 + i * 18 + (i * 2), 26));
		}
	}

	protected void addPlayerInventory(InventoryPlayer inventory) {

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {

		return true;
	}

}
