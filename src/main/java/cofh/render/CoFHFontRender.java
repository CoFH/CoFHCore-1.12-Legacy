package cofh.render;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CoFHFontRender extends FontRenderer {

	public CoFHFontRender(GameSettings par1GameSettings, ResourceLocation par2ResourceLocation, TextureManager par3TextureManager, boolean par4) {

		super(par1GameSettings, par2ResourceLocation, par3TextureManager, par4);
	}

	/**
	 * Returns the width of this string. Equivalent of FontMetrics.stringWidth(String s).
	 */
	@Override
	public int getStringWidth(String par1Str) {

		if (par1Str == null) {
			return 0;
		} else {
			int i = 0;
			boolean flag = false;

			for (int j = 0; j < par1Str.length(); ++j) {
				char c0 = par1Str.charAt(j);
				int k = this.getCharWidth(c0);

				if (k < 0 && j < par1Str.length() - 1) {
					++j;
					c0 = par1Str.charAt(j);

					if (c0 != 108 && c0 != 76) {
						if (c0 == 114 || c0 == 82) {
							flag = false;
						}
					} else {
						flag = true;
					}

					k = 0;
				}

				i += k;

				if (flag) {
					++i;
				}
			}

			return i;
		}
	}

	/**
	 * Trims a string to fit a specified Width.
	 */
	@Override
	public String trimStringToWidth(String par1Str, int par2) {

		return this.trimStringToWidth(par1Str, par2, false);
	}

	/**
	 * Breaks a string into a list of pieces that will fit a specified width.
	 */
	@Override
	public List<String> listFormattedStringToWidth(String par1Str, int par2) {

		return Arrays.asList(this.wrapFormattedStringToWidth(par1Str, par2).split("\n"));
	}

	@Override
	public String wrapFormattedStringToWidth(String par1Str, int par2) {

		int j = this.sizeStringToWidth(par1Str, par2);

		if (par1Str.length() <= j) {
			return par1Str;
		} else {
			String s1 = par1Str.substring(0, j);
			char c0 = par1Str.charAt(j);
			boolean flag = c0 == 32 || c0 == 10;
			String s2 = getFormatFromString(s1) + par1Str.substring(j + (flag ? 1 : 0));
			return s1 + "\n" + this.wrapFormattedStringToWidth(s2, par2);
		}
	}

	public int sizeStringToWidth(String par1Str, int par2) {

		int j = par1Str.length();
		int k = 0;
		int l = 0;
		int i1 = -1;

		for (boolean flag = false; l < j; ++l) {
			char c0 = par1Str.charAt(l);

			switch (c0) {
			case 10:
				--l;
				break;
			case 167:
				if (l < j - 1) {
					++l;
					char c1 = par1Str.charAt(l);

					if (c1 != 108 && c1 != 76) {
						if (c1 == 114 || c1 == 82 || isFormatColor(c1)) {
							flag = false;
						}
					} else {
						flag = true;
					}
				}

				break;
			case 32:
				i1 = l;
			default:
				k += this.getCharWidth(c0);

				if (flag) {
					++k;
				}
			}

			if (c0 == 10) {
				++l;
				i1 = l;
				break;
			}

			if (k > par2) {
				break;
			}
		}

		return l != j && i1 != -1 && i1 < l ? i1 : l;
	}

	public static String getFormatFromString(String par0Str) {

		String s1 = "";
		int i = -1;
		int j = par0Str.length();

		while ((i = par0Str.indexOf(167, i + 1)) != -1) {
			if (i < j - 1) {
				char c0 = par0Str.charAt(i + 1);

				if (isFormatColor(c0)) {
					s1 = "\u00a7" + c0;
				} else if (isFormatSpecial(c0)) {
					s1 = s1 + "\u00a7" + c0;
				}
			}
		}

		return s1;
	}

	public static boolean isFormatColor(char par0) {

		return par0 >= 48 && par0 <= 57 || par0 >= 97 && par0 <= 102 || par0 >= 65 && par0 <= 70;
	}

	public static boolean isFormatSpecial(char par0) {

		return par0 >= 107 && par0 <= 111 || par0 >= 75 && par0 <= 79 || par0 == 114 || par0 == 82;
	}
}
