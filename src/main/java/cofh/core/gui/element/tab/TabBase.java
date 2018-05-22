package cofh.core.gui.element.tab;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementBase;
import cofh.core.init.CoreProps;
import cofh.core.util.Rectangle4i;
import cofh.core.util.helpers.RenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for a tab element. Has self-contained rendering methods and a link back to the {@link GuiContainerCore} it is a part of.
 *
 * @author King Lemming
 */
public abstract class TabBase extends ElementBase {

	public static final ResourceLocation DEFAULT_TEXTURE_LEFT = new ResourceLocation(CoreProps.PATH_ELEMENTS + "tab_left.png");
	public static final ResourceLocation DEFAULT_TEXTURE_RIGHT = new ResourceLocation(CoreProps.PATH_ELEMENTS + "tab_right.png");

	public static final int LEFT = 0;
	public static final int RIGHT = 1;

	public static int tabExpandSpeed = 8;

	protected int offsetX = 0;
	protected int offsetY = 0;

	public boolean open;
	public boolean fullyOpen;
	public int side = RIGHT;

	public int headerColor = 0xe1c92f;
	public int subheaderColor = 0xaaafb8;
	public int textColor = 0x000000;
	public int backgroundColor = 0xffffff;

	protected int currentShiftX = 0;
	protected int currentShiftY = 0;

	public int minWidth = 22;
	public int maxWidth = 124;
	public int currentWidth = minWidth;

	public int minHeight = 22;
	public int maxHeight = 22;
	public int currentHeight = minHeight;

	protected ArrayList<ElementBase> elements = new ArrayList<>();

	public TabBase(GuiContainerCore gui) {

		super(gui, 0, 0);
		texture = DEFAULT_TEXTURE_RIGHT;
	}

	public TabBase(GuiContainerCore gui, int side) {

		super(gui, 0, 0);
		this.side = side;

		if (side == LEFT) {
			texture = DEFAULT_TEXTURE_LEFT;
		} else {
			texture = DEFAULT_TEXTURE_RIGHT;
		}
	}

	public TabBase setOffsets(int x, int y) {

		posX -= offsetX;
		posY -= offsetY;
		offsetX = x;
		offsetY = y;
		posX += offsetX;
		posY += offsetY;

		return this;
	}

	@Override
	public TabBase setPosition(int posX, int posY) {

		this.posX = posX + offsetX;
		this.posY = posY + offsetY;
		return this;
	}

	protected void drawForeground() {

	}

