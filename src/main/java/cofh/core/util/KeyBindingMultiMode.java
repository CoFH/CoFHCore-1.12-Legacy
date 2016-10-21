package cofh.core.util;

import codechicken.lib.util.ItemUtils;
import cofh.CoFHCore;
import cofh.api.item.IMultiModeItem;
import cofh.core.key.IKeyBinding;
import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class KeyBindingMultiMode implements IKeyBinding {

    public static KeyBindingMultiMode instance = new KeyBindingMultiMode();

    @Override
    public String getUUID() {

        return "cofh.multimode";
    }

    @Override
    public boolean keyPress() {

        EntityPlayer player = CoreUtils.getClientPlayer();
        return player != null && ItemHelper.isPlayerHoldingMultiModeItem(player);
    }

    @Override
    public void keyPressServer(EntityPlayer player) {

        if (ItemHelper.isPlayerHoldingMultiModeItem(player) && ItemHelper.incrHeldMultiModeItemState(player)) {
            ItemStack heldItem = ItemUtils.getHeldStack(player);
            ((IMultiModeItem) heldItem.getItem()).onModeChange(player, heldItem);
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
