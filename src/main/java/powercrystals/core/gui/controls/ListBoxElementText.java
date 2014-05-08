package powercrystals.core.gui.controls;

public class ListBoxElementText implements IListBoxElement
{
	private String _text;
	
	public ListBoxElementText(String text)
	{
		_text = text;
	}
	
	@Override
	public Object getValue()
	{
		return _text;
	}
	
	@Override
	public int getHeight()
	{
		return 10;
	}

	@Override
	public void draw(ListBox listBox, int x, int y, int backColor, int textColor)
	{
		String text = listBox.getContainerScreen().fontRendererObj.trimStringToWidth(_text, listBox.getContentWidth());
		listBox.getContainerScreen().fontRendererObj.drawStringWithShadow(text, x, y, textColor);
	}
}
