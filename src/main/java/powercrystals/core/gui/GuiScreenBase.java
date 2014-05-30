package powercrystals.core.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public abstract class GuiScreenBase extends GuiContainer {

	protected List<Object> _controls = new LinkedList<Object>();
	protected ResourceLocation _backgroundTexture;

	public GuiScreenBase(Container container, String backgroundTexture) {

		super(container);
		_backgroundTexture = new ResourceLocation(backgroundTexture);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float gameTicks, int mouseX, int mouseY) {

		mouseX -= guiLeft;
		mouseY -= guiTop;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(_backgroundTexture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 0.0F);
		for (Object c : _controls) {
			//if (c.getVisible()) {
			//c.drawBackground(mouseX, mouseY, gameTicks);
			//}
		}
		GL11.glPopMatrix();
	}

	protected boolean isPointInArea(int x1, int y1, int width, int height, int px, int py) {

		return px >= x1 - 1 && px < x1 + width + 1 && py >= y1 - 1 && py < y1 + height + 1;
	}
}
