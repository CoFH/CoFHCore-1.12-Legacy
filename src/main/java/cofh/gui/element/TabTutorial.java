package cofh.gui.element;

import cofh.CoFHCore;
import cofh.gui.GuiBase;
import cofh.util.MathHelper;
import cofh.util.StringHelper;

public class TabTutorial extends TabScrolledText {

	public static boolean enable;
	public static int defaultSide = 0;
	public static int defaultHeaderColor = 0xe1c92f;
	public static int defaultSubHeaderColor = 0xaaafb8;
	public static int defaultTextColor = 0xffffff;
	public static int defaultBackgroundColor = 0x5a09bb;

	public static void initialize() {

		String category = "tab.tutorial";
		enable = CoFHCore.configClient.get(category, "Enable", true);
		defaultSide = MathHelper.clampI(CoFHCore.configClient.get(category, "Side", defaultSide), 0, 1);
		defaultHeaderColor = MathHelper.clampI(CoFHCore.configClient.get(category, "ColorHeader", defaultHeaderColor), 0, 0xffffff);
		defaultSubHeaderColor = MathHelper.clampI(CoFHCore.configClient.get(category, "ColorSubHeader", defaultSubHeaderColor), 0, 0xffffff);
		defaultTextColor = MathHelper.clampI(CoFHCore.configClient.get(category, "ColorText", defaultTextColor), 0, 0xffffff);
		defaultBackgroundColor = MathHelper.clampI(CoFHCore.configClient.get(category, "ColorBackground", defaultBackgroundColor), 0, 0xffffff);
		CoFHCore.configClient.save();
	}

	public TabTutorial(GuiBase gui, String infoString) {

		this(gui, defaultSide, infoString);
	}

	public TabTutorial(GuiBase gui, int side, String infoString) {

		super(gui, side, infoString);
		setVisible(enable);

		headerColor = defaultHeaderColor;
		subheaderColor = defaultSubHeaderColor;
		textColor = defaultTextColor;
		backgroundColor = defaultBackgroundColor;
	}

	@Override
	public String getIcon() {

		return "IconTutorial";
	}

	@Override
	public String getTitle() {

		return StringHelper.localize("info.cofh.tutorial");
	}

}
