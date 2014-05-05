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

}
