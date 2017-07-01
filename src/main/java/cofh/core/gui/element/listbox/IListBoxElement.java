package cofh.core.gui.element.listbox;

import cofh.core.gui.element.ElementListBox;

public interface IListBoxElement {

	int getHeight();

	int getWidth();

	Object getValue();

	void draw(ElementListBox listBox, int x, int y, int backColor, int textColor);

}
