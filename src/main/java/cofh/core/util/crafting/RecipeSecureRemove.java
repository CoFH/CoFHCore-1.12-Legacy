package cofh.core.util.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeSecureRemove extends ShapelessOreRecipe {

    public RecipeSecureRemove(ItemStack result, Object[] recipe) {

        super(result, recipe);
    }

    // @Override
    // public ItemStack getCraftingResult(InventoryCrafting craftMatrix) {
    //
    // ItemStack secureStack;
    // ItemStack tool;
    //
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
