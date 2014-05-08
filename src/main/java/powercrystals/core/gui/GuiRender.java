package powercrystals.core.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiRender
{
	public static void drawHorizontalLine(int x1, int x2, int y, int color)
	{
		if (x2 < x1)
		{
			int var5 = x1;
			x1 = x2;
			x2 = var5;
		}

		drawRect(x1, y, x2 + 1, y + 1, color);
	}

	public static void drawVerticalLine(int x, int y1, int y2, int color)
	{
		if (y2 < y1)
		{
			int temp = y1;
			y1 = y2;
			y2 = temp;
		}

		drawRect(x, y1 + 1, x + 1, y2, color);
	}

	public static void drawRect(int x1, int y1, int x2, int y2, int color)
	{
		int temp;

		if (x1 < x2)
		{
			temp = x1;
			x1 = x2;
			x2 = temp;
		}

		if (y1 < y2)
		{
			temp = y1;
			y1 = y2;
			y2 = temp;
		}

		float a = (color >> 24 & 255) / 255.0F;
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(r, g, b, a);
		tessellator.startDrawingQuads();
		tessellator.addVertex(x1, y2, 0.0D);
		tessellator.addVertex(x2, y2, 0.0D);
		tessellator.addVertex(x2, y1, 0.0D);
		tessellator.addVertex(x1, y1, 0.0D);
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void drawGradientRect(int x1, int x2, int y1, int y2, int color1, int color2, int zLevel)
	{
		float a1 = (color1 >> 24 & 255) / 255.0F;
		float r1 = (color1 >> 16 & 255) / 255.0F;
		float g1 = (color1 >> 8 & 255) / 255.0F;
		float b1 = (color1 & 255) / 255.0F;
		float a2 = (color2 >> 24 & 255) / 255.0F;
		float r2 = (color2 >> 16 & 255) / 255.0F;
		float g2 = (color2 >> 8 & 255) / 255.0F;
		float b2 = (color2 & 255) / 255.0F;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(r1, g1, b1, a1);
		tessellator.addVertex(x2, y1, zLevel);
		tessellator.addVertex(x1, y1, zLevel);
		tessellator.setColorRGBA_F(r2, g2, b2, a2);
		tessellator.addVertex(x1, y2, zLevel);
		tessellator.addVertex(x2, y2, zLevel);
		tessellator.draw();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static void drawCenteredString(FontRenderer fontRenderer, String text, int x, int y, int color)
	{
		fontRenderer.drawStringWithShadow(text, x - fontRenderer.getStringWidth(text) / 2, y, color);
	}
	
	public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height)
	{
		drawTexturedModalRect(x, y, u, v, width, height, 0.0F);
	}

	public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, float zLevel)
	{
		float xScale = 0.00390625F;
		float yScale = 0.00390625F;
		Tessellator var9 = Tessellator.instance;
		var9.startDrawingQuads();
		var9.addVertexWithUV(x,         y + height, zLevel, u * xScale,           (v + height) * yScale);
		var9.addVertexWithUV(x + width, y + height, zLevel, (u + width) * xScale, (v + height) * yScale);
		var9.addVertexWithUV(x + width, y,          zLevel, (u + width) * xScale, v * yScale);
		var9.addVertexWithUV(x,         y,          zLevel, u * xScale,           v * yScale);
		var9.draw();
	}
}
