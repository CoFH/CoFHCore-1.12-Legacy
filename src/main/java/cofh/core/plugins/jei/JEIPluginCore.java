package cofh.core.plugins.jei;

import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;

@JEIPlugin
public class JEIPluginCore implements IModPlugin {

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {

	}

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {

	}

	@Override
	public void register(IModRegistry registry) {

		registry.addAdvancedGuiHandlers(new SlotMover());
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

	}

}
