package cofh.social;

import cofh.gui.client.GuiFriendsList;

import net.minecraft.client.Minecraft;

public class ProxyClient extends Proxy {

	@Override
	public void updateFriendListGui() {

		if (Minecraft.getMinecraft().currentScreen != null) {
			((GuiFriendsList) Minecraft.getMinecraft().currentScreen).taFriendsList.textLines = RegistryFriends.clientPlayerFriends;
		}
	}

}
