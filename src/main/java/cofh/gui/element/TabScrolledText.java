package cofh.gui.element;

import cofh.gui.GuiBase;
import cofh.util.MathHelper;

import java.util.List;

import org.lwjgl.opengl.GL11;

public abstract class TabScrolledText extends TabBase {

	protected List<String> myText;
	int firstLine = 0;
	int maxFirstLine = 0;
	int numLines = 0;

	public TabScrolledText(GuiBase gui, int side, String infoString) {

		super(gui, side);

		maxHeight = 92;
		myText = getFontRenderer().listFormattedStringToWidth(infoString, maxWidth - 16);
		numLines = Math.min(myText.size(), (maxHeight - 24) / getFontRenderer().FONT_HEIGHT);
		maxFirstLine = myText.size() - numLines;
	}

	public abstract String getIcon();

	public abstract String getTitle();

	@Override
	public void draw() {

		if (!isVisible()) {
			return;
		}
		drawBackground();
		drawTabIcon(getIcon());
		if (!isFullyOpened()) {
			return;
		}
		if (firstLine > 0) {
			gui.drawIcon("IconArrowUp1", posXOffset() + maxWidth - 20, posY + 16, 1);
		} else {
			gui.drawIcon("IconArrowUp0", posXOffset() + maxWidth - 20, posY + 16, 1);
		}
		if (firstLine < maxFirstLine) {
			gui.drawIcon("IconArrowDown1", posXOffset() + maxWidth - 20, posY + 76, 1);
		} else {
			gui.drawIcon("IconArrowDown0", posXOffset() + maxWidth - 20, posY + 76, 1);
		}
		getFontRenderer().drawStringWithShadow(getTitle(), posXOffset() + 18, posY + 6, headerColor);
		for (int i = firstLine; i < firstLine + numLines; i++) {
			getFontRenderer().drawString(myText.get(i), posXOffset() + 2, posY + 20 + (i - firstLine) * getFontRenderer().FONT_HEIGHT, textColor);
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(getTitle());
			return;
		}
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {

		if (!isFullyOpened()) {
			return false;
		}
		if (side == LEFT) {
			mouseX += currentWidth;
		}
		mouseX -= currentShiftX;
		mouseY -= currentShiftY;

		if (mouseX < 108) {
			return false;
		}
		if (mouseY < 52) {
			firstLine = MathHelper.clampI(firstLine - 1, 0, maxFirstLine);
		} else {
			firstLine = MathHelper.clampI(firstLine + 1, 0, maxFirstLine);
		}
		return true;
	}

	@Override
	public boolean onMouseWheel(int mouseX, int mouseY, int movement) {

		if (!isFullyOpened()) {
			return false;
		}
		if (movement > 0) {
			firstLine = MathHelper.clampI(firstLine - 1, 0, maxFirstLine);
			return true;
		} else if (movement < 0) {
			firstLine = MathHelper.clampI(firstLine + 1, 0, maxFirstLine);
			return true;
		}
		return false;
	}

}
