package cofh.core.gui.element;

import cofh.core.gui.GuiContainerCore;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;

/**
 * Base class for a modular GUI element. Has self-contained rendering methods and a link back to the {@link GuiContainerCore} it is a part of.
 *
 * @author King Lemming
 */
public abstract class ElementBase {

	protected GuiContainerCore gui;
	protected ResourceLocation texture;
	private FontRenderer fontRenderer;

	protected int posX;
	protected int posY;

	protected int sizeX;
	protected int sizeY;

	protected int texW = 256;
	protected int texH = 256;

	protected String name;

	private boolean visible = true;
	private boolean enabled = true;

	public ElementBase(GuiContainerCore gui, int posX, int posY) {

		this.gui = gui;
		this.posX = posX;
		this.posY = posY;
	}

	public ElementBase(GuiContainerCore gui, int posX, int posY, int width, int height) {

		this.gui = gui;
		this.posX = posX;
		this.posY = posY;
		this.sizeX = width;
		this.sizeY = height;
	}

	public ElementBase setName(String name) {

		this.name = name;
		return this;
	}

	public ElementBase setPosition(int posX, int posY) {

		this.posX = posX;
		this.posY = posY;
		return this;
	}

	public ElementBase setSize(int sizeX, int sizeY) {

		this.sizeX = sizeX;
		this.sizeY = sizeY;
		return this;
	}

	public ElementBase setTexture(String texture, int texW, int texH) {

		this.texture = new ResourceLocation(texture);
		this.texW = texW;
		this.texH = texH;
		return this;
	}

	public final ElementBase setVisible(boolean visible) {

		this.visible = visible;
		return this;
	}

	public boolean isVisible() {

		return visible;
	}

	public final ElementBase setEnabled(boolean enabled) {

		this.enabled = enabled;
		return this;
	}

	public boolean isEnabled() {

		return enabled;
	}

	public void update(int mouseX, int mouseY) {

		update();
	}

	public void update() {

	}

	public abstract void drawBackground(int mouseX, int mouseY, float gameTicks);

	public abstract void drawForeground(int mouseX, int mouseY);

	public void addTooltip(List<String> list) {

	}

	public void drawModalRect(int x, int y, int width, int height, int color) {

		gui.drawSizedModalRect(x, y, width, height, color);
	}

	public void drawStencil(int xStart, int yStart, int xEnd, int yEnd, int flag) {

		GlStateManager.disableTexture2D();
		GL11.glStencilFunc(GL11.GL_ALWAYS, flag, flag);
		GL11.glStencilOp(GL11.GL_ZERO, GL11.GL_ZERO, GL11.GL_REPLACE);
		GL11.glStencilMask(flag);
		GlStateManager.colorMask(false, false, false, false);
		GlStateManager.depthMask(false);
		GL11.glClearStencil(0);
		GlStateManager.clear(GL11.GL_STENCIL_BUFFER_BIT);

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		buffer.pos(xStart, yEnd, 0).endVertex();
		buffer.pos(xEnd, yEnd, 0).endVertex();
		buffer.pos(xEnd, yStart, 0).endVertex();
		buffer.pos(xStart, yStart, 0).endVertex();
		Tessellator.getInstance().draw();

		GlStateManager.enableTexture2D();
		GL11.glStencilFunc(GL11.GL_EQUAL, flag, flag);
		GL11.glStencilMask(0);
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.depthMask(true);
	}

	public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {

		gui.drawSizedTexturedModalRect(x, y, u, v, width, height, texW, texH);
	}

	public void drawCenteredString(FontRenderer fontRenderer, String text, int x, int y, int color) {

		fontRenderer.drawStringWithShadow(text, x - fontRenderer.getStringWidth(text) / 2, y, color);
	}

	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) throws IOException {

		return false;
	}

	public void onMouseReleased(int mouseX, int mouseY) {

	}

	public boolean onMouseWheel(int mouseX, int mouseY, int movement) {

		return false;
	}

	public boolean onKeyTyped(char characterTyped, int keyPressed) {

		return false;
	}

	public boolean intersectsWith(int mouseX, int mouseY) {

		return mouseX >= this.posX && mouseX < this.posX + this.sizeX && mouseY >= this.posY && mouseY < this.posY + this.sizeY;
	}

	public FontRenderer getFontRenderer() {

		return fontRenderer == null ? gui.getFontRenderer() : fontRenderer;
	}

	public ElementBase setFontRenderer(FontRenderer renderer) {

		fontRenderer = renderer;
		return this;
	}

	public final String getName() {

		return name;
	}

	public final GuiContainerCore getContainerScreen() {

		return gui;
	}

	/**
	 * This method is relative to the GUI's y coordinate
	 */
	public final int getPosY() {

		return posY;
	}

	/**
	 * This method is relative to the GUI's x coordinate
	 */
	public final int getPosX() {

		return posX;
	}

	public final int getHeight() {

		return sizeY;
	}

	public final int getWidth() {

		return sizeX;
	}

}
