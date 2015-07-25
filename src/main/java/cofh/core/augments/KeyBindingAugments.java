package cofh.core.augments;

import cofh.CoFHCore;
import cofh.core.gui.GuiHandler;
import cofh.core.key.IKeyBinding;

import net.minecraft.entity.player.EntityPlayer;

public class KeyBindingAugments implements IKeyBinding {

	public static KeyBindingAugments instance = new KeyBindingAugments();

	@Override
	public boolean keyPress() {

		return true;
	}

	@Override
	public void keyPressServer(EntityPlayer player) {

		player.openGui(CoFHCore.instance, GuiHandler.AUGMENTS_ID, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
	}

	@Override
	public String getUUID() {

		return "cofh.augment";
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
