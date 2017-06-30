package cofh.lib.gui;

import cofh.lib.audio.SoundBase;
import cofh.lib.gui.element.ElementBase;
import cofh.lib.gui.element.TabBase;
import cofh.lib.gui.slot.SlotFalseCopy;
import cofh.lib.util.helpers.RenderHelper;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Base class for a modular GUIs. Works with Elements {@link ElementBase} and Tabs {@link TabBase} which are both modular elements.
 *
 * @author King Lemming
 */
public abstract class GuiBase extends GuiContainer {

	public static final SoundHandler guiSoundManager = FMLClientHandler.instance().getClient().getSoundHandler();

	protected boolean drawTitle = true;
	protected boolean drawInventory = true;
	protected int mouseX = 0;
	protected int mouseY = 0;

	protected int lastIndex = -1;

	protected String name;
	protected ResourceLocation texture;

	public ArrayList<TabBase> tabs = new ArrayList<>();
	protected ArrayList<ElementBase> elements = new ArrayList<>();

	protected List<String> tooltip = new LinkedList<>();
	protected boolean tooltips = true;

	public static void playSound(String name, SoundCategory category, float volume, float pitch) {

		guiSoundManager.playSound(new SoundBase(name, category, volume, pitch));
	}

	public static void playClickSound(float volume, float pitch) {

		guiSoundManager.playSound(new SoundBase(SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, volume, pitch));
	}

	public GuiBase(Container container) {

		super(container);
	}

	public GuiBase(Container container, ResourceLocation texture) {

		super(container);
		this.texture = texture;
	}

	@Override
	public void initGui() {

		super.initGui();
		tabs.clear();
		elements.clear();
	}

	@Override
	public void drawScreen(int x, int y, float partialTick) {

		updateElementInformation();

		super.drawScreen(x, y, partialTick);

		if (tooltips && mc.player.inventory.getItemStack().isEmpty()) {
			addTooltips(tooltip);
			drawTooltip(tooltip);
		}
		mouseX = x - guiLeft;
		mouseY = y - guiTop;

		updateElements();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		if (drawTitle & name != null) {
			fontRenderer.drawString(StringHelper.localize(name), getCenteredOffset(StringHelper.localize(name)), 6, 0x404040);
		}
		if (drawInventory) {
			fontRenderer.drawString(I18n.translateToLocal("container.inventory"), 8, ySize - 96 + 3, 0x404040);
		}
		drawElements(0, true);
		drawTabs(0, true);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int x, int y) {

		GlStateManager.color(1, 1, 1, 1);
		bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		mouseX = x - guiLeft;
		mouseY = y - guiTop;

		GlStateManager.pushMatrix();
		GlStateManager.translate(guiLeft, guiTop, 0.0F);
		drawElements(partialTick, false);
		drawTabs(partialTick, false);
		GlStateManager.popMatrix();
	}

	@Override
	protected void keyTyped(char characterTyped, int keyPressed) throws IOException {

		for (int i = elements.size(); i-- > 0; ) {
			ElementBase c = elements.get(i);
			if (!c.isVisible() || !c.isEnabled()) {
				continue;
			}
			if (c.onKeyTyped(characterTyped, keyPressed)) {
				return;
			}
		}
		super.keyTyped(characterTyped, keyPressed);
	}

	@Override
	public void handleMouseInput() throws IOException {

		int x = Mouse.getEventX() * width / mc.displayWidth;
		int y = height - Mouse.getEventY() * height / mc.displayHeight - 1;

		mouseX = x - guiLeft;
		mouseY = y - guiTop;

		int wheelMovement = Mouse.getEventDWheel();

		if (wheelMovement != 0) {
			for (int i = elements.size(); i-- > 0; ) {
				ElementBase c = elements.get(i);
				if (!c.isVisible() || !c.isEnabled() || !c.intersectsWith(mouseX, mouseY)) {
					continue;
				}
				if (c.onMouseWheel(mouseX, mouseY, wheelMovement)) {
					return;
				}
			}
			TabBase tab = getTabAtPosition(mouseX, mouseY);

			if (tab != null && tab.onMouseWheel(mouseX, mouseY, wheelMovement)) {
				return;
			}

			if (onMouseWheel(mouseX, mouseY, wheelMovement)) {
				return;
			}
		}
		super.handleMouseInput();
	}

