package cofh.lib.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.util.IIcon;

public class IconOverlay implements IIcon {

	private IIcon overlayIcon;
	private float xSegments, ySegments;
	private float selectedSegmentX, selectedSegmentY;

	public IconOverlay(IIcon overlayIcon, int subX, int subY, int selectedX, int selectedY) {
		this.overlayIcon = overlayIcon;
		xSegments = subX;
		ySegments = subY;
		selectedSegmentX = selectedX;
		selectedSegmentY = selectedY;
	}

	public IconOverlay(IIcon overlayIcon, int subX, int subY, int index) {
		this.overlayIcon = overlayIcon;
		xSegments = subX;
		ySegments = subY;
		selectedSegmentX = index % subX;
		selectedSegmentY = index / subX;
	}

	public IconOverlay(IIcon overlayIcon, int subX, int subY, boolean ...sides) {
		this.overlayIcon = overlayIcon;
		xSegments = subX;
		ySegments = subY;
		int parts = toInt(sides) & 255;
		int value = (parts & 15);
		parts = parts >> 4;
		int w;
		switch (value) {
		case 3: // bottom right connection
			value ^= ((parts & 1) << 4); // bithack: add 16 if connection
			break;
		case 5: // top right connection
			value ^= ((parts & 8) << 1); // bithack: add 16 if connection
			break;
		case 7: // left empty
			w = parts & 9;
			value ^= ((w & (w << 3)) << 1); // bithack: add 16 if both connections
			if ((w == 1) | w == 8) // bottom right, top right
				value = 32 | (w >> 3);
			break;
		case 10: // bottom left connection
			value ^= ((parts & 2) << 3); // bithack: add 16 if connection
			break;
		case 11: // top empty
			w = parts & 3;
			value ^= ((w & (w << 1)) << 3); // bithack: add 16 if both connections
			if ((w == 1) | w == 2) // bottom right, bottom left
				value = 34 | (w >> 1);
			break;
		case 12: // top left connection
			value ^= ((parts & 4) << 2); // bithack: add 16 if connection
			break;
		case 13: // bottom empty
			w = parts & 12;
			value ^= ((w & (w << 1)) << 1); // bithack: add 16 if both connections
			if ((w == 4) | w == 8) // top left, top right
				value = 36 | (w >> 3);
			break;
		case 14: // right empty
			w = parts & 6;
			value ^= ((w & (w << 1)) << 2); // bithack: add 16 if both connections
			if ((w == 2) | w == 4) // bottom left, top left
				value = 38 | (w >> 2);
			break;
		case 15: // all sides
			value = 40 + parts;
		default:
		}
		selectedSegmentX = value % subX;
		selectedSegmentY = value / subX;
	}

	private static int toInt(boolean ...flags) {
		int ret = 0;
		for (int i = flags.length; i --> 0;)
			ret |= (flags[i] ? 1 : 0) << i;
		return ret;
	}

	/*@Override
	@SideOnly(Side.CLIENT)
	public int getOriginX() {
		return (int)(this.getMinU() * overlayIcon.getSheetWidth());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getOriginY() {
		return (int)(this.getMinV() * overlayIcon.getSheetHeight());
	}//*/

	@Override
	@SideOnly(Side.CLIENT)
	public float getMinU() {
		return overlayIcon.getInterpolatedU(((selectedSegmentX + 0.001f) / xSegments) * 16f);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getMaxU() {
		return overlayIcon.getInterpolatedU(((selectedSegmentX + 0.999f) / xSegments) * 16f);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getInterpolatedU(double d0) {
		float minU = this.getMinU();
		float f = this.getMaxU() - minU;
		return minU + f * ((float)d0 / 16.0F);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getMinV() {
		return overlayIcon.getInterpolatedV(((selectedSegmentY + 0.001f) / ySegments) * 16f);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getMaxV() {
		return overlayIcon.getInterpolatedV(((selectedSegmentY + 0.999f) / ySegments) * 16f);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getInterpolatedV(double d0) {
		float minV = this.getMinV();
		float f = this.getMaxV() - minV;
		return minV + f * ((float)d0 / 16.0F);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getIconName() {
		return overlayIcon.getIconName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getIconWidth() {
		return (int)(overlayIcon.getIconWidth() / xSegments);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getIconHeight() {
		return (int)(overlayIcon.getIconHeight() / ySegments);
	}

}
