package cofh.lib.gui.element;

import cofh.lib.gui.GuiBase;
import cofh.lib.gui.GuiProps;
import cofh.lib.render.RenderHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.StringHelper;

import java.util.List;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.IFluidTank;

public class ElementFluidTank extends ElementBase {

	public static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(GuiProps.PATH_ELEMENTS + "FluidTank.png");
	public static final int DEFAULT_SCALE = 60;

	protected IFluidTank tank;
	protected int gaugeType;

	// If this is enabled, 1 pixel of fluid will always show in the tank as long as fluid is present.
	protected boolean alwaysShowMinimum = false;

	public ElementFluidTank(GuiBase gui, int posX, int posY, IFluidTank tank) {

		super(gui, posX, posY);
		this.tank = tank;

		this.texture = DEFAULT_TEXTURE;
		this.texW = 64;
		this.texH = 64;

		this.sizeX = 16;
		this.sizeY = DEFAULT_SCALE;
	}

	public ElementFluidTank(GuiBase gui, int posX, int posY, IFluidTank tank, String texture) {

		super(gui, posX, posY);
		this.tank = tank;

		this.texture = new ResourceLocation(texture);
		this.texW = 64;
		this.texH = 64;

		this.sizeX = 16;
		this.sizeY = DEFAULT_SCALE;
	}

	public ElementFluidTank setGauge(int gaugeType) {

		this.gaugeType = gaugeType;
		return this;
	}

	public ElementFluidTank setAlwaysShow(boolean show) {

		alwaysShowMinimum = show;
		return this;
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {

		int amount = getScaled();

		gui.drawFluid(posX, posY + sizeY - amount, tank.getFluid(), sizeX, amount);
		RenderHelper.bindTexture(texture);
		drawTexturedModalRect(posX, posY, 32 + gaugeType * 16, 1, sizeX, sizeY);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

	}

	@Override
	public void addTooltip(List<String> list) {

		if (tank.getFluid() != null && tank.getFluidAmount() > 0) {
			list.add(StringHelper.getFluidName(tank.getFluid()));
		}
		if (tank.getCapacity() < 0) {
			list.add("Infinite Fluid");
		} else {
			list.add("" + tank.getFluidAmount() + " / " + tank.getCapacity() + " mB");
		}
	}

	protected int getScaled() {

		if (tank.getCapacity() < 0) {
			return sizeY;
		}
		long fraction = (long) tank.getFluidAmount() * sizeY / tank.getCapacity();

		return alwaysShowMinimum && tank.getFluidAmount() > 0 ? Math.max(1, MathHelper.round(fraction)) : MathHelper.round(fraction);
	}

}
