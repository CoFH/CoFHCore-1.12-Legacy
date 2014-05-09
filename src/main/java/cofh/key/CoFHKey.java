package cofh.key;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;

public class CoFHKey {

	public static TMap<String, IKeyBinding> keybindModules = new THashMap<String, IKeyBinding>();
	public static TMap<String, Boolean> keybindRepeat = new THashMap<String, Boolean>();
	public static TMap<String, IKeyBinding> serverBinds = new THashMap<String, IKeyBinding>();

	public static boolean addServerKeyBind(IKeyBinding theBind) {

		if (!serverBinds.containsKey(theBind.getUUID())) {
			serverBinds.put(theBind.getUUID(), theBind);
			return true;
		}
		return false;
	}

	public static boolean addKeyBind(IKeyBinding theBind) {

		if (!keybindModules.containsKey(theBind.getUUID())) {
			keybindModules.put(theBind.getUUID(), theBind);
			return true;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void keyPress(KeyInputEvent keyInput) {

		for (Entry<String, IKeyBinding> entry : keybindModules.entrySet()) {
			if (Keyboard.isKeyDown(entry.getValue().getKey())) {
				if (entry.getValue().keyPress() && entry.getValue().hasServerSide()) {
					new KeyPacket().sendKeyPacket(entry.getValue().getUUID());
				}
			}
		}
	}

}
