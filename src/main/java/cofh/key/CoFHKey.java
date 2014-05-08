package cofh.key;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class CoFHKey {
	public static TMap<String, IKeyBinding> keybindModules = new THashMap<String, IKeyBinding>();
	public static TMap<String, Boolean> keybindRepeat = new THashMap<String, Boolean>();
	public static TMap<String, IKeyBinding> serverBinds = new THashMap<String, IKeyBinding>();

	public static boolean addServerKeyBind(IKeyBinding theBind, String keyName) {

		if (!serverBinds.containsKey(keyName)) {
			serverBinds.put(keyName, theBind);
			return true;
		}
		return false;
	}

	public static boolean addKeyBind(IKeyBinding theBind, String keyName) {

		if (!keybindModules.containsKey(keyName)) {
			keybindModules.put(keyName, theBind);
			return true;
		}
		return false;
	}

	@SubscribeEvent
	public void keyPress(KeyInputEvent keyInput) {
		for (Entry<String, IKeyBinding> entry : keybindModules.entrySet()) {
			if (Keyboard.isKeyDown(entry.getValue().getKey())) {
				if (entry.getValue().suppressRepeating()) {
					if (keybindRepeat.get(entry.getValue().getUUID()) == false) {
						keybindRepeat.put(entry.getValue().getUUID(), true);
						entry.getValue().keyPress();
						if (entry.getValue().hasServerSide()) {
							new KeyPacket().sendKeyPacket(entry.getValue()
									.getUUID());
						}
					}
				} else {
					entry.getValue().keyPress();
				}
			} else {
				if (entry.getValue().suppressRepeating()) {
					keybindRepeat.put(entry.getValue().getUUID(), false);
				}
			}
		}
	}

}