	protected boolean onMouseWheel(int mouseX, int mouseY, int wheelMovement) {

		return false;
	}

	@Override
	protected void mouseClicked(int mX, int mY, int mouseButton) throws IOException {

		mX -= guiLeft;
		mY -= guiTop;

		for (int i = elements.size(); i-- > 0; ) {
			ElementBase c = elements.get(i);
			if (!c.isVisible() || !c.isEnabled() || !c.intersectsWith(mX, mY)) {
				continue;
			}
			if (c.onMousePressed(mX, mY, mouseButton)) {
				return;
			}
		}

		TabBase tab = getTabAtPosition(mX, mY);
		if (tab != null) {
			int tMx = mX;

			if (!tab.onMousePressed(tMx, mY, mouseButton)) {
				for (int i = tabs.size(); i-- > 0; ) {
					TabBase other = tabs.get(i);
					if (other != tab && other.open && other.side == tab.side) {
						other.toggleOpen();
					}
				}
				tab.toggleOpen();
				return;
			}
		}

		mX += guiLeft;
		mY += guiTop;

		if (tab != null) {
			switch (tab.side) {
				case TabBase.LEFT:
					// guiLeft -= tab.currentWidth;
					break;
				case TabBase.RIGHT:
					xSize += tab.currentWidth;
					break;
			}
		}
		super.mouseClicked(mX, mY, mouseButton);
		if (tab != null) {
			switch (tab.side) {
				case TabBase.LEFT:
					// guiLeft += tab.currentWidth;
					break;
				case TabBase.RIGHT:
					xSize -= tab.currentWidth;
					break;
			}
		}
	}

	@Override
	protected void mouseReleased(int mX, int mY, int mouseButton) {

		mX -= guiLeft;
		mY -= guiTop;

		if (mouseButton >= 0 && mouseButton <= 2) { // 0:left, 1:right, 2: middle
			for (int i = elements.size(); i-- > 0; ) {
				ElementBase c = elements.get(i);
				if (!c.isVisible() || !c.isEnabled()) { // no bounds checking on mouseUp events
					continue;
				}
				c.onMouseReleased(mX, mY);
			}
		}
		mX += guiLeft;
		mY += guiTop;

		super.mouseReleased(mX, mY, mouseButton);
	}

	@Override
	protected void mouseClickMove(int mX, int mY, int lastClick, long timeSinceClick) {

		Slot slot = getSlotAtPosition(mX, mY);
		ItemStack itemstack = this.mc.player.inventory.getItemStack();

		if (this.dragSplitting && slot != null && !itemstack.isEmpty() && slot instanceof SlotFalseCopy) {
			if (lastIndex != slot.slotNumber) {
				lastIndex = slot.slotNumber;
				this.handleMouseClick(slot, slot.slotNumber, 0, ClickType.PICKUP);
			}
		} else {
			lastIndex = -1;
			super.mouseClickMove(mX, mY, lastClick, timeSinceClick);
		}
	}

	public Slot getSlotAtPosition(int xCoord, int yCoord) {

		for (int k = 0; k < this.inventorySlots.inventorySlots.size(); ++k) {
			Slot slot = this.inventorySlots.inventorySlots.get(k);

			if (this.isMouseOverSlot(slot, xCoord, yCoord)) {
				return slot;
			}
		}
		return null;
	}

	public boolean isMouseOverSlot(Slot theSlot, int xCoord, int yCoord) {

		return this.isPointInRegion(theSlot.xPos, theSlot.yPos, 16, 16, xCoord, yCoord);
	}

