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

		super(parentScreen, getConfigElements(parentScreen), CoFHCore.MOD_ID, false, false, CoFHCore.MOD_NAME);
	}

	public static final String[] CATEGORIES_CLIENT = { "General", "Global", "Interface", "Security", "Tab" };
	public static final String[] CATEGORIES_CORE = {};
	public static final String[] CATEGORIES_LOOT = { "General", "Heads" };

	private static List<IConfigElement> getConfigElements(GuiScreen parent) {

		List<IConfigElement> list = new ArrayList<>();

		list.add(new DummyCategoryElement("Client", "config.client", getClientConfigElements()));
		list.add(new DummyCategoryElement("Core", "config.core", getCoreModuleConfigElements()));
		list.add(new DummyCategoryElement("Loot", "config.loot", getLootModuleConfigElements()));

		return list;
	}

	private static List<IConfigElement> getClientConfigElements() {

		List<IConfigElement> list = new ArrayList<>();

		for (String category : CATEGORIES_CLIENT) {
			list.add(new ConfigElement(CoFHCore.CONFIG_CLIENT.getCategory(category)));
		}
		return list;
	}

	private static List<IConfigElement> getCoreModuleConfigElements() {

		List<IConfigElement> list = new ArrayList<>();

		for (String category : CATEGORIES_CORE) {
			list.add(new ConfigElement(CoFHCore.CONFIG_CORE.getCategory(category)));
		}
		return list;
	}

	private static List<IConfigElement> getLootModuleConfigElements() {

		List<IConfigElement> list = new ArrayList<>();

		for (String category : CATEGORIES_LOOT) {
			list.add(new ConfigElement(CoFHCore.CONFIG_LOOT.getCategory(category)));
		}
		return list;
	}

}
