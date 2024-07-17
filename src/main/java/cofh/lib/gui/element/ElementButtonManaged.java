package cofh.lib.gui.element;

import cofh.lib.gui.GuiBase;

import org.lwjgl.opengl.GL11;

public abstract class ElementButtonManaged extends ElementButtonBase {

	private String _text;

	public ElementButtonManaged(GuiBase containerScreen, int posX, int posY, int sizeX, int sizeY, String text) {

		super(containerScreen, posX, posY, sizeX, sizeY);
		_text = text;
	}

	public void setText(String text) {

		_text = text;
	}

	public String getText() {

		return _text;
	}

	protected void bindTexture(int mouseX, int mouseY) {

		if (!isEnabled()) {
			gui.bindTexture(DISABLED);
		} else if (intersectsWith(mouseX, mouseY)) {
			gui.bindTexture(HOVER);
		} else {
			gui.bindTexture(ENABLED);
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {

		bindTexture(mouseX, mouseY);

		drawTexturedModalRect(posX, posY, 0, 0, sizeX / 2, sizeY / 2);
		drawTexturedModalRect(posX, posY + sizeY / 2, 0, 256 - sizeY / 2, sizeX / 2, sizeY / 2);
		drawTexturedModalRect(posX + sizeX / 2, posY, 256 - sizeX / 2, 0, sizeX / 2, sizeY / 2);
		drawTexturedModalRect(posX + sizeX / 2, posY + sizeY / 2, 256 - sizeX / 2, 256 - sizeY / 2, sizeX / 2, sizeY / 2);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

		String text = getFontRenderer().trimStringToWidth(_text, sizeX - 4);
		drawCenteredString(getFontRenderer(), text, posX + sizeX / 2, posY + (sizeY - 8) / 2, getTextColor(mouseX, mouseY));
	}

	protected int getTextColor(int mouseX, int mouseY) {

		if (!isEnabled()) {
			return -6250336;
		} else if (intersectsWith(mouseX, mouseY)) {
			return 16777120;
		} else {
			return 14737632;
		}
	}

	@Override
	public abstract void onClick();

}
