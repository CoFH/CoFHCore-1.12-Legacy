package cofh.core.util.crafting;

import cofh.core.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipeSecure extends ShapedOreRecipe {

	int targetSlot = 4;

	public RecipeSecure(ItemStack result, Object... recipe) {

		super(result, recipe);
	}

	public RecipeSecure(int slot, ItemStack result, Object... recipe) {

		super(result, recipe);
		targetSlot = slot;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting craftMatrix) {

		if (SecurityHelper.isSecure(craftMatrix.getStackInSlot(targetSlot))) {
			return ItemStack.EMPTY;
		}
		if (craftMatrix.getStackInSlot(targetSlot).isEmpty()) {
			return super.getCraftingResult(craftMatrix);
		}
		ItemStack secureStack = ItemHelper.copyTag(getRecipeOutput().copy(), craftMatrix.getStackInSlot(targetSlot));
		return SecurityHelper.setSecure(secureStack);
	}

}
