package cofh.core.util;

import cofh.api.transport.RegistryEnderAttuned;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;

@SideOnly(Side.CLIENT)
public class TickHandlerEnderRegistry {

	public static TickHandlerEnderRegistry instance = new TickHandlerEnderRegistry();

	public boolean needsMenu = false;

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void tickEnd(GuiOpenEvent evt) {

		if (evt.gui instanceof GuiMainMenu) {
			onMainMenu();
		}
	}

	public void onMainMenu() {

		RegistryEnderAttuned.clear();
	}

}
