package cofh.core.gui.element.tab;

import cofh.api.tileentity.IEnergyInfo;
import cofh.core.gui.GuiContainerCore;
import cofh.core.init.CoreTextures;
import cofh.core.util.helpers.StringHelper;
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
	private boolean displayMax = true;
	private boolean displayStored = true;

	static final String UNIT_INSTANT = " RF/t";
	static final String UNIT_STORAGE = " RF";

	public TabEnergy(GuiContainerCore gui, IEnergyInfo container, boolean isProducer) {

		this(gui, defaultSide, container, isProducer);
	}

	public TabEnergy(GuiContainerCore gui, int side, IEnergyInfo container, boolean producer) {

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

	public TabEnergy isProducer(boolean isProducer) {

		this.isProducer = isProducer;
		return this;
	}

	public TabEnergy displayMax(boolean displayMax) {

		this.displayMax = displayMax;
		return this;
	}

	public TabEnergy displayStored(boolean displayStored) {

		this.displayStored = displayStored;
		return this;
	}

	@Override
	protected void drawForeground() {

		drawTabIcon(CoreTextures.ICON_ENERGY);
		if (!isFullyOpened()) {
			return;
		}
		String flowDirection = isProducer ? "info.cofh.energyProduce" : "info.cofh.energyConsume";

		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.energy"), sideOffset() + 20, 6, headerColor);
		getFontRenderer().drawStringWithShadow(StringHelper.localize(flowDirection) + ":", sideOffset() + 6, 18, subheaderColor);
		getFontRenderer().drawString(myContainer.getInfoEnergyPerTick() + UNIT_INSTANT, sideOffset() + 14, 30, textColor);

		if (displayMax) {
			getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.energyMax") + ":", sideOffset() + 6, 42, subheaderColor);
			getFontRenderer().drawString(myContainer.getInfoMaxEnergyPerTick() + UNIT_INSTANT, sideOffset() + 14, 54, textColor);
		}
		if (displayStored) {
			getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.energyStored") + ":", sideOffset() + 6, 66, subheaderColor);
			getFontRenderer().drawString(myContainer.getInfoEnergyStored() + UNIT_STORAGE, sideOffset() + 14, 78, textColor);
		}
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(myContainer.getInfoEnergyPerTick() + UNIT_INSTANT);
		}
	}

}
