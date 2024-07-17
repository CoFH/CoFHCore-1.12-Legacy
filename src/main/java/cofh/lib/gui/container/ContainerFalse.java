package cofh.lib.gui.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

/**
 * Basic false container class. You'll know if you need one.
 *
 * @author King Lemming
 *
 */
public final class ContainerFalse extends Container {

	@Override
	public boolean canInteractWith(EntityPlayer player) {

		return false;
	}

}
