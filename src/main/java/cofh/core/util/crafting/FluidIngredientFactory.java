package cofh.core.util.crafting;

import cofh.core.util.helpers.ItemHelper;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidIngredientFactory implements IIngredientFactory {

	@Nonnull
	@Override
	public Ingredient parse(JsonContext context, JsonObject json) {

		String fluidName = JsonUtils.getString(json, "fluid");

		return new FluidIngredient(fluidName);
	}

	/* INGREDIENT */
	public static class FluidIngredient extends Ingredient {

		private FluidStack fluid;

		public FluidIngredient(String fluidName) {

			super(FluidUtil.getFilledBucket(new FluidStack(FluidRegistry.getFluid(fluidName), Fluid.BUCKET_VOLUME)));
			fluid = FluidRegistry.getFluidStack(fluidName, Fluid.BUCKET_VOLUME);
		}

		@Override
		public boolean apply(@Nullable ItemStack input) {

			if (input == null || input.isEmpty()) {
				return false;
			}
			IFluidHandlerItem handler = input.getCount() > 1 ? FluidUtil.getFluidHandler(ItemHelper.cloneStack(input, 1)) : FluidUtil.getFluidHandler(input);

			if (handler == null) {
				return false;
			}
			if (fluid.isFluidStackIdentical(handler.drain(Fluid.BUCKET_VOLUME, false))) {
				return true;
			}
			return false;
		}
	}

}
