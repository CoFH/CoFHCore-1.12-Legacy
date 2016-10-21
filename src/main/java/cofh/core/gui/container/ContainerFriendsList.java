package cofh.core.gui.container;

import cofh.lib.gui.container.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerFriendsList extends ContainerBase {

    public ContainerFriendsList(InventoryPlayer inventory) {

    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {

        return true;
    }

    @Override
    public boolean supportsShiftClick(int slotIndex) {

        return false;
    }

    @Override
    protected int getPlayerInventoryVerticalOffset() {

        return 0;
    }

    @Override
    protected int getSizeInventory() {

        return 0;
    }

}
