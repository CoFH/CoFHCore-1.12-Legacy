package cofh.core.key;

import cofh.CoFHCore;
import cofh.core.gui.GuiHandler;
import net.minecraft.entity.player.EntityPlayer;

public class KeyBindingPlayerAugments implements IKeyBinding {

	public static final KeyBindingPlayerAugments INSTANCE = new KeyBindingPlayerAugments();

	@Override
	public String getUUID() {

		return "cofh.augments";
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

		return true;
	}

	@Override
	public boolean keyPressServer(EntityPlayer player) {

		player.openGui(CoFHCore.instance, GuiHandler.AUGMENTS_ID, player.world, (int) player.posX, (int) player.posY, (int) player.posZ);
		return true;
	}

}
