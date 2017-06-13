package cofh.core.plugins.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class JEIPluginCore extends BlankModPlugin {

	@Override
	public void register(IModRegistry registry) {

		registry.addAdvancedGuiHandlers(new SlotMover());
	}
}
