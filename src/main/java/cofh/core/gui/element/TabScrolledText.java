package cofh.core.gui.element;

import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.TabBase;
import cofh.lib.util.helpers.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.List;

public abstract class TabScrolledText extends TabBase {

    protected List<String> myText;
    protected int firstLine = 0;
    protected int maxFirstLine = 0;
    protected int numLines = 0;

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
    protected void drawForeground() {

        drawTabIcon(getIcon());
        if (!isFullyOpened()) {
            return;
        }
        if (firstLine > 0) {
            gui.drawIcon("IconArrowUp1", posXOffset() + maxWidth - 20, posY + 16);
        } else {
            gui.drawIcon("IconArrowUp0", posXOffset() + maxWidth - 20, posY + 16);
        }
        if (firstLine < maxFirstLine) {
            gui.drawIcon("IconArrowDown1", posXOffset() + maxWidth - 20, posY + 76);
        } else {
            gui.drawIcon("IconArrowDown0", posXOffset() + maxWidth - 20, posY + 76);
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

        int shiftedMouseX = mouseX - this.posX();
        int shiftedMouseY = mouseY - this.posY;

        if (!isFullyOpened()) {
            return false;
        }

        if (shiftedMouseX < 108) {
            return super.onMousePressed(mouseX, mouseY, mouseButton);
        }

        if (shiftedMouseY < 52) {
            firstLine = MathHelper.clamp(firstLine - 1, 0, maxFirstLine);
        } else {
            firstLine = MathHelper.clamp(firstLine + 1, 0, maxFirstLine);
        }
        return true;
    }

    @Override
    public boolean onMouseWheel(int mouseX, int mouseY, int movement) {

        if (!isFullyOpened()) {
            return false;
        }
        if (movement > 0) {
            firstLine = MathHelper.clamp(firstLine - 1, 0, maxFirstLine);
            return true;
        } else if (movement < 0) {
            firstLine = MathHelper.clamp(firstLine + 1, 0, maxFirstLine);
            return true;
        }
        return false;
    }

}
