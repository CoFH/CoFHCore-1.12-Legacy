package cofh.util;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipeSecure extends ShapedOreRecipe {

	int upgradeSlot = 4;

	public RecipeSecure(ItemStack result, Object[] recipe) {

		super(result, recipe);
	}

	public RecipeSecure(int upgradeSlot, ItemStack result, Object[] recipe) {

		super(result, recipe);
		this.upgradeSlot = upgradeSlot;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting craftMatrix) {

		if (SecurityHelper.isSecure(craftMatrix.getStackInSlot(upgradeSlot))) {
			return null;
		}
		if (craftMatrix.getStackInSlot(upgradeSlot) == null) {
			return super.getCraftingResult(craftMatrix);
		}
		ItemStack secureItem = ItemHelper.copyTag(getRecipeOutput().copy(), craftMatrix.getStackInSlot(upgradeSlot));
		return SecurityHelper.setSecure(secureItem);
	}

}
