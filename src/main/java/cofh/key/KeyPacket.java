package cofh.key;

import net.minecraft.entity.player.EntityPlayer;
import cofh.CoFHCore;
import cofh.network.CoFHPacket;
import cofh.network.PacketHandler;

public class KeyPacket extends CoFHPacket {

	public static void initialize() {

		PacketHandler.instance.registerPacket(KeyPacket.class);
	}

	@Override
	public void handlePacket(EntityPlayer player) {

		String bindUUID = getString();
		if (CoFHKey.serverBinds.containsKey(bindUUID)) {
			CoFHKey.serverBinds.get(bindUUID).keyPressServer(player);
		} else {
			CoFHCore.log.error("Invalid Key Packet! Unregistered Server Key! UUID: " + bindUUID);
		}
	}

	public void sendKeyPacket(String uuid) {

		addString(uuid);
		PacketHandler.sendToServer(this);
	}
}
