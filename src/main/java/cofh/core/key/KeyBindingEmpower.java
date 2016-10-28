package cofh.core.key;

import cofh.CoFHCore;
import cofh.api.item.IEmpowerableItem;
import cofh.core.key.IKeyBinding;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.ItemHelper;

import net.minecraft.entity.player.EntityPlayer;

public class KeyBindingEmpower implements IKeyBinding {

	public static KeyBindingEmpower instance = new KeyBindingEmpower();

	@Override
	public String getUUID() {

		return "cofh.empower";
	}

	@Override
	public boolean keyPressClient() {

		EntityPlayer player = CoreUtils.getClientPlayer();
		return player != null && ItemHelper.isPlayerHoldingEmpowerableItem(player);
	}

	@Override
	public void keyPressServer(EntityPlayer player) {

		//TODO revisit for use of empowerable items in offhand
		if (ItemHelper.isPlayerHoldingEmpowerableItem(player) && ItemHelper.toggleHeldEmpowerableItemState(player)) {
			((IEmpowerableItem) player.getHeldItemMainhand().getItem()).onStateChange(player, player.getHeldItemMainhand());
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
