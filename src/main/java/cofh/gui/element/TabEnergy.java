package cofh.gui.element;

import cofh.api.tileentity.IEnergyInfo;
import cofh.gui.GuiBase;
import cofh.util.StringHelper;

import java.util.List;

import org.lwjgl.opengl.GL11;

public class TabEnergy extends TabBase {

	IEnergyInfo myTile;
	int headerColor = 0xe1c92f;
	int subheaderColor = 0xaaafb8;
	int textColor = 0x000000;
	boolean isProducer;

	public TabEnergy(GuiBase gui, IEnergyInfo theTile, boolean isProducer) {

		super(gui);

		myTile = theTile;
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
		String powerDirection;

		if (isProducer) {
			powerDirection = "info.cofh.energyProduce";
		} else {
			powerDirection = "info.cofh.energyConsume";
		}
		GuiBase.guiFontRenderer.drawStringWithShadow(StringHelper.localize("info.cofh.energy"), posX + 22, posY + 6, headerColor);
		GuiBase.guiFontRenderer.drawStringWithShadow(StringHelper.localize(powerDirection) + ":", posX + 8, posY + 18, subheaderColor);
		GuiBase.guiFontRenderer.drawString(myTile.getInfoEnergyPerTick() + " RF/t", posX + 16, posY + 30, textColor);
		GuiBase.guiFontRenderer.drawStringWithShadow(StringHelper.localize("info.cofh.maxEnergyPerTick") + ":", posX + 8, posY + 42, subheaderColor);
		GuiBase.guiFontRenderer.drawString(myTile.getInfoMaxEnergyPerTick() + " RF/t", posX + 16, posY + 54, textColor);
		GuiBase.guiFontRenderer.drawStringWithShadow(StringHelper.localize("info.cofh.energyStored") + ":", posX + 8, posY + 66, subheaderColor);
		GuiBase.guiFontRenderer.drawString(myTile.getInfoEnergy() + " RF", posX + 16, posY + 78, textColor);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(myTile.getInfoEnergyPerTick() + " RF/t");
			return;
		}
	}

}
