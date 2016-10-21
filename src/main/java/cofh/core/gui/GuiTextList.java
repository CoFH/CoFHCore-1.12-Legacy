package cofh.core.gui;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

@SideOnly(Side.CLIENT)
public class GuiTextList extends Gui {

	public static final int X_OFFSET = 4;
	public static final int Y_OFFSET = 2;

	private final FontRenderer fRend;
	public int xPos = 0;
	public int yPos = 0;

	public int width = 12;
	public int height = 12;

	public boolean drawBackground = true;
	public boolean drawBorder = true;
	public boolean isEnabled = true;
	public boolean isFocused = false;

	/** Color Configuration */
	public int borderColor = -6250336;
	public int backgroundColor = -16777216;
	public int selectedLineColor = -16777216;
	public int textColor = 14737632;
	public int selectedTextColor = 14737632;

	public int displayLines = 0;
	public int lineHeight = 10;

	public List<String> textLines = new LinkedList<String>();
	public int startLine = 0;
	public int selectedLine = -1;

	public boolean highlightSelectedLine = false;

	public GuiTextList(FontRenderer fontRenderer, int x, int y, int w, int lines) {

		fRend = fontRenderer;

		xPos = x;
		yPos = y;
		displayLines = lines;
		lineHeight = fontRenderer.FONT_HEIGHT + 1;
		width = w;
		height = displayLines * lineHeight + Y_OFFSET;
	}

	public void drawBackground() {

		if (drawBorder) {
			drawRect(xPos - 1, yPos - 1, xPos + width + 1, yPos + height + 1, borderColor);
		}
		if (drawBackground) {
			drawRect(xPos, yPos, xPos + width, yPos + height, backgroundColor);
		}
	}

	public void drawText() {

		for (int i = startLine; i < startLine + displayLines; i++) {
			if (textLines.size() > i) {
				String lineToDraw = fRend.trimStringToWidth(textLines.get(i), width);
				if (selectedLine == i && highlightSelectedLine) {
					drawRect(xPos, yPos + 1 + lineHeight * (i - startLine), xPos + width, yPos + lineHeight * (1 + i - startLine), selectedLineColor);
					fRend.drawStringWithShadow(lineToDraw, xPos + X_OFFSET, yPos + Y_OFFSET + lineHeight * (i - startLine), selectedTextColor);
				} else {
					fRend.drawStringWithShadow(lineToDraw, xPos + X_OFFSET, yPos + Y_OFFSET + lineHeight * (i - startLine), textColor);
				}
			}
		}
	}

	public int getSelectedLineYPos() {

		if (selectedLine < startLine || selectedLine > startLine + displayLines - 1) {
			return -1;
		}
		return yPos + 1 + lineHeight * (selectedLine - startLine);
	}

	public String mouseClicked(int mouseX, int mouseY, int mouseButton, int offsetY) {

		int theLine = (mouseY - offsetY) / lineHeight;
		if (textLines.size() > theLine + startLine) {
			return textLines.get(theLine + startLine);
		}
		return "";
	}

	public void setEnabled(boolean enable) {

		isEnabled = enable;
	}

	public void setFocused(boolean focus) {

		isFocused = focus;
	}

	public void addLine(String theLine) {

		textLines.add(theLine);
	}

	public void selectLine(int i) {

		selectedLine = i;
	}

	public void scrollUp() {

		startLine++;
		if (startLine > textLines.size() - displayLines) {
			startLine = textLines.size() - displayLines;
		}
		if (startLine < 0) {
			startLine = 0;
		}
	}

	public void scrollDown() {

		startLine--;
		if (startLine < 0) {
			startLine = 0;
		}
	}

}
