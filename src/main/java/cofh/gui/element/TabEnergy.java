package cofh.gui.element;

import cofh.CoFHCore;
import cofh.api.tileentity.IEnergyInfo;
import cofh.gui.GuiBase;
import cofh.util.MathHelper;
import cofh.util.StringHelper;

import java.util.List;

import org.lwjgl.opengl.GL11;

public class TabEnergy extends TabBase {

	public static boolean enable;
	public static int defaultSide = 0;
	public static int defaultHeaderColor = 0xe1c92f;
	public static int defaultSubHeaderColor = 0xaaafb8;
	public static int defaultTextColor = 0x000000;
	public static int defaultBackgroundColorOut = 0xd0650b;
	public static int defaultBackgroundColorIn = 0x0a76d0;

	public static void initialize() {

		String category = "tab.energy";
		// enable = CoFHCore.configClient.get(category, "Enable", true);
		defaultSide = MathHelper.clampI(CoFHCore.configClient.get(category, "Side", defaultSide), 0, 1);
		defaultHeaderColor = MathHelper.clampI(CoFHCore.configClient.get(category, "ColorHeader", defaultHeaderColor), 0, 0xffffff);
		defaultSubHeaderColor = MathHelper.clampI(CoFHCore.configClient.get(category, "ColorSubHeader", defaultSubHeaderColor), 0, 0xffffff);
		defaultTextColor = MathHelper.clampI(CoFHCore.configClient.get(category, "ColorText", defaultTextColor), 0, 0xffffff);
		defaultBackgroundColorOut = MathHelper.clampI(CoFHCore.configClient.get(category, "ColorBackgroundProducer", defaultBackgroundColorOut), 0, 0xffffff);
		defaultBackgroundColorIn = MathHelper.clampI(CoFHCore.configClient.get(category, "ColorBackgroundConsumer", defaultBackgroundColorIn), 0, 0xffffff);
		CoFHCore.configClient.save();
	}

	IEnergyInfo myContainer;
	boolean isProducer;

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
