package cofh.hud;

import cofh.CoFHCore;
import cofh.network.CoFHPacket;
import cofh.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;

public class CoFHServerKeyHandler extends CoFHPacket {

	public static final CoFHServerKeyHandler instance = new CoFHServerKeyHandler();
	public static final HashMap<String, IKeyBinding> serverBinds = new HashMap<String, IKeyBinding>();

	public static void initialize() {

		PacketHandler.cofhPacketHandler.registerPacket(CoFHServerKeyHandler.class);
	}

	public static boolean addServerKeyBind(IKeyBinding theBind, String keyName) {

		if (!serverBinds.containsKey(keyName)) {
			serverBinds.put(keyName, theBind);
			return true;
		}
		return false;
	}

	@Override
	public void handlePacket(EntityPlayer player) {

		String bindUUID = getString();
		if (serverBinds.containsKey(bindUUID)) {
			if (getBool()) {
				serverBinds.get(bindUUID).keyUpServer(bindUUID, getBool(), player);
			} else {
				serverBinds.get(bindUUID).keyDownServer(bindUUID, getBool(), getBool(), player);
			}
		} else {
			CoFHCore.log.error("Invalid Key Packet! Unregistered Server Key! UUID: " + bindUUID);
		}
	}

	public CoFHServerKeyHandler sendKeyPacket(String key, boolean keyUp, boolean isRepeat, boolean tickEnd) {

		addString(key);
		addBool(keyUp);
		addBool(tickEnd);

		if (!keyUp) {
			addBool(isRepeat);
		}
		return this;
	}

}
