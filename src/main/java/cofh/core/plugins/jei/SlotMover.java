package cofh.core.plugins.jei;

import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.TabBase;
import cofh.lib.util.Rectangle4i;
import mezz.jei.api.gui.IAdvancedGuiHandler;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SlotMover implements IAdvancedGuiHandler<GuiBase> {

	@Override
	public Class<GuiBase> getGuiContainerClass() {

		return GuiBase.class;
	}

	@Override
	public List<Rectangle> getGuiExtraAreas(GuiBase guiContainer) {

		List<Rectangle> tabBoxes = new ArrayList<Rectangle>();

		for (TabBase tab : guiContainer.tabs) {
			Rectangle4i rect = tab.getBounds();
			tabBoxes.add(new Rectangle(rect.x, rect.y, rect.w, rect.h));
		}
		return tabBoxes;
	}

	@Nullable
	@Override
	public Object getIngredientUnderMouse(GuiBase guiContainer, int mouseX, int mouseY) {

		return null;
	}

}
