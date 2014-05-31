package cofh.gui.element;

import cofh.gui.GuiBase;
import cofh.util.StringHelper;

import java.util.List;

import org.lwjgl.opengl.GL11;

public class TabInfo extends TabBase {

	public static boolean enable;

	int textColor = 0xffffff;

	String myInfo;

	public TabInfo(GuiBase gui, String infoString) {

		super(gui, 0);
		visible = enable;

		backgroundColor = 0x555555;
		maxHeight += 4 + StringHelper.getSplitStringHeight(getFontRenderer(), infoString, maxWidth);
		myInfo = infoString;
	}

	public TabInfo(GuiBase gui, String infoString, int extraLines) {

		super(gui, 0);
		visible = enable;

		backgroundColor = 0x555555;
		maxHeight += 4 + getFontRenderer().FONT_HEIGHT * extraLines + StringHelper.getSplitStringHeight(getFontRenderer(), infoString, maxWidth);
		myInfo = infoString;
	}

	@Override
	public void draw() {

		if (!visible) {
			return;
		}
		drawBackground();
		drawTabIcon("IconInformation");
		if (!isFullyOpened()) {
			return;
		}
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.information"), posX - currentWidth + 22, posY + 6, headerColor);
		getFontRenderer().drawSplitString(myInfo, posX + 8 - currentWidth, posY + 20, maxWidth - 8, textColor);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(StringHelper.localize("info.cofh.information"));
			return;
		}
	}

}
