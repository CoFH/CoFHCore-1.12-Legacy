package cofh.lib.gui.element.listbox;

import cofh.lib.gui.element.ElementListBox;

public interface IListBoxElement {

	int getHeight();

	int getWidth();

	Object getValue();

	void draw(ElementListBox listBox, int x, int y, int backColor, int textColor);

}
