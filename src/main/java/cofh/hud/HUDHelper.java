package cofh.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.render.RenderHelper;
import cofh.render.RenderItemUtils;

public class HUDHelper {

	public static void drawTooltipBoxOnSide(int w, int h, int scaledHeight, int scaledWidth, ForgeDirection side) {

		if (side == ForgeDirection.EAST) {
			drawTooltipBox(scaledWidth - w + 2, scaledHeight / 2 - h / 2, w, h);
		}
	}

	public static void drawTooltipBox(int x, int y, int w, int h) {

		int bg = 0xf0100010;
		drawGradientRect(x + 1, y, w - 1, 1, bg, bg);
		drawGradientRect(x + 1, y + h, w - 1, 1, bg, bg);
		drawGradientRect(x + 1, y + 1, w - 1, h - 1, bg, bg);
		drawGradientRect(x, y + 1, 1, h - 1, bg, bg);
		drawGradientRect(x + w, y + 1, 1, h - 1, bg, bg);
		int grad1 = 0x505000ff;
		int grad2 = 0x5028007F;
		drawGradientRect(x + 1, y + 2, 1, h - 3, grad1, grad2);
		drawGradientRect(x + w - 1, y + 2, 1, h - 3, grad1, grad2);
		drawGradientRect(x + 1, y + 1, w - 1, 1, grad1, grad1);
		drawGradientRect(x + 1, y + h - 1, w - 1, 1, grad2, grad2);
	}

	public static void drawTooltipStringOnSide(int w, int h, int scaledHeight, int scaledWidth, ForgeDirection side, int index, String theString) {

		if (side == ForgeDirection.EAST) {
			drawTooltipString(scaledWidth - w + 26, scaledHeight / 2 - h / 2 + 6 + (index * 17), 0x00FFFFFF, theString);
		}
	}

	public static void drawTooltipString(int x, int y, int color, String theString) {

		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(theString, x, y, color);
	}

	public static void drawItemStackOnSide(ItemStack theStack, int w, int h, int scaledHeight, int scaledWidth, ForgeDirection side, int index) {

		if (side == ForgeDirection.EAST) {
			drawItemStack(scaledWidth - w + 8, scaledHeight / 2 - h / 2 + 2 + (index * 17), theStack);
		}
	}

	public static void drawItemStack(int x, int y, ItemStack theStack) {

		RenderItemUtils.renderItemStackAtScale(x, y, 1, theStack, Minecraft.getMinecraft(), 1F, true);
	}

	public static void drawIconOnSide(IIcon toDraw, int w, int h, int scaledHeight, int scaledWidth, ForgeDirection side, int index) {

		if (side == ForgeDirection.EAST) {
			drawIcon(scaledWidth - w + 8, scaledHeight / 2 - h / 2 + 2 + (index * 17), toDraw);
		}
	}

	public static void drawFixedIconOnSide(IIcon toDraw, int w, int h, int scaledHeight, int scaledWidth, ForgeDirection side, int index) {

		if (side == ForgeDirection.EAST) {
			drawFixedIcon(scaledWidth - w + 8, scaledHeight / 2 - h / 2 + 2 + (index * 17), toDraw);
		}
	}

	public static void drawIcon(int x, int y, IIcon toDraw) {

		RenderHelper.renderIcon(x, y, ZLEVEL, toDraw, toDraw.getIconWidth(), toDraw.getIconHeight());
	}

	public static void drawFixedIcon(int x, int y, IIcon toDraw) {

		RenderHelper.renderIcon(x, y, ZLEVEL, toDraw, 16, 16);
	}

	public static void drawGradientRect(int x, int y, int w, int h, int colour1, int colour2) {

		gui.drawGradientRect(x, y, x + w, y + h, colour1, colour2);
	}

	public static class GuiHook extends Gui {

		public GuiHook(float zlevel) {

			this.zLevel = zlevel;
		}

		public void setZLevel(float f) {

			zLevel = f;
		}

		public float getZLevel() {

			return zLevel;
		}

		public void incZLevel(float f) {

			zLevel += f;
		}

		@Override
		public void drawGradientRect(int par1, int par2, int par3, int par4, int par5, int par6) {

			super.drawGradientRect(par1, par2, par3, par4, par5, par6);
		}
	}

	public static final int ZLEVEL = 301;

	public static final GuiHook gui = new GuiHook(ZLEVEL);

}