	protected void drawBackground() {

		float colorR = (backgroundColor >> 16 & 255) / 255.0F;
		float colorG = (backgroundColor >> 8 & 255) / 255.0F;
		float colorB = (backgroundColor & 255) / 255.0F;

		GlStateManager.color(colorR, colorG, colorB, 1.0F);

		RenderHelper.bindTexture(texture);

		gui.drawTexturedModalRect(0, 4, 0, 256 - currentHeight + 4, 4, currentHeight - 4);
		gui.drawTexturedModalRect(4, 0, 256 - currentWidth + 4, 0, currentWidth - 4, 4);
		gui.drawTexturedModalRect(0, 0, 0, 0, 4, 4);
		gui.drawTexturedModalRect(4, 4, 256 - currentWidth + 4, 256 - currentHeight + 4, currentWidth - 4, currentHeight - 4);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {

		mouseX -= this.posX();
		mouseY -= this.posY;

		GlStateManager.pushMatrix();
		GlStateManager.translate(this.posX(), this.posY, 0.0F);

		drawBackground();

		for (ElementBase element : elements) {
			if (element.isVisible()) {
				element.drawBackground(mouseX, mouseY, gameTicks);
			}
		}
		GlStateManager.popMatrix();
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

		mouseX -= this.posX();
		mouseY -= this.posY;

		GlStateManager.pushMatrix();
		GlStateManager.translate(this.posX(), this.posY, 0.0F);

		drawForeground();

		for (ElementBase element : elements) {
			if (element.isVisible()) {
				element.drawForeground(mouseX, mouseY);
			}
		}
		GlStateManager.popMatrix();
	}

	@Override
	public void update(int mouseX, int mouseY) {

		super.update(mouseX, mouseY);

		mouseX -= this.posX();
		mouseY -= this.posY;

		for (int i = elements.size(); i-- > 0; ) {
			ElementBase c = elements.get(i);
			if (c.isVisible() && c.isEnabled()) {
				c.update(mouseX, mouseY);
			}
		}
	}

	@Override
	public void update() {

		if (open && currentWidth < maxWidth) {
			currentWidth += tabExpandSpeed;
		} else if (!open && currentWidth > minWidth) {
			currentWidth -= tabExpandSpeed;
		}
		if (currentWidth > maxWidth) {
			currentWidth = maxWidth;
		} else if (currentWidth < minWidth) {
			currentWidth = minWidth;
		}
		if (open && currentHeight < maxHeight) {
			currentHeight += tabExpandSpeed;
		} else if (!open && currentHeight > minHeight) {
			currentHeight -= tabExpandSpeed;
		}
		if (currentHeight > maxHeight) {
			currentHeight = maxHeight;
		} else if (currentHeight < minHeight) {
			currentHeight = minHeight;
		}
		if (!fullyOpen && open && currentWidth == maxWidth && currentHeight == maxHeight) {
			setFullyOpen();
		}
	}

	protected void drawTabIcon(TextureAtlasSprite iconName) {

		gui.drawIcon(iconName, sideOffset(), 3);
	}

	/**
	 * Shortcut to correct for the proper X position.
	 */
	protected int posX() {

		if (side == LEFT) {
			return posX - currentWidth;
		}
		return posX;
	}

	/**
	 * Corrects for shadowing differences in tabs to ensure that they always look nice - used in font rendering, typically.
	 */
	protected int posXOffset() {

		return posX() + sideOffset();
	}

	protected int sideOffset() {

		return (side == LEFT ? 4 : 2);
	}

	public boolean intersectsWith(int mouseX, int mouseY, int shiftX, int shiftY) {

		shiftX += offsetX;
		shiftY += offsetY;

		if (side == LEFT) {
			if (mouseX <= shiftX && mouseX >= shiftX - currentWidth && mouseY >= shiftY && mouseY <= shiftY + currentHeight) {
				return true;
			}
		} else if (mouseX >= shiftX && mouseX <= shiftX + currentWidth && mouseY >= shiftY && mouseY <= shiftY + currentHeight) {
			return true;
		}
		return false;
	}

	public boolean isFullyOpened() {

		return fullyOpen;
	}

	public void setCurrentShift(int x, int y) {

		updateElements();

		currentShiftX = x + offsetX;
		currentShiftY = y + offsetY;
	}

	public void setFullyOpen() {

		open = true;
		currentWidth = maxWidth;
		currentHeight = maxHeight;
		fullyOpen = true;

		updateElements();
	}

	public void toggleOpen() {

		if (open) {
			open = false;
			if (side == LEFT) {
				TabTracker.setOpenedLeftTab(null);
			} else {
				TabTracker.setOpenedRightTab(null);
			}
			fullyOpen = false;
		} else {
			open = true;
			if (side == LEFT) {
				TabTracker.setOpenedLeftTab(this.getClass());
			} else {
				TabTracker.setOpenedRightTab(this.getClass());
			}
		}
		updateElements();
	}

	public Rectangle4i getBounds() {

		if (isVisible()) {
			return new Rectangle4i(posX() + gui.getGuiLeft(), posY + gui.getGuiTop(), currentWidth, currentHeight);
		} else {
			return new Rectangle4i(posX() + gui.getGuiLeft(), posY + gui.getGuiTop(), 0, 0);
		}

	}

	/* Elements */
	public ElementBase addElement(ElementBase element) {

		elements.add(element);
		return element;
	}

	protected ElementBase getElementAtPosition(int mX, int mY) {

		for (int i = elements.size(); i-- > 0; ) {
			ElementBase element = elements.get(i);
			if (element.intersectsWith(mX, mY)) {
				return element;
			}
		}
		return null;
	}

	/* Redirects to Elements */

	@Override
	public boolean onMouseWheel(int mouseX, int mouseY, int movement) {

		int wheelMovement = Mouse.getEventDWheel();

		mouseX -= this.posX();
		mouseY -= this.posY;

		if (wheelMovement != 0) {
			for (int i = elements.size(); i-- > 0; ) {
				ElementBase c = elements.get(i);
				if (!c.isVisible() || !c.isEnabled() || !c.intersectsWith(mouseX, mouseY)) {
					continue;
				}
				if (c.onMouseWheel(mouseX, mouseY, wheelMovement)) {
					return true;
				}
			}
		}
		return true;
	}

	@Override
	public void addTooltip(List<String> list) {

		for (int i = 0; i < this.elements.size(); i++) {
			ElementBase c = elements.get(i);

			if (!c.isVisible() || !c.isEnabled() || !c.intersectsWith(gui.getMouseX(), gui.getMouseY())) {
				continue;
			}
			c.addTooltip(list);
		}
	}

	@Override
	public boolean onKeyTyped(char characterTyped, int keyPressed) {

		for (int i = elements.size(); i-- > 0; ) {
			ElementBase c = elements.get(i);
			if (!c.isVisible() || !c.isEnabled()) {
				continue;
			}
			if (c.onKeyTyped(characterTyped, keyPressed)) {
				return true;
			}
		}
		return super.onKeyTyped(characterTyped, keyPressed);
	}

	/**
	 * @return Whether the tab should stay open or not.
	 */
	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) throws IOException {

		mouseX -= this.posX();
		mouseY -= this.posY;

		boolean shouldStayOpen = false;

		for (int i = 0; i < this.elements.size(); i++) {
			ElementBase c = elements.get(i);
			if (!c.isVisible() || !c.isEnabled() || !c.intersectsWith(mouseX, mouseY)) {
				continue;
			}
			shouldStayOpen = true;

			if (c.onMousePressed(mouseX, mouseY, mouseButton)) {
				return true;
			}
		}
		return shouldStayOpen;
	}

	@Override
	public void onMouseReleased(int mouseX, int mouseY) {

		mouseX -= this.posX();
		mouseY -= this.posY;

		for (int i = elements.size(); i-- > 0; ) {
			ElementBase c = elements.get(i);
			if (!c.isVisible() || !c.isEnabled()) { // no bounds checking on mouseUp events
				continue;
			}
			c.onMouseReleased(mouseX, mouseY);
		}
	}

	protected void updateElements() {

		for (ElementBase element : elements) {
			element.setVisible(this.isFullyOpened());
		}
	}

}
