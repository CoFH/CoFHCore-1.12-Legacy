package cofh.core.gui.element;

import cofh.core.gui.GuiContainerCore;
import cofh.core.util.helpers.RenderHelper;
import cofh.core.util.helpers.StringHelper;

import java.util.List;

/**
 * Basic element which can render an arbitrary texture and may have a tooltip.
 *
 * @author King Lemming
 */
public class ElementSimple extends ElementBase {

	protected int texU = 0;
	protected int texV = 0;
	boolean tooltipLocalized = false;
	String tooltip;

	public ElementSimple(GuiContainerCore gui, int posX, int posY) {

		super(gui, posX, posY);
	}

	public ElementSimple setTextureOffsets(int u, int v) {

		texU = u;
		texV = v;
		return this;
	}

	public ElementSimple clearToolTip() {

		this.tooltip = null;
		return this;
	}

	public ElementSimple setToolTip(String tooltip) {

		this.tooltip = tooltip;
		return this;
	}

	public ElementSimple setToolTipLocalized(boolean localized) {

		this.tooltipLocalized = localized;
		return this;
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {

		RenderHelper.bindTexture(texture);
		drawTexturedModalRect(posX, posY, texU, texV, sizeX, sizeY);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

	}

	@Override
	public void addTooltip(List<String> list) {

		if (tooltip != null) {
			if (tooltipLocalized) {
				list.add(tooltip);
			} else {
				list.add(StringHelper.localize(tooltip));
			}
		}
	}

}
