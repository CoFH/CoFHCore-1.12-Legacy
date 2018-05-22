package cofh.core.gui.element.tab;

import cofh.core.gui.GuiContainerCore;
import cofh.core.init.CoreTextures;
import cofh.core.util.helpers.MathHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.io.IOException;
import java.util.List;

public abstract class TabScrolledText extends TabBase {

	protected List<String> myText;
	protected int firstLine = 0;
	protected int maxFirstLine;
	protected int numLines;

	protected boolean scrollable;

	public TabScrolledText(GuiContainerCore gui, int side, String infoString) {

		super(gui, side);

		maxHeight = 92;
		myText = getFontRenderer().listFormattedStringToWidth(infoString, maxWidth - 16);
		numLines = Math.min(myText.size(), (maxHeight - 24) / getFontRenderer().FONT_HEIGHT);
		maxFirstLine = myText.size() - numLines;
	}

	public abstract TextureAtlasSprite getIcon();

	public abstract String getTitle();

	@Override
	protected void drawForeground() {

		drawTabIcon(getIcon());
		if (!isFullyOpened()) {
			return;
		}
		if (maxFirstLine > 0) {
			if (firstLine > 0) {
				gui.drawIcon(CoreTextures.ICON_ARROW_UP, sideOffset() + maxWidth - 20, 16);
			} else {
				gui.drawIcon(CoreTextures.ICON_ARROW_UP_INACTIVE, sideOffset() + maxWidth - 20, 16);
			}
			if (firstLine < maxFirstLine) {
				gui.drawIcon(CoreTextures.ICON_ARROW_DOWN, sideOffset() + maxWidth - 20, 76);
			} else {
				gui.drawIcon(CoreTextures.ICON_ARROW_DOWN_INACTIVE, sideOffset() + maxWidth - 20, 76);
			}
		}
		getFontRenderer().drawStringWithShadow(getTitle(), sideOffset() + 18, 6, headerColor);
		for (int i = firstLine; i < firstLine + numLines; i++) {
			getFontRenderer().drawString(myText.get(i), sideOffset() + 2, 20 + (i - firstLine) * getFontRenderer().FONT_HEIGHT, textColor);
		}
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(getTitle());
		}
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) throws IOException {

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
