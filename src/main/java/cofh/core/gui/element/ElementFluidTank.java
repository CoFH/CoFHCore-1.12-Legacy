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

	public static final ResourceLocation LARGE_TEXTURE = new ResourceLocation(CoreProps.PATH_ELEMENTS + "fluid_tank_large.png");
	public static final ResourceLocation MEDIUM_TEXTURE = new ResourceLocation(CoreProps.PATH_ELEMENTS + "fluid_tank_medium.png");
	public static final ResourceLocation SMALL_TEXTURE = new ResourceLocation(CoreProps.PATH_ELEMENTS + "fluid_tank_small.png");

	protected IFluidTank tank;
	protected int gaugeType;
	protected boolean drawTank;
	protected boolean isInfinite;
	protected boolean isThin;
	protected float durationFactor = 1.0F;

	// If this is enabled, 1 pixel of fluid will always show in the tank as long as fluid is present.
	protected boolean alwaysShowMinimum = false;

	protected TextureAtlasSprite fluidTextureOverride;

	public ElementFluidTank(GuiContainerCore gui, int posX, int posY, IFluidTank tank) {

		this(gui, posX, posY, tank, LARGE_TEXTURE);
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

	public ElementFluidTank setLarge() {

		this.texture = LARGE_TEXTURE;
		this.sizeX = 16;
		this.sizeY = 60;
		return this;
	}

	public ElementFluidTank setMedium() {

		this.texture = MEDIUM_TEXTURE;
		this.sizeY = 40;
		return this;
	}

	public ElementFluidTank setSmall() {

		this.texture = SMALL_TEXTURE;
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

	public ElementFluidTank setInfinite(boolean infinite) {

		isInfinite = infinite;
		return this;
	}

	public ElementFluidTank setThin(boolean thin) {

		this.isThin = thin;
		this.sizeX = 7;
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
			if (isThin) {
				drawTexturedModalRect(posX - 1, posY - 1, 0, 0, sizeX, sizeY + 2);
				drawTexturedModalRect(posX - 1 + sizeX, posY - 1, sizeX, 0, 2, sizeY + 2);
			} else {
				drawTexturedModalRect(posX - 1, posY - 1, 0, 0, sizeX + 2, sizeY + 2);
			}
		}
		drawFluid();
		RenderHelper.bindTexture(texture);
		drawTexturedModalRect(posX, posY, 32 + gaugeType * 16 + (isThin ? 3 : 0), 1, sizeX, sizeY);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

	}

	@Override
	public void addTooltip(List<String> list) {

		if (tank.getFluid() != null && tank.getFluidAmount() > 0) {
			list.add(StringHelper.getFluidName(tank.getFluid()));

			if (FluidHelper.isPotionFluid(tank.getFluid())) {
				FluidHelper.addPotionTooltip(tank.getFluid(), list, durationFactor);
			}
		}
		if (isInfinite) {
			list.add(StringHelper.localize("info.cofh.infiniteFluid"));
		} else {
			list.add(StringHelper.formatNumber(tank.getFluidAmount()) + " / " + StringHelper.formatNumber(tank.getCapacity()) + " mB");
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
