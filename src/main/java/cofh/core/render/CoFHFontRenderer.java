package cofh.core.render;

import cofh.core.ProxyClient;
import cofh.core.render.customcharrendering.ICustomCharRenderer;
import cofh.core.render.customcharrendering.RenderPlayerFace;
import cofh.core.render.customcharrendering.RenderSprite;
import cofh.lib.util.helpers.SecurityHelper;
import gnu.trove.map.hash.TCharObjectHashMap;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;

@SideOnly (Side.CLIENT)
public class CoFHFontRenderer extends FontRenderer {

	public CoFHFontRenderer(GameSettings par1GameSettings, ResourceLocation par2ResourceLocation, TextureManager par3TextureManager, boolean par4) {

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
					i++;
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

	private float r;
	private float g;
	private float b;
	private float a;

	public void resetColor() {

		super.setColor(r, g, b, a);
	}

	@Override
	public void setColor(float r, float g, float b, float a) {

		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		super.setColor(r, g, b, a);
	}

	public final TCharObjectHashMap<ICustomCharRenderer> renderOverrides = new TCharObjectHashMap<ICustomCharRenderer>();

	@Override
	public float renderUnicodeChar(char letter, boolean p_78277_2_) {

		ICustomCharRenderer iCustomCharRenderer = renderOverrides.get(letter);
		if (iCustomCharRenderer != null) {
			float v = iCustomCharRenderer.renderChar(letter, p_78277_2_, posX, posY, this);
			if (v != -2) {
				return v;
			}
		}

		return super.renderUnicodeChar(letter, p_78277_2_);
	}

	@Override
	public int getCharWidth(char c) {

		ICustomCharRenderer iCustomCharRenderer = renderOverrides.get(c);
		if (iCustomCharRenderer != null) {
			int width = iCustomCharRenderer.getCharWidth(c, this);
			if (width != -2) {
				return width;
			}
		}
		return super.getCharWidth(c);
	}

	// Some existing Blank MC Characters (the default renderer is hardcoded to NEVER render these)
	// \u0379 \u037f \u0380 \u0381 \u0382 \u0383 \u038b \u038d \u03a2 \u0524 \u0525 \u0526 \u0527 \u0528 \u0529 \u052a \u052b \u052c \u052d \u052e \u052f \u0530
	// \u0557 \u0558 \u0560 \u0588 \u058b \u058c \u058d \u058e \u058f \u0590 \u05c8 \u05c9 \u05ca \u05cb \u05cc \u05cd \u05ce \u05cf \u05eb \u05ec \u05ed \u05ee
	// \u05ef \u05f5 \u05f6 \u05f7 \u05f8 \u05f9 \u05fa \u05fb \u05fc \u05fd \u05fe \u05ff \u0604 \u0605 \u061c \u061d \u0620 \u065f \u070e \u074b \u074c \u07b2
	// \u07b3 \u07b4 \u07b5 \u07b6 \u07b7 \u07b8 \u07b9 \u07ba \u07bb \u07bc \u07bd \u07be \u07bf

	public void initSpecialCharacters() {

		RenderSprite.addRenderer('\u2661', Gui.ICONS, 52, 0, 9, 9, this); // heart
		RenderSprite.addRenderer('\u2665', Gui.ICONS, 124, 0, 9, 9, this); // wither heart
		RenderSprite.addRenderer('\u2314', Gui.ICONS, 34, 9, 9, 9, this); // shield
		RenderSprite.addRenderer('\u25CB', Gui.ICONS, 16, 18, 9, 9, this); // bubble
		RenderSprite.addRenderer('\u25CC', Gui.ICONS, 25, 18, 9, 9, this); // bubble broken
		RenderSprite.addRenderer('\u29F0', Gui.ICONS, 52, 27, 9, 9, this); // hunger
		RenderSprite.addRenderer('\u2763', Gui.ICONS, 52, 45, 9, 9, this); // hardcore heart

		ResourceLocation beacon = new ResourceLocation("textures/gui/container/beacon.png");
		RenderSprite.addRenderer('\u2715', beacon, 113, 222, 15, 15, this); // cross mark
		RenderSprite.addRenderer('\u2714', beacon, 90, 223, 16, 14, this); // check mark

		RenderPlayerFace.init(this);
	}

	@Override
	public void bindTexture(ResourceLocation location) { // make protected -> public

		super.bindTexture(location);
	}

	public static FontRenderer loadFontRendererStack(ItemStack stack) {

		if (SecurityHelper.isSecure(stack)) {
			return RenderPlayerFace.loadProfile(stack);
		}
		return ProxyClient.fontRenderer;
	}

	private char n = '\u0378';

	public char getNextBlankChar() {

		n++;

		while (glyphWidth[n] != 0 && renderOverrides.containsKey(n)) {
			n++;
		}
		return n;
	}

}
