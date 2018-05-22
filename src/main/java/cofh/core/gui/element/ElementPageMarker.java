package cofh.core.gui.element;

import cofh.core.gui.GuiContainerCore;
import cofh.core.init.CoreProps;
import net.minecraft.util.ResourceLocation;

public class ElementPageMarker extends ElementBase {

	public static final ResourceLocation DEFAULT_TEXTURE_TOP = new ResourceLocation(CoreProps.PATH_ELEMENTS + "page_marker_top");
	public static final ResourceLocation DEFAULT_TEXTURE_BOTTOM = new ResourceLocation(CoreProps.PATH_ELEMENTS + "page_marker_bottom");

	public static final int TOP = 0;
	public static final int BOTTOM = 1;

	public int side = TOP;

	public ElementPageMarker(GuiContainerCore gui) {

		super(gui, 0, 0);
		texture = DEFAULT_TEXTURE_TOP;
	}

	public ElementPageMarker(GuiContainerCore gui, int side) {

		super(gui, 0, 0);
		this.side = side;

		if (side == BOTTOM) {
			texture = DEFAULT_TEXTURE_BOTTOM;
		} else {
			texture = DEFAULT_TEXTURE_TOP;
		}
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {

	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

	}

}
