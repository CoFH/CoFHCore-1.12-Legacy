package cofh.core.util;

import cofh.api.item.IEmpowerableItem;
import cofh.core.key.IKeyBinding;
import cofh.lib.util.helpers.ItemHelper;

import net.minecraft.entity.player.EntityPlayer;

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
			((IEmpowerableItem) player.getCurrentEquippedItem().getItem()).onStateChange(player, player.getCurrentEquippedItem());
		}
	}

	@Override
	public int getKey() {

		return 0x2F;
	}

	@Override
	public boolean hasServerSide() {

		return true;
	}

}
