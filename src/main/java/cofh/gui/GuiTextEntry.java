package cofh.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTextEntry extends Gui {

	public static final int X_OFFSET = 4;
	public static final int Y_OFFSET = 2;

	protected final FontRenderer fRend;
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

	public GuiTextEntry(FontRenderer fontRenderer, int x, int y, int w, int h) {

		fRend = fontRenderer;

		xPos = x;
		yPos = y;
		width = w;
		height = h;
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

	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

	}

	public void setEnabled(boolean enable) {

		isEnabled = enable;
	}

	public void setFocused(boolean focus) {

		isFocused = focus;
	}

	public boolean keyTyped(char par1, int par2) {

		return true;
	}

}
