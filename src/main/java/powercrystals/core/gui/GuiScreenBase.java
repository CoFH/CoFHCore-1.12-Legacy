package powercrystals.core.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public abstract class GuiScreenBase extends GuiContainer {

	protected List<Control> _controls = new LinkedList<Control>();
	protected ResourceLocation _backgroundTexture;

	public GuiScreenBase(Container container, String backgroundTexture) {

		super(container);
		_backgroundTexture = new ResourceLocation(backgroundTexture);
	}

	protected void addControl(Control control) {

		_controls.add(control);
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
		for (Control c : _controls) {
			if (c.getVisible()) {
				c.drawBackground(mouseX, mouseY, gameTicks);
			}
		}
		GL11.glPopMatrix();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		mouseX -= guiLeft;
		mouseY -= guiTop;
		for (Control c : _controls) {
			if (c.getVisible()) {
				c.drawForeground(mouseX, mouseY);
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float gameTicks) {

		super.drawScreen(mouseX, mouseY, gameTicks);

		mouseX -= guiLeft;
		mouseY -= guiTop;
		for (Control c : _controls) {
			if (c.getVisible() && c.getEnabled()) {
				c.drawTooltip(mouseX, mouseY, gameTicks);
			}
		}

		for (int i = _controls.size() - 1; i >= 0; i--) {
			Control c = _controls.get(i);
			if (!c.visible || !c.enabled) {
				continue;
			}
			c.updateTick(mouseX, mouseY);
		}
	}

	@Override
	public void handleMouseInput() {

		super.handleMouseInput();
		int mouseX = Mouse.getEventX() * width / mc.displayWidth - guiLeft;
		int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1 - guiTop;
		int wheelMovement = Mouse.getEventDWheel();

		if (wheelMovement != 0) {
			for (int i = _controls.size() - 1; i >= 0; i--) {
				Control c = _controls.get(i);
				if (!c.isPointInBounds(mouseX, mouseY) || !c.visible || !c.enabled) {
					continue;
				}
				if (c.onMouseWheel(mouseX, mouseY, wheelMovement)) {
					return;
				}
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {

		mouseX -= guiLeft;
		mouseY -= guiTop;

		for (int i = _controls.size() - 1; i >= 0; i--) {
			Control c = _controls.get(i);
			if (!c.isPointInBounds(mouseX, mouseY) || !c.visible || !c.enabled) {
				continue;
			}
			if (c.onMousePressed(mouseX, mouseY, mouseButton)) {
				return;
			}
		}

		mouseX += guiLeft;
		mouseY += guiTop;
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseMovedOrUp(int mouseX, int mouseY, int event) {

		mouseX -= guiLeft;
		mouseY -= guiTop;

		if (event == 0 || event == 1) {
			for (int i = _controls.size() - 1; i >= 0; i--) {
				Control c = _controls.get(i);
				if (!c.visible || !c.enabled) {
					continue;
				}
				c.onMouseReleased(mouseX, mouseY);
			}
		}

		mouseX += guiLeft;
		mouseY += guiTop;
		super.mouseMovedOrUp(mouseX, mouseY, event);
	}

	@Override
	protected void keyTyped(char characterTyped, int keyPressed) {

		for (int i = _controls.size() - 1; i >= 0; i--) {
			Control c = _controls.get(i);
			if (!c.visible || !c.enabled) {
				continue;
			}
			if (c.onKeyTyped(characterTyped, keyPressed)) {
				return;
			}
		}
		super.keyTyped(characterTyped, keyPressed);
	}

	protected boolean isPointInArea(int x1, int y1, int width, int height, int px, int py) {

		return px >= x1 - 1 && px < x1 + width + 1 && py >= y1 - 1 && py < y1 + height + 1;
	}
}
