package cofh.gui.element;

import cofh.gui.GuiBase;

public class TabTinker extends TabBase {

	public TabTinker(GuiBase gui) {

		super(gui);

		maxHeight = 92;
		maxWidth = 112;
	}

	@Override
	public void draw() {

		drawBackground();

		if (!isFullyOpened()) {
			return;
		}
	}

}
