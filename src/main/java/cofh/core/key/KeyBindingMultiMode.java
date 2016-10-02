package cofh.core.key;

import cofh.CoFHCore;
import cofh.api.item.IMultiModeItem;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.entity.player.EntityPlayer;

public class KeyBindingMultiMode implements IKeyBinding {
	public static KeyBindingMultiMode instance = new KeyBindingMultiMode();

	@Override
	public String getUUID() {

		return "cofh.multimode";
	}

	@Override
	public boolean keyPressClient() {

		EntityPlayer player = CoreUtils.getClientPlayer();
		return player != null && ItemHelper.isPlayerHoldingMultiModeItem(player);
	}

	@Override
	public void keyPressServer(EntityPlayer player) {

		//TODO figure out off hand multi mode items (handles main hand only)
		if (ItemHelper.isPlayerHoldingMultiModeItem(player) && ItemHelper.incrHeldMultiModeItemState(player)) {
			((IMultiModeItem) player.getHeldItemMainhand().getItem()).onModeChange(player, player.getHeldItemMainhand());
		}
	}

	@Override
	public int getKey() {

		return CoFHCore.proxy.getKeyBind(getUUID());
	}

	@Override
	public boolean hasServerSide() {

		return true;
	}}
