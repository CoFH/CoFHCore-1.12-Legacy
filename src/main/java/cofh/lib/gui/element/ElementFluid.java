package cofh.lib.gui.element;

import cofh.lib.gui.GuiBase;
import cofh.lib.util.helpers.FluidHelper;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class ElementFluid extends ElementBase {

	public FluidStack fluid;

	public ElementFluid(GuiBase gui, int posX, int posY) {

		super(gui, posX, posY);
	}

	public ElementFluid setFluid(FluidStack stack) {

		this.fluid = stack;
		return this;
	}

	public ElementFluid setFluid(Fluid fluid) {

		this.fluid = new FluidStack(fluid, FluidHelper.BUCKET_VOLUME);
		return this;
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {

		gui.drawFluid(posX, posY, fluid, sizeX, sizeY);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

	}

}
