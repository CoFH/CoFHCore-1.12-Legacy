package cofh.core.util.crafting;

import cofh.core.util.helpers.ItemHelper;
import com.google.gson.JsonObject;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ShapedFluidRecipeFactory implements IRecipeFactory {

	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {

		ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);

		ShapedPrimer primer = new ShapedPrimer();
		primer.width = recipe.getRecipeWidth();
		primer.height = recipe.getRecipeHeight();
		primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
		primer.input = recipe.getIngredients();

		return new ShapedFluidRecipe(new ResourceLocation("cofh", "fluid_shaped"), recipe.getRecipeOutput(), primer);
	}

	/* RECIPE */
	public static class ShapedFluidRecipe extends ShapedOreRecipe {

		public ShapedFluidRecipe(ResourceLocation group, ItemStack result, ShapedPrimer primer) {

			super(group, result, primer);
		}

		@Override
		public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {

			NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

			for (int i = 0; i < ret.size(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				IFluidHandlerItem handler = stack.getCount() > 1 ? FluidUtil.getFluidHandler(ItemHelper.cloneStack(stack, 1)) : FluidUtil.getFluidHandler(stack);

				if (handler == null) {
					ret.set(i, ForgeHooks.getContainerItem(stack));
				} else {
					handler.drain(Fluid.BUCKET_VOLUME, true);
					ret.set(i, handler.getContainer().copy());
				}
			}
			return ret;
		}

		@Override
		public boolean isDynamic() {

			return true;
		}
	}

}
