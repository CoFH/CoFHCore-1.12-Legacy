package cofh.lib.gui.element;

import cofh.lib.gui.GuiBase;
import cofh.lib.util.helpers.RenderHelper;
import cofh.lib.util.helpers.StringHelper;

import java.util.List;

/**
 * Basic element which can render an arbitrary texture and may have a tooltip.
 *
 * @author King Lemming
 */
public class ElementSimpleTooltip extends ElementBase {

	int texU = 0;
	int texV = 0;
	boolean tooltipLocalized = false;
	String tooltip;

	public ElementSimpleTooltip(GuiBase gui, int posX, int posY) {

		super(gui, posX, posY);
	}

	public ElementSimpleTooltip setTextureOffsets(int u, int v) {

		texU = u;
		texV = v;
		return this;
	}

	public ElementSimpleTooltip clearToolTip() {

		this.tooltip = null;
		return this;
	}

	public ElementSimpleTooltip settooltip(String tooltip) {

		this.tooltip = tooltip;
		return this;
	}

	public ElementSimpleTooltip settooltiplocalized(boolean localized) {

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
