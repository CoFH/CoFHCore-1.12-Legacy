package cofh.core.gui.element.tab;

import cofh.api.tileentity.ISteamInfo;
import cofh.core.gui.GuiContainerCore;
import cofh.core.init.CoreTextures;
import cofh.core.util.helpers.StringHelper;
import net.minecraft.client.renderer.GlStateManager;

import java.util.List;

public class TabSteam extends TabBase {

	public static int defaultSide = 0;
	public static int defaultHeaderColor = 0xe1c92f;
	public static int defaultSubHeaderColor = 0xaaafb8;
	public static int defaultTextColor = 0x000000;
	public static int defaultBackgroundColorOut = 0xd9843c;
	public static int defaultBackgroundColorIn = 0x3b91d9;

	private ISteamInfo myContainer;
	private boolean isProducer;
	private boolean displayMax = true;
	private boolean displayStored = true;

	static final String UNIT_INSTANT = " mB/t";

	public TabSteam(GuiContainerCore gui, ISteamInfo container, boolean isProducer) {

		this(gui, defaultSide, container, isProducer);
	}

	public TabSteam(GuiContainerCore gui, int side, ISteamInfo container, boolean producer) {

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

	public TabSteam isProducer(boolean isProducer) {

		this.isProducer = isProducer;
		return this;
	}

	public TabSteam displayMax(boolean displayMax) {

		this.displayMax = displayMax;
		return this;
	}

	public TabSteam displayStored(boolean displayStored) {

		this.displayStored = displayStored;
		return this;
	}

	@Override
	protected void drawForeground() {

		drawTabIcon(CoreTextures.ICON_STEAM);
		if (!isFullyOpened()) {
			return;
		}
		String flowDirection = isProducer ? "info.cofh.steamProduce" : "info.cofh.steamConsume";

		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.steam"), posXOffset() + 20, posY + 6, headerColor);
		getFontRenderer().drawStringWithShadow(StringHelper.localize(flowDirection) + ":", posXOffset() + 6, posY + 18, subheaderColor);
		getFontRenderer().drawString(myContainer.getInfoSteamPerTick() + UNIT_INSTANT, posXOffset() + 14, posY + 30, textColor);
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.steamMax") + ":", posXOffset() + 6, posY + 42, subheaderColor);
		getFontRenderer().drawString(myContainer.getInfoMaxSteamPerTick() + UNIT_INSTANT, posXOffset() + 14, posY + 54, textColor);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(myContainer.getInfoSteamPerTick() + UNIT_INSTANT);
		}
	}

	public void setProducer(boolean producer) {

		backgroundColor = producer ? defaultBackgroundColorOut : defaultBackgroundColorIn;
		isProducer = producer;
	}
}
