package cofh.gui.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class ContainerFriendsList extends Container {

	public ContainerFriendsList(InventoryPlayer inventory) {

	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {

		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {

		return null;
	}

}
