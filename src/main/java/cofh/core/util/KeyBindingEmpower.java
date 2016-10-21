package cofh.core.util;

import codechicken.lib.util.ItemUtils;
import cofh.CoFHCore;
import cofh.api.item.IEmpowerableItem;
import cofh.core.key.IKeyBinding;
import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class KeyBindingEmpower implements IKeyBinding {

    public static KeyBindingEmpower instance = new KeyBindingEmpower();

    @Override
    public String getUUID() {

        return "cofh.empower";
    }

    @Override
    public boolean keyPress() {

        EntityPlayer player = CoreUtils.getClientPlayer();
        return player != null && ItemHelper.isPlayerHoldingEmpowerableItem(player);
    }

    @Override
    public void keyPressServer(EntityPlayer player) {

        if (ItemHelper.isPlayerHoldingEmpowerableItem(player) && ItemHelper.toggleHeldEmpowerableItemState(player)) {
            ItemStack heldStack = ItemUtils.getHeldStack(player);
            ((IEmpowerableItem) heldStack.getItem()).onStateChange(player, heldStack);
        }
    }

    @Override
    public int getKey() {

        return CoFHCore.proxy.getKeyBind(getUUID());
    }

    @Override
    public boolean hasServerSide() {

        return true;
    }

}
