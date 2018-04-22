package cofh.core.util.crafting;

import cofh.CoFHCore;
import cofh.api.item.IColorableItem;
import cofh.core.util.helpers.ColorHelper;
import cofh.core.util.helpers.ItemHelper;
import com.google.gson.JsonObject;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;

public class ShapedColorRecipeFactory implements IRecipeFactory {

	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {

		ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);

		ShapedPrimer primer = new ShapedPrimer();
		primer.width = recipe.getRecipeWidth();
		primer.height = recipe.getRecipeHeight();
		primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
		primer.input = recipe.getIngredients();

		return new ShapedColorRecipe(new ResourceLocation(CoFHCore.MOD_ID, "color_shaped"), recipe.getRecipeOutput(), primer);
	}

	/* RECIPE */
	public static class ShapedColorRecipe extends ShapedOreRecipe {

		public ShapedColorRecipe(ResourceLocation group, ItemStack result, ShapedPrimer primer) {

			super(group, result, primer);
		}

		@Override
		@Nonnull
		public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {

			ItemStack dyeStack = ItemStack.EMPTY;
			ItemStack inputStack = ItemStack.EMPTY;
			ItemStack outputStack = output.copy();

			int dyeIndex = 0;
			int colorableIndex = 0;

			for (int i = 0; i < inv.getSizeInventory(); ++i) {
				ItemStack stack = inv.getStackInSlot(i);
				if (!stack.isEmpty()) {
					if (ColorHelper.isDye(stack)) {
						dyeStack = stack.copy();
						dyeIndex = i;
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
			((IColorableItem) outputStack.getItem()).applyColor(outputStack, ColorHelper.getDyeColor(dyeStack), dyeIndex < colorableIndex ? 0 : 1);

			return outputStack;
		}

		@Override
		public boolean isDynamic() {

			return true;
		}
	}

}
