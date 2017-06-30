package cofh.lib.gui.element;

import cofh.lib.gui.GuiBase;
import cofh.lib.gui.GuiColor;
import cofh.lib.gui.element.listbox.IListBoxElement;
import cofh.lib.util.helpers.StringHelper;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class ElementListBox extends ElementBase {

	public int borderColor = new GuiColor(120, 120, 120, 255).getColor();
	public int backgroundColor = new GuiColor(0, 0, 0, 255).getColor();
	public int selectedLineColor = new GuiColor(0, 0, 0, 255).getColor();
	public int textColor = new GuiColor(150, 150, 150, 255).getColor();
	public int selectedTextColor = new GuiColor(255, 255, 255, 255).getColor();

	protected int _marginTop = 2;
	protected int _marginLeft = 2;
	protected int _marginRight = 2;
	protected int _marginBottom = 2;

	protected final List<IListBoxElement> _elements = new LinkedList<>();

	protected int _firstIndexDisplayed;
	protected int _selectedIndex;
	protected int scrollHoriz;

	public ElementListBox(GuiBase containerScreen, int x, int y, int width, int height) {

		super(containerScreen, x, y, width, height);
	}

	public void add(IListBoxElement element) {

		_elements.add(element);
	}

	public void add(Collection<? extends IListBoxElement> elements) {

		_elements.addAll(elements);
	}

	public void remove(IListBoxElement element) {

		int e = _elements.indexOf(element);
		if (_elements.remove(element)) {
			if (e < _firstIndexDisplayed) {
				--_firstIndexDisplayed;
			}
			if (e < _selectedIndex) {
				--_selectedIndex;
			}
		}
	}

	public void removeAt(int index) {

		_firstIndexDisplayed = scrollHoriz = 0;
		_selectedIndex = -1;
		_elements.remove(index);
	}

	public void removeAll() {

		_elements.clear();
	}

	public int getInternalWidth() {

		int width = 0;
		for (int i = 0; i < _elements.size(); i++) {
			width = Math.max(_elements.get(i).getWidth(), width);
		}
		return width;
	}

	public int getInternalHeight() {

		int height = 0;
		for (int i = 0; i < _elements.size(); i++) {
			height += _elements.get(i).getHeight();
		}
		return height;
	}

	public int getContentWidth() {

		return sizeX - _marginLeft - _marginRight;
	}

	public int getContentHeight() {

		return sizeY - _marginTop - _marginBottom;
	}

	public int getContentTop() {

		return posY + _marginTop;
	}

	public int getContentLeft() {

		return posX + _marginLeft;
	}

	public final int getContentBottom() {

		return getContentTop() + getContentHeight();
	}

	public final int getContentRight() {

		return getContentLeft() + getContentWidth();
	}

	public ElementListBox setTextColor(Number textColor, Number selectedTextColor) {

		if (textColor != null) {
			this.textColor = textColor.intValue();
		}
		if (selectedTextColor != null) {
			this.selectedTextColor = selectedTextColor.intValue();
		}
		return this;
	}

	public ElementListBox setSelectionColor(Number selectedLineColor) {

		if (selectedLineColor != null) {
			this.selectedLineColor = selectedLineColor.intValue();
		}
		return this;
	}

	public ElementListBox setBackgroundColor(Number backgroundColor, Number borderColor) {

		if (backgroundColor != null) {
			this.backgroundColor = backgroundColor.intValue();
		}
		if (borderColor != null) {
			this.borderColor = borderColor.intValue();
		}
		return this;
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {

		drawModalRect(posX - 1, posY - 1, posX + sizeX + 1, posY + sizeY + 1, borderColor);
		drawModalRect(posX, posY, posX + sizeX, posY + sizeY, backgroundColor);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

		int heightDrawn = 0;
		int nextElement = _firstIndexDisplayed;

		glPushMatrix();
		glDisable(GL_LIGHTING);

		glEnable(GL_STENCIL_TEST);
		drawStencil(getContentLeft(), getContentTop(), getContentRight(), getContentBottom(), 1);

		glPushMatrix();
		glTranslated(-scrollHoriz, 0, 0);

		int e = _elements.size();
		while (nextElement < e && heightDrawn <= getContentHeight()) {
			heightDrawn += drawElement(nextElement, getContentLeft(), getContentTop() + heightDrawn);
			nextElement++;
		}
		glPopMatrix();
		glDisable(GL_STENCIL_TEST);
		glPopMatrix();
	}

	protected int drawElement(int elementIndex, int x, int y) {

		IListBoxElement element = _elements.get(elementIndex);
		if (elementIndex == _selectedIndex) {
			element.draw(this, x, y, selectedLineColor, selectedTextColor);
		} else {
			element.draw(this, x, y, backgroundColor, textColor);
		}

		return element.getHeight();
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {

		int heightChecked = 0;
		for (int i = _firstIndexDisplayed; i < _elements.size(); i++) {
			if (heightChecked > getContentHeight()) {
				break;
			}
			int elementHeight = _elements.get(i).getHeight();
			if (getContentTop() + heightChecked <= mouseY && getContentTop() + heightChecked + elementHeight >= mouseY) {
				setSelectedIndex(i);
				onElementClicked(_elements.get(i));
				break;
			}
			heightChecked += elementHeight;
		}
		return true;
	}

	@Override
	public boolean onMouseWheel(int mouseX, int mouseY, int movement) {

		if (StringHelper.isControlKeyDown()) {
			if (movement > 0) {
				scrollLeft();
			} else if (movement < 0) {
				scrollRight();
			}
		} else {
			if (movement > 0) {
				scrollUp();
			} else if (movement < 0) {
				scrollDown();
			}
		}
		return true;
	}

	public void scrollDown() {

		int heightDisplayed = 0;
		int elementsDisplayed = 0;
		for (int i = _firstIndexDisplayed; i < _elements.size(); i++) {
			if (heightDisplayed + _elements.get(i).getHeight() > sizeY) {
				break;
			}
			heightDisplayed += _elements.get(i).getHeight();
			elementsDisplayed++;
		}
		if (_firstIndexDisplayed + elementsDisplayed < _elements.size()) {
			_firstIndexDisplayed++;
		}
		onScrollV(_firstIndexDisplayed);
	}

	public void scrollUp() {

		if (_firstIndexDisplayed > 0) {
			_firstIndexDisplayed--;
		}
		onScrollV(_firstIndexDisplayed);
	}

	public void scrollLeft() {

		scrollHoriz = Math.max(scrollHoriz - 15, 0);
		onScrollH(scrollHoriz);
	}

	public void scrollRight() {

		scrollHoriz = Math.min(scrollHoriz + 15, getLastScrollPositionH());
		onScrollH(scrollHoriz);
	}

	public int getLastScrollPosition() {

		int position = _elements.size() - 1;
		if (position < 0) {
			return 0;
		}
		int heightUsed = 0;

		while (position >= 0 && heightUsed < sizeY) {
			heightUsed += _elements.get(position--).getHeight();
		}
		if (heightUsed > sizeY) {
			++position;
		}
		return position + 1;
	}

	public int getLastScrollPositionH() {

		return Math.max(getInternalWidth() - getContentWidth(), 0);
	}

	public int getSelectedIndex() {

		return _selectedIndex;
	}

	public int getIndexOf(Object value) {

		for (int i = 0; i < _elements.size(); i++) {
			if (_elements.get(i).getValue().equals(value)) {
				return i;
			}
		}
		return -1;
	}

	public IListBoxElement getSelectedElement() {

		if (_selectedIndex == -1 || _selectedIndex >= _elements.size()) {
			return null;
		}
		return _elements.get(_selectedIndex);
	}

	public void setSelectedIndex(int index) {

		if (index >= -1 && index != _selectedIndex && index < _elements.size()) {
			_selectedIndex = index;
			onSelectionChanged(_selectedIndex, getSelectedElement());
		}
	}

	public IListBoxElement getElement(int index) {

		return _elements.get(index);
	}

	public int getElementCount() {

		return _elements.size();
	}

	public void scrollToV(int index) {

		if (index >= 0 && index < _elements.size()) {
			_firstIndexDisplayed = index;
		}
	}

	public void scrollToH(int index) {

		if (index >= 0 && index <= getLastScrollPositionH()) {
			scrollHoriz = index;
		}
	}

	protected void onElementClicked(IListBoxElement element) {

	}

	protected void onScrollV(int newStartIndex) {

	}

	protected void onScrollH(int newStartIndex) {

	}

	protected void onSelectionChanged(int newIndex, IListBoxElement newElement) {

	}

}
