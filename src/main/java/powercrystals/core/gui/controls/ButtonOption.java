package powercrystals.core.gui.controls;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.inventory.GuiContainer;

public abstract class ButtonOption extends Button {

	private Map<Integer, String> _values = new HashMap<Integer, String>();
	private int _currentValue = 0;
	private int _maxValue;

	public ButtonOption(GuiContainer containerScreen, int x, int y, int width, int height) {

		super(containerScreen, x, y, width, height, "");
	}

	public void setValue(int value, String label) {

		_values.put(value, label);
		if (value > _maxValue) {
			_maxValue = value;
		}
	}

	@Override
	public void onClick() {

		int nextValue = _currentValue + 1;
		if (nextValue > _maxValue) {
			nextValue = 0;
		}
		while (_values.get(nextValue) == null) {
			nextValue++;
		}
		setSelectedIndex(nextValue);
	}

	@Override
	public void onRightClick() {

		int nextValue = _currentValue - 1;

		while (_values.get(nextValue) == null) {
			nextValue--;
			if (nextValue < 0) {
				nextValue = _maxValue;
			}
		}
		setSelectedIndex(nextValue);
	}

	public int getSelectedIndex() {

		return _currentValue;
	}

	public void setSelectedIndex(int index) {

		_currentValue = index;
		setText(_values.get(_currentValue));
		onValueChanged(_currentValue, _values.get(_currentValue));
	}

	public String getValue() {

		return _values.get(_currentValue);
	}

	public abstract void onValueChanged(int value, String label);
}
