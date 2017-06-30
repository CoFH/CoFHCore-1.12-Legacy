package cofh.lib.gui;

public class GuiColor extends Number {

	private static final long serialVersionUID = 7024827242888861187L;
	private final int _color;

	public GuiColor(int argb) {

		_color = argb;
	}

	public GuiColor(int rgba, Void dummy) {

		this(rgba >>> 24, rgba >> 16, rgba >> 8, rgba);
	}

	public GuiColor(byte alpha, int argb) {

		this(argb >> 16, argb >> 8, argb, alpha);
	}

	public GuiColor(int rgba, byte alpha) {

		this(rgba >>> 24, rgba >> 16, rgba >> 8, alpha);
	}

	public GuiColor(int r, int g, int b) {

		this(r, g, b, 255);
	}

	public GuiColor(int r, int g, int b, int a) {

		_color = (b & 0xFF) | (g & 0xFF) << 8 | (r & 0xFF) << 16 | (a & 0xFF) << 24;
	}

	public int getColor() {

		return _color;
	}

	public int getIntR() {

		return (_color >> 16) & 0xFF;
	}

	public int getIntG() {

		return (_color >> 8) & 0xFF;
	}

	public int getIntB() {

		return _color & 0xFF;
	}

	public int getIntA() {

		return (_color >> 24) & 0xFF;
	}

	public float getFloatR() {

		return getIntR() / 255f;
	}

	public float getFloatG() {

		return getIntG() / 255f;
	}

	public float getFloatB() {

		return getIntB() / 255f;
	}

	public float getFloatA() {

		return getIntA() / 255f;
	}

	// ///////////////////////////////////////////////////////// Math Methods ////////////////////////////////////////////
	public GuiColor multiply(float amount) {

		return multiply(amount, amount, amount, amount);
	}

	public GuiColor multiply(float rgb, float a) {

		return multiply(rgb, rgb, rgb, a);
	}

	public GuiColor multiply(float r, float g, float b) {

		return multiply(r, g, b, 1);
	}

	public GuiColor multiply(float r, float g, float b, float a) {

		return new GuiColor(Math.min((int) (getIntR() * r), 255), Math.min((int) (getIntG() * g), 255), Math.min((int) (getIntB() * b), 255));
	}

	public GuiColor add(int amount) {

		return new GuiColor(Math.max(Math.min(getIntR() + amount, 255), 0), Math.max(Math.min(getIntG() + amount, 255), 0), Math.max(Math.min(getIntB() + amount, 255), 0), Math.max(Math.min(getIntA() + amount, 255), 0));
	}

	public GuiColor add(GuiColor color) {

		return new GuiColor(Math.max(Math.min(getIntR() + color.getIntR(), 255), 0), Math.max(Math.min(getIntG() + color.getIntG(), 255), 0), Math.max(Math.min(getIntB() + color.getIntB(), 255), 0), Math.max(Math.min(getIntA() + color.getIntA(), 255), 0));
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public int intValue() {

		return getColor();
	}

	@Override
	public long longValue() {

		return getColor();
	}

	@Override
	public float floatValue() {

		return getColor();
	}

	@Override
	public double doubleValue() {

		return getColor();
	}

}
