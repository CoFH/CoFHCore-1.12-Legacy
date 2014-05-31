package cofh.gui.element;

import cofh.gui.GuiBase;
import cofh.util.StringHelper;

import java.util.List;

import org.lwjgl.opengl.GL11;

public class TabTutorial extends TabBase {

	public static boolean enable;

	int textColor = 0xffffff;

	String myInfo;

	public TabTutorial(GuiBase gui, String infoString) {

		super(gui, 0);
		visible = enable;

		backgroundColor = 0x5a09bb;
		maxHeight += 4 + StringHelper.getSplitStringHeight(getFontRenderer(), infoString, maxWidth);
		myInfo = infoString;
	}

	@Override
	public void draw() {

		if (!visible) {
			return;
		}
		drawBackground();
		drawTabIcon("IconTutorial");
		if (!isFullyOpened()) {
			return;
		}
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.tutorial"), posX - currentWidth + 22, posY + 6, headerColor);
		getFontRenderer().drawSplitString(myInfo, posX + 8 - currentWidth, posY + 20, maxWidth - 8, textColor);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(StringHelper.localize("info.cofh.tutorial"));
			return;
		}
	}

}
