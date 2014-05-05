package cofh.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TickHandlerEnderRegistry {

	public static TickHandlerEnderRegistry instance = new TickHandlerEnderRegistry();

	public boolean needsMenu = false;

	@SubscribeEvent
	public void tickEnd(ClientTickEvent evt) {

		Minecraft mc = Minecraft.getMinecraft();

		if (evt.phase == Phase.END) {
			if (mc.currentScreen instanceof GuiMainMenu) {
				if (needsMenu) {
					onMainMenu();
					needsMenu = false;
				}
			} else if (mc.inGameHasFocus) {
				needsMenu = true;
			}
		}
	}

	public void onMainMenu() {

		RegistryEnderAttuned.clear();
	}
}
