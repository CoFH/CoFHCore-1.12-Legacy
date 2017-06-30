package cofh.lib.gui.element;

import cofh.lib.gui.GuiBase;
import cofh.lib.util.helpers.RenderHelper;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.client.renderer.GlStateManager;

import java.util.List;

public class ElementButton extends ElementButtonBase {

	private int sheetX;
	private int sheetY;
	private int hoverX;
	private int hoverY;
	private int disabledX = 0;
	private int disabledY = 0;
	private boolean tooltipLocalized = false;
	private boolean managedClicks;
	private String tooltip;

	public ElementButton(GuiBase gui, int posX, int posY, int sizeX, int sizeY, int sheetX, int sheetY, int hoverX, int hoverY, String texture) {

		super(gui, posX, posY, sizeX, sizeY);
		setGuiManagedClicks(false);
		setTexture(texture, texW, texH);
		this.sheetX = sheetX;
		this.sheetY = sheetY;
		this.hoverX = hoverX;
		this.hoverY = hoverY;
	}

	public ElementButton(GuiBase gui, int posX, int posY, int sizeX, int sizeY, int sheetX, int sheetY, int hoverX, int hoverY, int disabledX, int disabledY, String texture) {

		this(gui, posX, posY, sizeX, sizeY, sheetX, sheetY, hoverX, hoverY, texture);
		this.disabledX = disabledX;
		this.disabledY = disabledY;
	}

	public ElementButton(GuiBase gui, int posX, int posY, String name, int sheetX, int sheetY, int hoverX, int hoverY, int sizeX, int sizeY, String texture) {

		super(gui, posX, posY, sizeX, sizeY);
		setGuiManagedClicks(true);
		setName(name);
		setTexture(texture, texW, texH);
		this.sheetX = sheetX;
		this.sheetY = sheetY;
		this.hoverX = hoverX;
		this.hoverY = hoverY;
	}

	public ElementButton(GuiBase gui, int posX, int posY, String name, int sheetX, int sheetY, int hoverX, int hoverY, int disabledX, int disabledY, int sizeX, int sizeY, String texture) {

		this(gui, posX, posY, name, sheetX, sheetY, hoverX, hoverY, sizeX, sizeY, texture);
		this.disabledX = disabledX;
		this.disabledY = disabledY;
	}

	public ElementButton setGuiManagedClicks(boolean managed) {

		this.managedClicks = managed;
		return this;
	}

	public ElementButton clearToolTip() {

		this.tooltip = null;
		return this;
	}

	public ElementButton setToolTip(String tooltip) {

		this.tooltip = tooltip;
		return this;
	}

	public ElementButton setToolTipLocalized(boolean localized) {

		this.tooltipLocalized = localized;
		return this;
	}

	public ElementButton setToolTipLocalized(String tooltip) {

		return setToolTip(tooltip).setToolTipLocalized(true);
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {

		GlStateManager.color(1, 1, 1, 1);
		RenderHelper.bindTexture(texture);
		if (isEnabled()) {
			if (intersectsWith(mouseX, mouseY)) {
				drawTexturedModalRect(posX, posY, hoverX, hoverY, sizeX, sizeY);
			} else {
				drawTexturedModalRect(posX, posY, sheetX, sheetY, sizeX, sizeY);
			}
		} else {
			drawTexturedModalRect(posX, posY, disabledX, disabledY, sizeX, sizeY);
		}
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

	@Override
	public void onClick() {

	}

	@Override
	public boolean onMousePressed(int x, int y, int mouseButton) {

		if (!managedClicks) {
			return super.onMousePressed(x, y, mouseButton);
		}
		if (isEnabled()) {
			gui.handleElementButtonClick(getName(), mouseButton);
			return true;
		}
		return false;
	}

	public void setSheetX(int pos) {

		sheetX = pos;
	}

	public void setSheetY(int pos) {

		sheetY = pos;
	}

	public void setHoverX(int pos) {

		hoverX = pos;
	}

	public void setHoverY(int pos) {

		hoverY = pos;
	}

	public ElementButton setDisabledX(int pos) {

		disabledX = pos;
		return this;
	}

	public ElementButton setDisabledY(int pos) {

		disabledY = pos;
		return this;
	}

	public void setActive() {

		setEnabled(true);
	}

	public void setDisabled() {

		setEnabled(false);
	}

}
