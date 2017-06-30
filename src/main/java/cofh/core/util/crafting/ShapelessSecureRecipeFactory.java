package cofh.core.util.crafting;

import cofh.api.item.INBTCopyIngredient;
import cofh.core.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.ItemHelper;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import javax.annotation.Nonnull;

public class ShapelessSecureRecipeFactory implements IRecipeFactory {

	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {

		ShapelessOreRecipe recipe = ShapelessOreRecipe.factory(context, json);

		return new ShapelessSecureRecipe(new ResourceLocation("cofh", "secure_shapeless"), recipe.getRecipeOutput(), recipe.getIngredients().toArray());
	}

	/* RECIPE */
	public static class ShapelessSecureRecipe extends ShapelessOreRecipe {

		public ShapelessSecureRecipe(ResourceLocation group, ItemStack result, Object... recipe) {

			super(group, result, recipe);
		}

		@Override
		@Nonnull
		public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {

			ItemStack inputStack = ItemStack.EMPTY;
			ItemStack outputStack = output.copy();

			for (int i = 0; i < inv.getSizeInventory(); ++i) {
				ItemStack stack = inv.getStackInSlot(i);

				if (!stack.isEmpty()) {
					if (stack.getItem() instanceof INBTCopyIngredient) {
						inputStack = stack;
					} else if (Block.getBlockFromItem(stack.getItem()) instanceof INBTCopyIngredient) {
						inputStack = stack;
					}
				}
			}
			if (SecurityHelper.isSecure(inputStack)) {
				return ItemStack.EMPTY;
			}
			outputStack = ItemHelper.copyTag(outputStack, inputStack);
			return SecurityHelper.setSecure(outputStack);
		}
	}

}
