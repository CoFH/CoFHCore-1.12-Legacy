package cofh.core.key;

import cofh.CoFHCore;
import cofh.api.item.IMultiModeItem;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class KeyBindingItemMultiMode implements IKeyBinding {

	public static KeyBindingItemMultiMode instance = new KeyBindingItemMultiMode();

	@Override
	public String getUUID() {

		return "cofh.multimode";
	}

	@Override
	public int getKey() {

		return CoFHCore.proxy.getKeyBind(getUUID());
	}

	@Override
	public boolean hasServerSide() {

		return true;
	}

	@Override
	public boolean keyPressClient() {

		EntityPlayer player = CoreUtils.getClientPlayer();
		return player != null && ItemHelper.isPlayerHoldingMultiModeItem(player);
	}

	@Override
	public boolean keyPressServer(EntityPlayer player) {

		if (ItemHelper.isPlayerHoldingMultiModeItem(player) && ItemHelper.incrHeldMultiModeItemState(player)) {
			ItemStack heldItem = ItemHelper.getHeldStack(player);
			((IMultiModeItem) heldItem.getItem()).onModeChange(player, heldItem);
			return true;
		}
		return false;
	}

}
