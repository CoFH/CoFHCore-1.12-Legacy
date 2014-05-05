package cofh.hud;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CoFHHUD {

	public static List<IHUDModule> modules = new LinkedList<IHUDModule>();
	public static TMap<String, IKeyBinding> keybindModules = new THashMap<String, IKeyBinding>();
	public static TMap<String, String> keybindUUIDMap = new THashMap<String, String>();

	static boolean initialized = false;
	static boolean keyInit = false;
	static int moduleID = 0;

	// Used to add all of our keybinds to one keybind at the end of init
	public static List<KeyBinding> keybinds = new LinkedList<KeyBinding>();
	public static List<Boolean> keybindsRepeat = new LinkedList<Boolean>();

	public static void addKeybind(IKeyBinding keybind, CoFHKeyBinding bind, boolean triggerRepeat) {

		if (!keybindModules.containsKey(keybind.getUUID())) {
			keybindModules.put(keybind.getUUID(), keybind);
		}
		if (bind != null && !keybinds.contains(bind)) {
			keybindUUIDMap.put(bind.getName(), keybind.getUUID());
			keybinds.add(bind);
			keybindsRepeat.add(triggerRepeat);
			if (HUDKeyHandler.instance != null) {
				HUDKeyHandler.instance.resetBindings();
			}
			if (!keyInit) {
				keyInit = true;
				KeyBindingRegistry.registerKeyBinding(HUDKeyHandler.instance);
			}
		}
	}

	public static int registerHUDModule(IHUDModule module) {

		if (module != null) {
			if (!modules.contains(module)) {
				modules.add(module);
				module.setModuleID(moduleID++);
				if (!initialized) {
					initialized = true;
					MinecraftForge.EVENT_BUS.register(HUDRenderHandler.instance);
				}
			}
			return 0;
		} else {
			if (!initialized) {
				initialized = true;
				MinecraftForge.EVENT_BUS.register(HUDRenderHandler.instance);
			}
			return moduleID++;
		}
	}

}
