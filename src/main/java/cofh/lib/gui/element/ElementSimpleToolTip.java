package cofh.lib.gui.element;

import cofh.lib.gui.GuiBase;
import cofh.lib.render.RenderHelper;
import cofh.lib.util.helpers.StringHelper;

import java.util.List;

/**
 * Basic element which can render an arbitrary texture and may have a tooltip.
 *
 * @author King Lemming
 *
 */
public class ElementSimpleToolTip extends ElementBase {

	int texU = 0;
	int texV = 0;
	boolean tooltipLocalized = false;
	String tooltip;

	public ElementSimpleToolTip(GuiBase gui, int posX, int posY) {

		super(gui, posX, posY);
	}

	public ElementSimpleToolTip setTextureOffsets(int u, int v) {

		texU = u;
		texV = v;
		return this;
	}

	public ElementSimpleToolTip clearToolTip() {

		this.tooltip = null;
		return this;
	}

	public ElementSimpleToolTip setToolTip(String tooltip) {

		this.tooltip = tooltip;
		return this;
	}

	public ElementSimpleToolTip setToolTipLocalized(boolean localized) {

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

		return;
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