	/**
	 * Draws the elements for this GUI.
	 */
	protected void drawElements(float partialTick, boolean foreground) {

		if (foreground) {
			for (ElementBase element : elements) {
				if (element.isVisible()) {
					element.drawForeground(mouseX, mouseY);
				}
			}
		} else {
			for (ElementBase element : elements) {
				if (element.isVisible()) {
					element.drawBackground(mouseX, mouseY, partialTick);
				}
			}
		}
	}

	/**
	 * Draws the tabs for this GUI. Handles Tab open/close animation.
	 */
	protected void drawTabs(float partialTick, boolean foreground) {

		int yPosRight = 4;
		int yPosLeft = 4;

		if (foreground) {
			for (TabBase tab : tabs) {
				tab.update();
				if (!tab.isVisible()) {
					continue;
				}
				if (tab.side == TabBase.LEFT) {
					tab.drawForeground(mouseX, mouseY);
					yPosLeft += tab.currentHeight;
				} else {
					tab.drawForeground(mouseX, mouseY);
					yPosRight += tab.currentHeight;
				}
			}
		} else {
			for (TabBase tab : tabs) {
				tab.update();
				if (!tab.isVisible()) {
					continue;
				}
				if (tab.side == TabBase.LEFT) {
					tab.setPosition(0, yPosLeft);
					tab.drawBackground(mouseX, mouseY, partialTick);
					yPosLeft += tab.currentHeight;
				} else {
					tab.setPosition(xSize, yPosRight);
					tab.drawBackground(mouseX, mouseY, partialTick);
					yPosRight += tab.currentHeight;
				}
			}
		}
	}

	public void addTooltips(List<String> tooltip) {

		TabBase tab = getTabAtPosition(mouseX, mouseY);

		if (tab != null) {
			tab.addTooltip(tooltip);
		}
		ElementBase element = getElementAtPosition(mouseX, mouseY);

		if (element != null && element.isVisible()) {
			element.addTooltip(tooltip);
		}
	}

	/* ELEMENTS */
	public ElementBase addElement(ElementBase element) {

		elements.add(element);
		return element;
	}

	public TabBase addTab(TabBase tab) {

		int yOffset = 4;
		for (TabBase tab1 : tabs) {
			if (tab1.side == tab.side && tab1.isVisible()) {
				yOffset += tab1.currentHeight;
			}
		}
		tab.setPosition(tab.side == TabBase.LEFT ? 0 : xSize, yOffset);
		tabs.add(tab);

		if (TabTracker.getOpenedLeftTab() != null && tab.getClass().equals(TabTracker.getOpenedLeftTab())) {
			tab.setFullyOpen();
		} else if (TabTracker.getOpenedRightTab() != null && tab.getClass().equals(TabTracker.getOpenedRightTab())) {
			tab.setFullyOpen();
		}
		return tab;
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

	protected TabBase getTabAtPosition(int mX, int mY) {

		int xShift = 0;
		int yShift = 4;

		for (TabBase tab : tabs) {
			if (!tab.isVisible() || tab.side == TabBase.RIGHT) {
				continue;
			}
			tab.setCurrentShift(xShift, yShift);
			if (tab.intersectsWith(mX, mY, xShift, yShift)) {
				return tab;
			}
			yShift += tab.currentHeight;
		}

		xShift = xSize;
		yShift = 4;

		for (TabBase tab : tabs) {
			if (!tab.isVisible() || tab.side == TabBase.LEFT) {
				continue;
			}
			tab.setCurrentShift(xShift, yShift);
			if (tab.intersectsWith(mX, mY, xShift, yShift)) {
				return tab;
			}
			yShift += tab.currentHeight;
		}
		return null;
	}

	protected final void updateElements() {

		for (int i = elements.size(); i-- > 0; ) {
			ElementBase c = elements.get(i);
			if (c.isVisible() && c.isEnabled()) {
				c.update(mouseX, mouseY);
			}
		}
	}

	protected void updateElementInformation() {

	}

	public void handleElementButtonClick(String buttonName, int mouseButton) {

	}

	/* HELPERS */
	public void bindTexture(ResourceLocation texture) {

		mc.renderEngine.bindTexture(texture);
	}

	/**
	 * Essentially a placeholder method for tabs to use should they need to draw a button.
	 */
	public void drawButton(TextureAtlasSprite icon, int x, int y, int mode) {

		drawIcon(icon, x, y);
	}

	public void drawItemStack(ItemStack stack, int x, int y, boolean drawOverlay, String overlayTxt) {

		GlStateManager.enableDepth();
		GlStateManager.enableLighting();
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, 0.0F, 32.0F);
		this.zLevel = 200.0F;
		itemRender.zLevel = 200.0F;

		FontRenderer font = null;
		if (!stack.isEmpty()) {
			font = stack.getItem().getFontRenderer(stack);
		}
		if (font == null) {
			font = fontRenderer;
		}

		itemRender.renderItemAndEffectIntoGUI(stack, x, y);

		if (drawOverlay) {
			itemRender.renderItemOverlayIntoGUI(font, stack, x, y - (this.draggedStack.isEmpty() ? 0 : 8), overlayTxt);
		}
		this.zLevel = 0.0F;
		itemRender.zLevel = 0.0F;
		GlStateManager.popMatrix();
		GlStateManager.disableLighting();
	}

