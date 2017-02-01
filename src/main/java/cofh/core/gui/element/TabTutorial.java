package cofh.core.gui.element;

import cofh.core.init.CoreTextures;
import cofh.lib.gui.GuiBase;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class TabTutorial extends TabScrolledText {

	public static int defaultSide = 0;
	public static int defaultHeaderColor = 0xe1c92f;
	public static int defaultSubHeaderColor = 0xaaafb8;
	public static int defaultTextColor = 0xffffff;
	public static int defaultBackgroundColor = 0x5a09bb;

	public TabTutorial(GuiBase gui, String infoString) {

		this(gui, defaultSide, infoString);
	}

	public TabTutorial(GuiBase gui, int side, String infoString) {

		super(gui, side, infoString);

		headerColor = defaultHeaderColor;
		subheaderColor = defaultSubHeaderColor;
		textColor = defaultTextColor;
		backgroundColor = defaultBackgroundColor;
	}

	@Override
	public TextureAtlasSprite getIcon() {

		return CoreTextures.ICON_TUTORIAL;
	}

	@Override
	public String getTitle() {

		return StringHelper.localize("info.cofh.tutorial");
	}

}
