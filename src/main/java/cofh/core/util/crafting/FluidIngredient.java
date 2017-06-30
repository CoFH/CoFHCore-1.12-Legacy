package cofh.core.util.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nullable;

public class FluidIngredient extends Ingredient {

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
		IFluidHandlerItem handler = FluidUtil.getFluidHandler(input);

		if (handler == null) {
			return false;
		}
		if (fluid.isFluidStackIdentical(handler.drain(Fluid.BUCKET_VOLUME, false))) {
			return true;
		}
		return false;
	}

}
