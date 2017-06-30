package cofh.lib.util.helpers;

/**
 * Contains various helper functions to assist with colors.
 *
 * @author King Lemming
 */
public final class ColorHelper {

	private ColorHelper() {

	}

	public static final int DYE_BLACK = 0x191919;
	public static final int DYE_RED = 0xCC4C4C;
	public static final int DYE_GREEN = 0x667F33;
	public static final int DYE_BROWN = 0x7F664C;
	public static final int DYE_BLUE = 0x3366CC;
	public static final int DYE_PURPLE = 0xB266E5;
	public static final int DYE_CYAN = 0x4C99B2;
	public static final int DYE_LIGHT_GRAY = 0x999999;
	public static final int DYE_GRAY = 0x4C4C4C;
	public static final int DYE_PINK = 0xF2B2CC;
	public static final int DYE_LIME = 0x7FCC19;
	public static final int DYE_YELLOW = 0xE5E533;
	public static final int DYE_LIGHT_BLUE = 0x99B2F2;
	public static final int DYE_MAGENTA = 0xE57FD8;
	public static final int DYE_ORANGE = 0xF2B233;
	public static final int DYE_WHITE = 0xFFFFFF;

	public static final int[] DYE_COLORS = { DYE_BLACK, DYE_RED, DYE_GREEN, DYE_BROWN, DYE_BLUE, DYE_PURPLE, DYE_CYAN, DYE_LIGHT_GRAY, DYE_GRAY, DYE_PINK, DYE_LIME, DYE_YELLOW, DYE_LIGHT_BLUE, DYE_MAGENTA, DYE_ORANGE, DYE_WHITE };

	// Yes, this list is pre-localized to en_US and has no spaces. There are times when this is useful, such as in a config file. Localization there is messy
	// and not strictly required.
	public static final String[] WOOL_COLOR_CONFIG = { "White", "Orange", "Magenta", "LightBlue", "Yellow", "Lime", "Pink", "Gray", "LightGray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black" };

	public static int getDyeColor(int color) {

		return color < 0 || color > 15 ? 0xFFFFFF : DYE_COLORS[color];
	}

}
