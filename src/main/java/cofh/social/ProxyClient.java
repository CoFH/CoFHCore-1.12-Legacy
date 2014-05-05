package cofh.social;

import net.minecraft.client.Minecraft;
import cofh.gui.client.GuiFriendsList;

public class ProxyClient extends Proxy {

	@Override
	public void updateFriendListGui() {

		if (Minecraft.getMinecraft().currentScreen != null) {
			((GuiFriendsList) Minecraft.getMinecraft().currentScreen).taFriendsList.textLines = RegistryFriends.clientPlayerFriends;
		}
	}

}
