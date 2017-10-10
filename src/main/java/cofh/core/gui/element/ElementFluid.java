package cofh.core.gui.element;

import cofh.core.gui.GuiContainerCore;
import cofh.core.util.helpers.FluidHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class ElementFluid extends ElementBase {

	public FluidStack fluid;

	public ElementFluid(GuiContainerCore gui, int posX, int posY) {

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
