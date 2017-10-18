package cofh.core.gui.element;

import cofh.core.gui.GuiContainerCore;
import cofh.core.init.CoreProps;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.RenderHelper;
import cofh.core.util.helpers.StringHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.IFluidTank;

import java.util.List;

public class ElementFluidTank extends ElementBase {

	public static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(CoreProps.PATH_ELEMENTS + "fluid_tank.png");
	public static final ResourceLocation SHORT_TEXTURE = new ResourceLocation(CoreProps.PATH_ELEMENTS + "fluid_tank_short.png");
	public static final ResourceLocation THIN_TEXTURE = new ResourceLocation(CoreProps.PATH_ELEMENTS + "fluid_tank_thin.png");

	protected IFluidTank tank;
	protected int gaugeType;
	protected boolean drawTank;
	protected float durationFactor = 1.0F;

	// If this is enabled, 1 pixel of fluid will always show in the tank as long as fluid is present.
	protected boolean alwaysShowMinimum = false;

	protected TextureAtlasSprite fluidTextureOverride;

	public ElementFluidTank(GuiContainerCore gui, int posX, int posY, IFluidTank tank) {

		this(gui, posX, posY, tank, DEFAULT_TEXTURE);
	}

	public ElementFluidTank(GuiContainerCore gui, int posX, int posY, IFluidTank tank, ResourceLocation texture) {

		super(gui, posX, posY);
		this.tank = tank;

		this.texture = texture;
		this.texW = 64;
		this.texH = 64;

		this.sizeX = 16;
		this.sizeY = 60;
	}

	public ElementFluidTank setGauge(int gaugeType) {

		this.gaugeType = gaugeType;
		return this;
	}

	public ElementFluidTank setDefault() {

		this.texture = DEFAULT_TEXTURE;
		this.sizeX = 16;
		this.sizeY = 60;
		return this;
	}

	public ElementFluidTank setThin() {

		this.texture = THIN_TEXTURE;
		this.sizeX = 7;
		return this;
	}

	public ElementFluidTank setShort() {

		this.texture = SHORT_TEXTURE;
		this.sizeY = 30;
		return this;
	}

	public ElementFluidTank setFluidTextureOverride(TextureAtlasSprite fluidTextureOverride) {

		this.fluidTextureOverride = fluidTextureOverride;
		return this;
	}

	public ElementFluidTank drawTank(boolean drawTank) {

		this.drawTank = drawTank;
		return this;
	}

	public ElementFluidTank setAlwaysShow(boolean show) {

		alwaysShowMinimum = show;
		return this;
	}

	public ElementFluidTank setDurationFactor(float durationFactor) {

		this.durationFactor = durationFactor;
		return this;
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {

		if (drawTank) {
			RenderHelper.bindTexture(texture);
			drawTexturedModalRect(posX - 1, posY - 1, 0, 0, sizeX + 2, sizeY + 2);
		}
		drawFluid();
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
			list.add(StringHelper.formatNumber(tank.getFluidAmount()) + " / " + StringHelper.formatNumber(tank.getCapacity()) + " mB");
		}
		if (FluidHelper.isPotionFluid(tank.getFluid())) {
			FluidHelper.addPotionTooltip(tank.getFluid(), list, durationFactor);
		}
	}

	protected int getScaled() {

		if (tank.getCapacity() < 0) {
			return sizeY;
		}
		long fraction = (long) tank.getFluidAmount() * sizeY / tank.getCapacity();

		return alwaysShowMinimum && tank.getFluidAmount() > 0 ? Math.max(1, MathHelper.ceil(fraction)) : MathHelper.ceil(fraction);
	}

	protected void drawFluid() {

		int amount = getScaled();

		if (fluidTextureOverride != null) {
			RenderHelper.setBlockTextureSheet();
			gui.drawTiledTexture(posX, posY + sizeY - amount, fluidTextureOverride, sizeX, amount);
		} else {
			gui.drawFluid(posX, posY + sizeY - amount, tank.getFluid(), sizeX, amount);
		}
	}

}
