package cofh.core.key;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.MouseInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;

public class KeyHandlerCore {

	private static TMap<String, IKeyBinding> clientBinds = new THashMap<>();
	static TMap<String, IKeyBinding> serverBinds = new THashMap<>();
	private static ArrayList<IKeyBinding> keys = new ArrayList<>();

	static {
		MinecraftForge.EVENT_BUS.register(new KeyHandlerCore());
	}

	public static boolean addClientKeyBind(IKeyBinding binding) {

		if (!clientBinds.containsKey(binding.getUUID())) {
			keys.add(binding);
			clientBinds.put(binding.getUUID(), binding);
			return true;
		}
		return false;
	}

	public static boolean addServerKeyBind(IKeyBinding binding) {

		if (!serverBinds.containsKey(binding.getUUID())) {
			serverBinds.put(binding.getUUID(), binding);
			return true;
		}
		return false;
	}

	/* EVENT HANDLING */
	@SubscribeEvent
	@SideOnly (Side.CLIENT)
	public void handleKeyInputEvent(KeyInputEvent event) {

		for (IKeyBinding key : keys) {
			int button = key.getKey();
			if (button > 0 && Keyboard.isKeyDown(button)) {
				if (key.keyPressClient() && key.hasServerSide()) {
					PacketKey.sendToServer(key.getUUID());
				}
			}
		}
	}

	@SubscribeEvent
	@SideOnly (Side.CLIENT)
	public void handleMouseInputEvent(MouseInputEvent event) {

		for (IKeyBinding key : keys) {
			int button = key.getKey(); // value saved as button - 100 instead of -button because moderp
			if (button < 0 && Mouse.isButtonDown(button + 100)) {
				if (key.keyPressClient() && key.hasServerSide()) {
					PacketKey.sendToServer(key.getUUID());
				}
			}
		}
	}

}
