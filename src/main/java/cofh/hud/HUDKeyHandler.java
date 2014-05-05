package cofh.hud;

import cofh.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import java.util.EnumSet;

class HUDKeyHandler extends KeyHandler {

	static HUDKeyHandler instance = new HUDKeyHandler();

	public HUDKeyHandler() {

		super(new KeyBinding[]{ new KeyBinding("", 0, "") }, new boolean[]{ false });
	}

	@Override
	public String getLabel() {

		return "cofh.hud.keyhandler";
	}

	CoFHKeyBinding keybind;

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {

		if (tickEnd || Minecraft.getMinecraft().currentScreen != null) {
			return;
		}
		keybind = (CoFHKeyBinding) kb;

		if (CoFHHUD.keybindUUIDMap.containsKey(keybind.getName())) {
			if (CoFHHUD.keybindModules.get(CoFHHUD.keybindUUIDMap.get(keybind.getName())).keyDown(keybind.getName(), tickEnd, isRepeat)) {
				PacketHandler.cofhPacketHandler.sendToServer(new CoFHServerKeyHandler().sendKeyPacket(keybind.getName(), false, isRepeat, tickEnd));
			}
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {

		if (tickEnd || Minecraft.getMinecraft().currentScreen != null) {
			return;
		}
		keybind = (CoFHKeyBinding) kb;

		if (CoFHHUD.keybindUUIDMap.containsKey(keybind.getName())) {
			if (CoFHHUD.keybindModules.get(CoFHHUD.keybindUUIDMap.get(keybind.getName())).keyUp(keybind.getName(), tickEnd)) {
				PacketHandler.cofhPacketHandler.sendToServer(new CoFHServerKeyHandler().sendKeyPacket(keybind.getName(), true, false, tickEnd));
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() {

		return EnumSet.of(TickType.CLIENT);
	}

	public void resetBindings() {

		keyBindings = CoFHHUD.keybinds.toArray(new KeyBinding[]{ });

		// Java sucks...
		repeatings = new boolean[CoFHHUD.keybindsRepeat.size()];
		int i = 0;
		for (Boolean bool : CoFHHUD.keybindsRepeat) {
			repeatings[i++] = bool;
		}
	}

}
