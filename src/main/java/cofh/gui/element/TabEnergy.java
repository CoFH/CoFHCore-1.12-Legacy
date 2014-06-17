package cofh.gui.element;

import cofh.api.core.IEnergyInfo;
import cofh.gui.GuiBase;
import cofh.util.StringHelper;

import java.util.List;

import org.lwjgl.opengl.GL11;

public class TabEnergy extends TabBase {

	public static int defaultSide = 1;

	IEnergyInfo myContainer;
	boolean isProducer;

	public TabEnergy(GuiBase gui, IEnergyInfo container, boolean isProducer) {

		this(gui, defaultSide, container, isProducer);
	}

	public TabEnergy(GuiBase gui, int side, IEnergyInfo container, boolean isProducer) {

		super(gui, side);

		myContainer = container;
		maxHeight = 92;
		maxWidth = 100;
		this.isProducer = isProducer;

		if (isProducer) {
			backgroundColor = 0xd0650b;
		} else {
			backgroundColor = 0x0a76d0;
		}
	}

	@Override
	public void draw() {

		drawBackground();
		drawTabIcon("IconEnergy");
		if (!isFullyOpened()) {
			return;
		}
		String powerDirection = isProducer ? "info.cofh.energyProduce" : "info.cofh.energyConsume";

		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.energy"), posXOffset() + 20, posY + 6, headerColor);
		getFontRenderer().drawStringWithShadow(StringHelper.localize(powerDirection) + ":", posXOffset() + 6, posY + 18, subheaderColor);
		getFontRenderer().drawString(myContainer.getInfoEnergyPerTick() + " RF/t", posXOffset() + 14, posY + 30, textColor);
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.maxEnergyPerTick") + ":", posXOffset() + 6, posY + 42, subheaderColor);
		getFontRenderer().drawString(myContainer.getInfoMaxEnergyPerTick() + " RF/t", posXOffset() + 14, posY + 54, textColor);
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.energyStored") + ":", posXOffset() + 6, posY + 66, subheaderColor);
		getFontRenderer().drawString(myContainer.getInfoEnergyStored() + " RF", posXOffset() + 14, posY + 78, textColor);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(myContainer.getInfoEnergyPerTick() + " RF/t");
			return;
		}
	}

}
