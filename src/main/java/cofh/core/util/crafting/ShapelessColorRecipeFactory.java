package cofh.core.util.crafting;

import cofh.api.item.IColorableItem;
import cofh.core.util.helpers.ColorHelper;
import cofh.core.util.helpers.ItemHelper;
import com.google.gson.JsonObject;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import javax.annotation.Nonnull;

public class ShapelessColorRecipeFactory implements IRecipeFactory {

	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {

		ShapelessOreRecipe recipe = ShapelessOreRecipe.factory(context, json);

		return new ShapelessColorRecipe(new ResourceLocation("cofh", "color_shapeless"), recipe.getRecipeOutput(), recipe.getIngredients().toArray());
	}

	/* RECIPE */
	public static class ShapelessColorRecipe extends ShapelessOreRecipe {

		public ShapelessColorRecipe(ResourceLocation group, ItemStack result, Object... recipe) {

			super(group, result, recipe);
		}

		@Override
		@Nonnull
		public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {

			System.out.println("here");

			ItemStack dyeStack = ItemStack.EMPTY;
			ItemStack dyeStack2 = ItemStack.EMPTY;
			ItemStack inputStack = ItemStack.EMPTY;
			ItemStack outputStack = output.copy();

			int dyeIndex = 0;
			int colorableIndex = 0;

			for (int i = 0; i < inv.getSizeInventory(); ++i) {
				ItemStack stack = inv.getStackInSlot(i);
				if (!stack.isEmpty()) {
					if (ColorHelper.isDye(stack) && dyeStack.isEmpty()) {
						dyeStack = stack.copy();
						dyeIndex = i;
					} else if (ColorHelper.isDye(stack)) {
						dyeStack2 = stack.copy();
					}
					if (stack.getItem() instanceof IColorableItem) {
						inputStack = stack;
						colorableIndex = i;
					}
				}
			}
			if (dyeStack.isEmpty() || inputStack.isEmpty()) {
				return ItemStack.EMPTY;
			}
			outputStack = ItemHelper.copyTag(outputStack, inputStack);
			IColorableItem colorableItem = ((IColorableItem) outputStack.getItem());

			if (dyeStack2.isEmpty()) {
				colorableItem.applyColor(outputStack, ColorHelper.getDyeColor(dyeStack), dyeIndex < colorableIndex ? 0 : 1);
			} else {
				colorableItem.applyColor(outputStack, ColorHelper.getDyeColor(dyeStack), 0);
				colorableItem.applyColor(outputStack, ColorHelper.getDyeColor(dyeStack2), 1);
			}
			return outputStack;
		}

		@Override
		public boolean isDynamic() {

			return true;
		}
	}

}
