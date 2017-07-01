package cofh.core.plugins.jei;

import cofh.core.gui.GuiCore;
import cofh.core.gui.element.tab.TabBase;
import cofh.core.util.Rectangle4i;
import mezz.jei.api.gui.IAdvancedGuiHandler;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SlotMover implements IAdvancedGuiHandler<GuiCore> {

	@Override
	public Class<GuiCore> getGuiContainerClass() {

		return GuiCore.class;
	}

	@Override
	public List<Rectangle> getGuiExtraAreas(GuiCore guiContainer) {

		List<Rectangle> tabBoxes = new ArrayList<>();

		for (TabBase tab : guiContainer.tabs) {
			Rectangle4i rect = tab.getBounds();
			tabBoxes.add(new Rectangle(rect.x, rect.y, rect.w, rect.h));
		}
		return tabBoxes;
	}

	@Nullable
	@Override
	public Object getIngredientUnderMouse(GuiCore guiContainer, int mouseX, int mouseY) {

		return null;
	}

}
