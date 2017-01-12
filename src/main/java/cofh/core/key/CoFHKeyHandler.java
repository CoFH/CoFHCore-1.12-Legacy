package cofh.core.key;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.MouseInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;

public class CoFHKeyHandler {

	static {
		FMLCommonHandler.instance().bus().register(new CoFHKeyHandler());
	}

	private static TMap<String, IKeyBinding> keybindModules = new THashMap<String, IKeyBinding>();
	// public static TMap<String, Boolean> keybindRepeat = new THashMap<String, Boolean>();
	static TMap<String, IKeyBinding> serverBinds = new THashMap<String, IKeyBinding>();

	private static ArrayList<IKeyBinding> keys = new ArrayList<IKeyBinding>();

	public static boolean addServerKeyBind(IKeyBinding theBind) {

		if (!serverBinds.containsKey(theBind.getUUID())) {
			serverBinds.put(theBind.getUUID(), theBind);
			return true;
		}
		return false;
	}

	public static boolean addKeyBind(IKeyBinding theBind) {

		if (!keybindModules.containsKey(theBind.getUUID())) {
			keys.add(theBind);
			keybindModules.put(theBind.getUUID(), theBind);
			return true;
		}
		return false;
	}

	@SubscribeEvent
	@SideOnly (Side.CLIENT)
	public void keyPress(KeyInputEvent keyInput) {

		for (int i = 0, e = keys.size(); i < e; ++i) {
			IKeyBinding key = keys.get(i);
			int button = key.getKey();
			if (button > 0 && Keyboard.isKeyDown(button)) {
				if (key.keyPress() && key.hasServerSide()) {
					KeyPacket.sendToServer(key.getUUID());
				}
			}
		}
	}

	@SubscribeEvent
	@SideOnly (Side.CLIENT)
	public void keyPress(MouseInputEvent keyInput) {

		for (int i = 0, e = keys.size(); i < e; ++i) {
			IKeyBinding key = keys.get(i);
			int button = key.getKey(); // value saved as button - 100 instead of -button because moderp
			if (button < 0 && Mouse.isButtonDown(button + 100)) {
				if (key.keyPress() && key.hasServerSide()) {
					KeyPacket.sendToServer(key.getUUID());
				}
			}
		}
	}

}