	/**
	 * Simple method used to draw a fluid of arbitrary size.
	 */
	public void drawFluid(int x, int y, FluidStack fluid, int width, int height) {

		if (fluid == null) {
			return;
		}
		RenderHelper.setBlockTextureSheet();
		int color = fluid.getFluid().getColor(fluid);
		RenderHelper.setGLColorFromInt(color);
		drawTiledTexture(x, y, RenderHelper.getTexture(fluid.getFluid().getStill(fluid)), width, height);
	}

	public void drawTiledTexture(int x, int y, TextureAtlasSprite icon, int width, int height) {

		int i;
		int j;

		int drawHeight;
		int drawWidth;

		for (i = 0; i < width; i += 16) {
			for (j = 0; j < height; j += 16) {
				drawWidth = Math.min(width - i, 16);
				drawHeight = Math.min(height - j, 16);
				drawScaledTexturedModelRectFromIcon(x + i, y + j, icon, drawWidth, drawHeight);
			}
		}
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public void drawIcon(TextureAtlasSprite icon, int x, int y) {

		RenderHelper.setBlockTextureSheet();
		GlStateManager.color(1, 1, 1, 1);
		drawTexturedModalRect(x, y, icon, 16, 16);
	}

	public void drawColorIcon(TextureAtlasSprite icon, int x, int y) {

		drawTexturedModalRect(x, y, icon, 16, 16);
	}

	public void drawSizedModalRect(int x1, int y1, int x2, int y2, int color) {

		int temp;

		if (x1 < x2) {
			temp = x1;
			x1 = x2;
			x2 = temp;
		}
		if (y1 < y2) {
			temp = y1;
			y1 = y2;
			y2 = temp;
		}

		float a = (color >> 24 & 255) / 255.0F;
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(r, g, b, a);

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		buffer.pos(x1, y2, this.zLevel).endVertex();
		buffer.pos(x2, y2, this.zLevel).endVertex();
		buffer.pos(x2, y1, this.zLevel).endVertex();
		buffer.pos(x1, y1, this.zLevel).endVertex();
		Tessellator.getInstance().draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public void drawSizedRect(int x1, int y1, int x2, int y2, int color) {

		int temp;

		if (x1 < x2) {
			temp = x1;
			x1 = x2;
			x2 = temp;
		}
		if (y1 < y2) {
			temp = y1;
			y1 = y2;
			y2 = temp;
		}

		float a = (color >> 24 & 255) / 255.0F;
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		GlStateManager.disableTexture2D();
		GlStateManager.color(r, g, b, a);

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		buffer.pos(x1, y2, this.zLevel).endVertex();
		buffer.pos(x2, y2, this.zLevel).endVertex();
		buffer.pos(x2, y1, this.zLevel).endVertex();
		buffer.pos(x1, y1, this.zLevel).endVertex();
		Tessellator.getInstance().draw();
		GlStateManager.enableTexture2D();
	}

	public void drawSizedTexturedModalRect(int x, int y, int u, int v, int width, int height, float texW, float texH) {

		float texU = 1 / texW;
		float texV = 1 / texH;
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x, y + height, this.zLevel).tex((u) * texU, (v + height) * texV).endVertex();
		buffer.pos(x + width, y + height, this.zLevel).tex((u + width) * texU, (v + height) * texV).endVertex();
		buffer.pos(x + width, y, this.zLevel).tex((u + width) * texU, (v) * texV).endVertex();
		buffer.pos(x, y, this.zLevel).tex((u) * texU, (v) * texV).endVertex();
		Tessellator.getInstance().draw();
	}

	public void drawScaledTexturedModelRectFromIcon(int x, int y, TextureAtlasSprite icon, int width, int height) {

		if (icon == null) {
			return;
		}
		double minU = icon.getMinU();
		double maxU = icon.getMaxU();
		double minV = icon.getMinV();
		double maxV = icon.getMaxV();

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x, y + height, this.zLevel).tex(minU, minV + (maxV - minV) * height / 16F).endVertex();
		buffer.pos(x + width, y + height, this.zLevel).tex(minU + (maxU - minU) * width / 16F, minV + (maxV - minV) * height / 16F).endVertex();
		buffer.pos(x + width, y, this.zLevel).tex(minU + (maxU - minU) * width / 16F, minV).endVertex();
		buffer.pos(x, y, this.zLevel).tex(minU, minV).endVertex();
		Tessellator.getInstance().draw();
	}

