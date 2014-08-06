package cofh.core.gui;

import cofh.core.render.IconRegistry;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.GuiProps;

import net.minecraft.inventory.Container;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public abstract class GuiBaseAdv extends GuiBase {

	public static final String TEX_ARROW_LEFT = GuiProps.PATH_ELEMENTS + "Progress_Arrow_Left.png";
	public static final String TEX_ARROW_RIGHT = GuiProps.PATH_ELEMENTS + "Progress_Arrow_Right.png";
	public static final String TEX_DROP_LEFT = GuiProps.PATH_ELEMENTS + "Progress_Fluid_Left.png";
	public static final String TEX_DROP_RIGHT = GuiProps.PATH_ELEMENTS + "Progress_Fluid_Right.png";

	public static final String TEX_ALCHEMY = GuiProps.PATH_ELEMENTS + "Scale_Alchemy.png";
	public static final String TEX_BUBBLE = GuiProps.PATH_ELEMENTS + "Scale_Bubble.png";
	public static final String TEX_CRUSH = GuiProps.PATH_ELEMENTS + "Scale_Crush.png";
	public static final String TEX_FLAME = GuiProps.PATH_ELEMENTS + "Scale_Flame.png";
	public static final String TEX_FLUX = GuiProps.PATH_ELEMENTS + "Scale_Flux.png";
	public static final String TEX_SAW = GuiProps.PATH_ELEMENTS + "Scale_Saw.png";
	public static final String TEX_SNOWFLAKE = GuiProps.PATH_ELEMENTS + "Scale_Snowflake.png";

	public static final String TEX_TANK = GuiProps.PATH_ELEMENTS + "FluidTank.png";

	public static final int PROGRESS = 24;
	public static final int SPEED = 16;

	public GuiBaseAdv(Container container) {

		super(container);
	}

	public GuiBaseAdv(Container container, ResourceLocation texture) {

		super(container, texture);
	}

	/* HELPERS */
	@Override
	public void drawButton(IIcon icon, int x, int y, int spriteSheet, int mode) {

		switch (mode) {
		case 0:
			drawIcon(IconRegistry.getIcon("IconButton"), x, y, 1);
			break;
		case 1:
			drawIcon(IconRegistry.getIcon("IconButtonHighlight"), x, y, 1);
			break;
		default:
			drawIcon(IconRegistry.getIcon("IconButtonInactive"), x, y, 1);
			break;
		}
		drawIcon(icon, x, y, spriteSheet);
	}

	@Override
	public IIcon getIcon(String name) {

		return IconRegistry.getIcon(name);
	}

}
