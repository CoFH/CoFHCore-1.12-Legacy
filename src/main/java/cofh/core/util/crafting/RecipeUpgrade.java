package cofh.core.util.crafting;

import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipeUpgrade extends ShapedOreRecipe {

	int targetSlot = 4;

	// TODO: FIXME. Obviously do NOT PASS NULL.
	public RecipeUpgrade(ItemStack result, Object... recipe) {

		super(null, result, recipe);
	}

	public RecipeUpgrade(int slot, ItemStack result, Object... recipe) {

		super(null, result, recipe);
		targetSlot = slot;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting craftMatrix) {

		if (craftMatrix.getStackInSlot(targetSlot).isEmpty() || craftMatrix.getStackInSlot(targetSlot).getTagCompound() == null) {
			return super.getCraftingResult(craftMatrix);
		}
		return ItemHelper.copyTag(getRecipeOutput().copy(), craftMatrix.getStackInSlot(targetSlot));
	}

}
