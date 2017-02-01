package cofh.core.gui.element;

import cofh.api.tileentity.IEnergyInfo;
import cofh.core.init.CoreTextures;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.TabBase;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.client.renderer.GlStateManager;

import java.util.List;

public class TabEnergy extends TabBase {

	public static int defaultSide = 0;
	public static int defaultHeaderColor = 0xe1c92f;
	public static int defaultSubHeaderColor = 0xaaafb8;
	public static int defaultTextColor = 0x000000;
	public static int defaultBackgroundColorOut = 0xd0650b;
	public static int defaultBackgroundColorIn = 0x0a76d0;

	private IEnergyInfo myContainer;
	private boolean isProducer;

	public TabEnergy(GuiBase gui, IEnergyInfo container, boolean isProducer) {

		this(gui, defaultSide, container, isProducer);
	}

	public TabEnergy(GuiBase gui, int side, IEnergyInfo container, boolean producer) {

		super(gui, side);

		headerColor = defaultHeaderColor;
		subheaderColor = defaultSubHeaderColor;
		textColor = defaultTextColor;
		backgroundColor = producer ? defaultBackgroundColorOut : defaultBackgroundColorIn;

		maxHeight = 92;
		maxWidth = 100;
		myContainer = container;
		isProducer = producer;
	}

	@Override
	protected void drawForeground() {

		drawTabIcon(CoreTextures.ICON_ENERGY);
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
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(myContainer.getInfoEnergyPerTick() + " RF/t");
			return;
		}
	}

}
