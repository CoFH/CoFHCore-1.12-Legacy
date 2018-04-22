package cofh.core.util.crafting;

import cofh.api.item.IColorableItem;
import cofh.core.util.crafting.ShapelessFluidRecipeFactory.ShapelessFluidRecipe;
import com.google.gson.JsonObject;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import javax.annotation.Nonnull;

public class ShapelessColorRemoveRecipeFactory implements IRecipeFactory {

	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {

		ShapelessOreRecipe recipe = ShapelessOreRecipe.factory(context, json);

		return new ShapelessColorRemoveRecipe(new ResourceLocation("cofh", "color_remove_shapeless"), recipe.getRecipeOutput(), recipe.getIngredients().toArray());
	}

	/* RECIPE */
	public static class ShapelessColorRemoveRecipe extends ShapelessFluidRecipe {

		public ShapelessColorRemoveRecipe(ResourceLocation group, ItemStack result, Object... recipe) {

			super(group, result, recipe);
		}

		@Override
		@Nonnull
		public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {

			if (!(output.getItem() instanceof IColorableItem)) {
				return ItemStack.EMPTY;
			}
			ItemStack outputStack = output.copy();
			((IColorableItem) outputStack.getItem()).removeColor(outputStack);

			return outputStack;
		}
	}

}
