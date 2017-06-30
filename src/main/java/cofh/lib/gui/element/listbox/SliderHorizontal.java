package cofh.lib.gui.element.listbox;

import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.ElementSlider;

public class SliderHorizontal extends ElementSlider {

	public SliderHorizontal(GuiBase containerScreen, int x, int y, int width, int height, int maxValue) {

		this(containerScreen, x, y, width, height, maxValue, 0);
	}

	public SliderHorizontal(GuiBase containerScreen, int x, int y, int width, int height, int maxValue, int minValue) {

		super(containerScreen, x, y, width, height, maxValue, minValue);
		int dist = maxValue - minValue;
		setSliderSize(dist <= 0 ? width : Math.max(width / ++dist, 9), height);
	}

	@Override
	public ElementSlider setLimits(int min, int max) {

		int dist = max - min;
		setSliderSize(dist <= 0 ? getWidth() : Math.max(getWidth() / ++dist, 9), getHeight());
		return super.setLimits(min, max);
	}

	@Override
	public int getSliderX() {

		int dist = _valueMax - _valueMin;
		int maxPos = sizeX - _sliderWidth;
		return Math.min(dist == 0 ? 0 : maxPos * (_value - _valueMin) / dist, maxPos);
	}

	@Override
	public void dragSlider(int v, int y) {

		v += Math.round(_sliderWidth * (v / (float) sizeX) + (_sliderWidth * 0.25f));
		setValue(_valueMin + ((_valueMax - _valueMin) * v / sizeX));
	}
}