	public void drawTooltip(List<String> list) {

		drawTooltipHoveringText(list, mouseX + guiLeft, mouseY + guiTop, fontRenderer);
		tooltip.clear();
	}

	protected void drawTooltipHoveringText(List<String> list, int x, int y, FontRenderer font) {

		if (list == null || list.isEmpty()) {
			return;
		}
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		int k = 0;

		for (String s : list) {
			int l = font.getStringWidth(s);

			if (l > k) {
				k = l;
			}
		}
		int i1 = x + 12;
		int j1 = y - 12;
		int k1 = 8;

		if (list.size() > 1) {
			k1 += 2 + (list.size() - 1) * 10;
		}
		if (i1 + k > this.width) {
			i1 -= 28 + k;
		}
		if (j1 + k1 + 6 > this.height) {
			j1 = this.height - k1 - 6;
		}
		this.zLevel = 300.0F;
		itemRender.zLevel = 300.0F;
		int l1 = -267386864;
		this.drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
		this.drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
		this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
		this.drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
		this.drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
		int i2 = 1347420415;
		int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
		this.drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
		this.drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2);
		this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
		this.drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);

		for (int k2 = 0; k2 < list.size(); ++k2) {
			String s1 = list.get(k2);
			font.drawStringWithShadow(s1, i1, j1, -1);

			if (k2 == 0) {
				j1 += 2;
			}
			j1 += 10;
		}
		this.zLevel = 0.0F;
		itemRender.zLevel = 0.0F;
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.enableRescaleNormal();
	}

	/**
	 * Passthrough method for tab use.
	 */
	public void mouseClicked(int mouseButton) throws IOException {

		super.mouseClicked(guiLeft + mouseX, guiTop + mouseY, mouseButton);
	}

	public FontRenderer getFontRenderer() {

		return fontRenderer;
	}

	protected int getCenteredOffset(String string) {

		return this.getCenteredOffset(string, xSize);
	}

	protected int getCenteredOffset(String string, int xWidth) {

		return (xWidth - fontRenderer.getStringWidth(string)) / 2;
	}

	public int getGuiLeft() {

		return guiLeft;
	}

	public int getGuiTop() {

		return guiTop;
	}

	public int getMouseX() {

		return mouseX;
	}

	public int getMouseY() {

		return mouseY;
	}

}
