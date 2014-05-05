package cofh.hud;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import cofh.CoFHCore;
import cofh.network.PacketHandler;

public class CoFHServerKeyHandler implements IGeneralPacketHandler {

	public static final CoFHServerKeyHandler instance = new CoFHServerKeyHandler();
	public static final HashMap<String, IKeyBinding> serverBinds = new HashMap<String, IKeyBinding>();
	public static int packetId;

	public static void initialize() {

		packetId = PacketHandler.getAvailablePacketIdAndRegister(instance);
	}

	public static boolean addServerKeyBind(IKeyBinding theBind, String keyName) {

		if (!serverBinds.containsKey(keyName)) {
			serverBinds.put(keyName, theBind);
			return true;
		}
		return false;
	}

	@Override
	public void handlePacket(int id, Payload payload, EntityPlayer player) throws Exception {

		String bindUUID = payload.getString();
		if (serverBinds.containsKey(bindUUID)) {
			if (payload.getBool()) {
				serverBinds.get(bindUUID).keyUpServer(bindUUID, payload.getBool(), player);
			} else {
				serverBinds.get(bindUUID).keyDownServer(bindUUID, payload.getBool(), payload.getBool(), player);
			}
		} else {
			CoFHCore.log.error("Invalid Key Packet! Unregistered Server Key! UUID: " + bindUUID);
		}
	}

	public static void sendKeyPacket(String key, boolean keyUp, boolean isRepeat, boolean tickEnd) {

		Payload payload = Payload.getPayload(packetId);
		payload.addString(key);
		payload.addBool(keyUp);
		payload.addBool(tickEnd);

		if (!keyUp) {
			payload.addBool(isRepeat);
		}
		PacketUtils.sendToServer(payload.getPacket());
	}

}
