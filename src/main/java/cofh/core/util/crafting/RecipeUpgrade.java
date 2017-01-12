package cofh.core.util.crafting;

import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipeUpgrade extends ShapedOreRecipe {

	int targetSlot = 4;

	public RecipeUpgrade(ItemStack result, Object[] recipe) {

		super(result, recipe);
	}

	public RecipeUpgrade(int slot, ItemStack result, Object[] recipe) {

		super(result, recipe);
		targetSlot = slot;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting craftMatrix) {

		if (craftMatrix.getStackInSlot(targetSlot) == null || craftMatrix.getStackInSlot(targetSlot).getTagCompound() == null) {
			return super.getCraftingResult(craftMatrix);
		}
		return ItemHelper.copyTag(getRecipeOutput().copy(), craftMatrix.getStackInSlot(targetSlot));
	}

}
