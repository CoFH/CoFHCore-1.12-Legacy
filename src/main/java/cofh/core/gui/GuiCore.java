package cofh.core.gui;

import cofh.core.init.CoreTextures;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.GuiProps;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public abstract class GuiCore extends GuiBase {

	/* SIDE TYPES */
	public static final int NONE = 0;
	public static final int INPUT_ALL = 1;
	public static final int OUTPUT_PRIMARY = 2;
	public static final int OUTPUT_SECONDARY = 3;
	public static final int OUTPUT_ALL = 4;
	public static final int INPUT_PRIMARY = 5;
	public static final int INPUT_SECONDARY = 6;
	public static final int OPEN = 7;
	public static final int OMNI = 8;

	/* INFO TEXTURES */
	public static final String TEX_INFO_INPUT = GuiProps.PATH_ELEMENTS + "info_input.png";
	public static final String TEX_INFO_OUTPUT = GuiProps.PATH_ELEMENTS + "info_output.png";

	/* PROGRESS TEXTURES */
	public static final String TEX_ARROW_LEFT = GuiProps.PATH_ELEMENTS + "progress_arrow_left.png";
	public static final String TEX_ARROW_RIGHT = GuiProps.PATH_ELEMENTS + "progress_arrow_right.png";
	public static final String TEX_ARROW_FLUID_LEFT = GuiProps.PATH_ELEMENTS + "progress_arrow_fluid_left.png";
	public static final String TEX_ARROW_FLUID_RIGHT = GuiProps.PATH_ELEMENTS + "progress_arrow_fluid_right.png";
	public static final String TEX_DROP_LEFT = GuiProps.PATH_ELEMENTS + "progress_fluid_left.png";
	public static final String TEX_DROP_RIGHT = GuiProps.PATH_ELEMENTS + "progress_fluid_right.png";

	/* SPEED / INTENSITY TEXTURES */
	public static final String TEX_ALCHEMY = GuiProps.PATH_ELEMENTS + "scale_alchemy.png";
	public static final String TEX_BUBBLE = GuiProps.PATH_ELEMENTS + "scale_bubble.png";
	public static final String TEX_COMPACT = GuiProps.PATH_ELEMENTS + "scale_compact.png";
	public static final String TEX_CRUSH = GuiProps.PATH_ELEMENTS + "scale_crush.png";
	public static final String TEX_FLAME = GuiProps.PATH_ELEMENTS + "scale_flame.png";
	public static final String TEX_FLAME_GREEN = GuiProps.PATH_ELEMENTS + "scale_flame_green.png";
	public static final String TEX_FLUX = GuiProps.PATH_ELEMENTS + "scale_flux.png";
	public static final String TEX_SAW = GuiProps.PATH_ELEMENTS + "scale_saw.png";
	public static final String TEX_SPIN = GuiProps.PATH_ELEMENTS + "scale_spin.png";
	public static final String TEX_SUN = GuiProps.PATH_ELEMENTS + "scale_sun.png";
	public static final String TEX_SNOWFLAKE = GuiProps.PATH_ELEMENTS + "scale_snowflake.png";

	public static final String TEX_BUTTONS = GuiProps.PATH_ELEMENTS + "buttons.png";

	public static final int PROGRESS = 24;
	public static final int SPEED = 16;

	protected String myInfo = "";

	public GuiCore(Container container) {

		super(container);
	}

	public GuiCore(Container container, ResourceLocation texture) {

		super(container, texture);
	}

	protected void generateInfo(String tileString) {

		int i = 0;
		String line = tileString + "." + i;
		while (StringHelper.canLocalize(line)) {
			if (i > 0) {
				myInfo += "\n\n";
			}
			myInfo += StringHelper.localize(line);
			i++;
			line = tileString + "." + i;
		}
	}

	/* HELPERS */
	@Override
	public void drawButton(TextureAtlasSprite icon, int x, int y, int mode) {

		switch (mode) {
			case 0:
				drawIcon(CoreTextures.ICON_BUTTON, x, y);
				break;
			case 1:
				drawIcon(CoreTextures.ICON_BUTTON_HIGHLIGHT, x, y);
				break;
			default:
				drawIcon(CoreTextures.ICON_BUTTON_INACTIVE, x, y);
				break;
		}
		drawIcon(icon, x, y);
	}

}
