package cofh.core.util.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeReset extends ShapelessOreRecipe {

    public RecipeReset(ItemStack result, Object[] recipe) {

        super(result, recipe);
    }

    // @Override
    // public ItemStack getCraftingResult(InventoryCrafting craftMatrix) {
    //
    // if (getRecipeSize() > 1) {
    // return getRecipeOutput();
    // }
    // ItemStack secureStack;
    // for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
    // secureStack = craftMatrix.getStackInSlot(i);
    // if (secureStack != null) {
    // if (SecurityHelper.isSecure(secureStack)) {
    //
    // }
    // }
    // }
    // }
}
