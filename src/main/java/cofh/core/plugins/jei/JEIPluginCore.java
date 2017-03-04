package cofh.core.plugins.jei;

import cofh.core.item.tool.*;
import cofh.lib.util.helpers.ItemHelper;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import net.minecraft.item.Item;

@JEIPlugin
public class JEIPluginCore implements IModPlugin {

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
		for (Item item : Item.REGISTRY) {
			if (item instanceof ItemToolMulti ||
					item instanceof ItemBowMulti ||
					item instanceof ItemShearsMulti ||
					item instanceof ItemShieldMulti ||
					item instanceof ItemFishingRodMulti ||
					item instanceof ItemHoeMulti ||
					item instanceof ItemSwordMulti
					) {
				subtypeRegistry.registerSubtypeInterpreter(item, itemStack -> String.valueOf(ItemHelper.getItemDamage(itemStack)));
			}
		}
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
