package powercrystals.core.gui.controls;

public interface IListBoxElement {

	public int getHeight();

	public Object getValue();

	public void draw(ListBox listBox, int x, int y, int backColor, int textColor);
}
