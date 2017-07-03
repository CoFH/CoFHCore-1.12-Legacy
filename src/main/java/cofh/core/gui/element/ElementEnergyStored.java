package cofh.core.gui.element;

import cofh.core.gui.GuiCore;
import cofh.core.init.CoreProps;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.RenderHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.redstoneflux.api.IEnergyStorage;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class ElementEnergyStored extends ElementBase {

	public static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(CoreProps.PATH_ELEMENTS + "energy.png");
	public static final int DEFAULT_SCALE = 42;

	protected IEnergyStorage storage;

	// If this is enabled, 1 pixel of energy will always show in the bar as long as it is non-zero.
	protected boolean alwaysShowMinimum = false;

	public ElementEnergyStored(GuiCore gui, int posX, int posY, IEnergyStorage storage) {

		super(gui, posX, posY);
		this.storage = storage;

		this.texture = DEFAULT_TEXTURE;
		this.sizeX = 16;
		this.sizeY = DEFAULT_SCALE;

		this.texW = 32;
		this.texH = 64;
	}

	public ElementEnergyStored setAlwaysShow(boolean show) {

		alwaysShowMinimum = show;
		return this;
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {

		int amount = getScaled();
		RenderHelper.bindTexture(texture);
		drawTexturedModalRect(posX, posY, 0, 0, sizeX, sizeY);
		drawTexturedModalRect(posX, posY + DEFAULT_SCALE - amount, 16, DEFAULT_SCALE - amount, sizeX, amount);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

	}

	@Override
	public void addTooltip(List<String> list) {

		if (storage.getMaxEnergyStored() < 0) {
			list.add("Infinite RF");
		} else {
			list.add(StringHelper.formatNumber(storage.getEnergyStored()) + " / " + StringHelper.formatNumber(storage.getMaxEnergyStored()) + " RF");
		}
	}

	protected int getScaled() {

		if (storage.getMaxEnergyStored() <= 0) {
			return sizeY;
		}
		long fraction = (long) storage.getEnergyStored() * sizeY / storage.getMaxEnergyStored();

		return alwaysShowMinimum && storage.getEnergyStored() > 0 ? Math.max(1, MathHelper.round(fraction)) : MathHelper.round(fraction);
	}

}
