package cofh.core.key;

import cofh.CoFHCore;
import cofh.core.network.PacketBase;
import cofh.core.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;

public class PacketKey extends PacketBase {

	public static void initialize() {

		PacketHandler.INSTANCE.registerPacket(PacketKey.class);
	}

	public static void sendToServer(String uuid) {

		PacketHandler.sendToServer(new PacketKey(uuid));
	}

	public PacketKey() {

	}

	protected PacketKey(String uuid) {

		addString(uuid);
	}

	@Override
	public void handlePacket(EntityPlayer player, boolean isServer) {

		String bindUUID = getString();
		if (KeyHandlerCore.serverBinds.containsKey(bindUUID)) {
			KeyHandlerCore.serverBinds.get(bindUUID).keyPressServer(player);
		} else {
			CoFHCore.LOG.error("Invalid Key Packet! Unregistered Server Key! UUID: " + bindUUID);
		}
	}

	public void sendKeyPacket(String uuid) {

		addString(uuid);
		PacketHandler.sendToServer(this);
	}

}
