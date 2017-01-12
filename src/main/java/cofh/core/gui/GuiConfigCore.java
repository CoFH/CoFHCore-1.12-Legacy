package cofh.core.gui;

import cofh.CoFHCore;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GuiConfigCore extends GuiConfig {

	public GuiConfigCore(GuiScreen parentScreen) {

		super(parentScreen, getConfigElements(parentScreen), CoFHCore.modId, false, false, CoFHCore.modName);
	}

	public static final String[] CATEGORIES_CLIENT = { "General", "Global", "Interface", "Security", "Tab" };
	public static final String[] CATEGORIES_CORE = {};
	public static final String[] CATEGORIES_LOOT = { "General", "Heads" };

	private static List<IConfigElement> getConfigElements(GuiScreen parent) {

		List<IConfigElement> list = new ArrayList<IConfigElement>();

		list.add(new DummyCategoryElement("Client", "config.Client", getClientConfigElements()));
		list.add(new DummyCategoryElement("Core", "config.Core", getCoreModuleConfigElements()));
		list.add(new DummyCategoryElement("Loot", "config.Loot", getLootModuleConfigElements()));

		return list;
	}

	private static List<IConfigElement> getClientConfigElements() {

		List<IConfigElement> list = new ArrayList<IConfigElement>();

		for (int i = 0; i < CATEGORIES_CLIENT.length; i++) {
			list.add(new ConfigElement(CoFHCore.configClient.getCategory(CATEGORIES_CLIENT[i])));
		}
		return list;
	}

	private static List<IConfigElement> getCoreModuleConfigElements() {

		List<IConfigElement> list = new ArrayList<IConfigElement>();

		for (int i = 0; i < CATEGORIES_CORE.length; i++) {
			list.add(new ConfigElement(CoFHCore.configCore.getCategory(CATEGORIES_CORE[i])));
		}
		return list;
	}

	private static List<IConfigElement> getLootModuleConfigElements() {

		List<IConfigElement> list = new ArrayList<IConfigElement>();

		for (int i = 0; i < CATEGORIES_LOOT.length; i++) {
			list.add(new ConfigElement(CoFHCore.configLoot.getCategory(CATEGORIES_LOOT[i])));
		}
		return list;
	}

}
