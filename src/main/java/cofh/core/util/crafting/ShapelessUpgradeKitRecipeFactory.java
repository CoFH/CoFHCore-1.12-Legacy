package cofh.core.util.crafting;

import cofh.api.item.INBTCopyIngredient;
import cofh.api.item.IUpgradeItem.UpgradeType;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.ItemHelper;
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

public class ShapelessUpgradeKitRecipeFactory implements IRecipeFactory {

	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {

		ShapelessOreRecipe recipe = ShapelessOreRecipe.factory(context, json);

		return new ShapelessUpgradeKitRecipe(new ResourceLocation("cofh", "upgrade_kit_shapeless"), recipe.getRecipeOutput(), recipe.getIngredients().toArray());
	}

	/* RECIPE */
	public static class ShapelessUpgradeKitRecipe extends ShapelessOreRecipe {

		public ShapelessUpgradeKitRecipe(ResourceLocation group, ItemStack result, Object... recipe) {

			super(group, result, recipe);
		}

		@Override
		@Nonnull
		public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {

			ItemStack inputStack = ItemStack.EMPTY;
			ItemStack outputStack = output.copy();
			ItemStack upgradeStack = ItemStack.EMPTY;

			for (int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack stack = inv.getStackInSlot(i);

				if (!stack.isEmpty()) {
					if (stack.getItem() instanceof INBTCopyIngredient) {
						inputStack = stack;
					} else if (Block.getBlockFromItem(stack.getItem()) instanceof INBTCopyIngredient) {
						inputStack = stack;
					} else if (AugmentHelper.isUpgradeItem(stack)) {
						upgradeStack = stack;
					}
				}
			}
			if (inputStack.isEmpty() || upgradeStack.isEmpty()) {
				return ItemStack.EMPTY;
			}
			int curLevel = AugmentHelper.getLevel(inputStack);
			int upgradeLevel = AugmentHelper.getUpgradeLevel(upgradeStack);
			UpgradeType upgradeType = AugmentHelper.getUpgradeType(upgradeStack);

			if (upgradeLevel <= curLevel || upgradeType == UpgradeType.INCREMENTAL && curLevel + 1 != upgradeLevel) {
				return ItemStack.EMPTY;
			}
			outputStack = ItemHelper.copyTag(outputStack, inputStack);

			if (upgradeType == UpgradeType.CREATIVE) {
				return AugmentHelper.setCreative(outputStack);
			}
			return AugmentHelper.setLevel(outputStack, upgradeLevel);
		}
	}

}
