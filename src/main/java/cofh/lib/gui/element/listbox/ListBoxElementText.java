package cofh.lib.gui.element.listbox;

import cofh.lib.gui.element.ElementListBox;
import net.minecraft.client.Minecraft;

public class ListBoxElementText implements IListBoxElement {

	private final String text;

	public ListBoxElementText(String text) {

		this.text = text;
	}

	@Override
	public Object getValue() {

		return text;
	}

	@Override
	public int getHeight() {

		return 10;
	}

	@Override
	public int getWidth() {

		return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
	}

	@Override
	public void draw(ElementListBox listBox, int x, int y, int backColor, int textColor) {

		listBox.getFontRenderer().drawStringWithShadow(text, x, y, textColor);
	}

}
