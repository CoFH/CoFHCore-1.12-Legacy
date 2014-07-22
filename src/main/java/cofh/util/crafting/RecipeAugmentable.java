package cofh.util.crafting;

import cofh.util.AugmentHelper;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipeAugmentable extends ShapedOreRecipe {

	ItemStack[] augments;

	public RecipeAugmentable(ItemStack result, ItemStack[] augments, Object[] recipe) {

		super(result, recipe);
		this.augments = augments;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting craftMatrix) {

		ItemStack retStack = getRecipeOutput().copy();
		AugmentHelper.writeAugments(retStack, augments);

		return retStack;
	}

}
