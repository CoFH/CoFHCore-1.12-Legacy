package powercrystals.core.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;

@SideOnly(Side.CLIENT)
public abstract class Control
{
	public static final String textureLocation = "powercrystalscore:textures/gui/";
	protected GuiContainer containerScreen;
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected boolean enabled = true;
	protected boolean visible = true;
	
	protected Control(GuiContainer containerScreen, int x, int y, int width, int height)
	{
		this.containerScreen = containerScreen;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public abstract void drawBackground(int mouseX, int mouseY, float gameTicks);
	
	public abstract void drawForeground(int mouseX, int mouseY);
	
	public void drawTooltip(int mouseX, int mouseY, float gameTicks) { }
	
	public GuiContainer getContainerScreen() { return containerScreen; }
	
	public final int getPosX() { return x; }
	
	public final int getPosY() { return y; }
	
	public final int getHeight() { return height; }
	
	public final int getWidth() { return width; }
	
	public final boolean getEnabled() { return enabled; }
	public final void setEnabled(boolean enabled) { this.enabled = enabled; }
	
	public final boolean getVisible() { return visible; }
	public final void setVisible(boolean visible) { this.visible = visible; }
	
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) { return false; }
	
	public void updateTick(int mouseX, int mouseY) { return; }
	
	public void onMouseReleased(int mouseX, int mouseY) { return; }
	
	public boolean onMouseWheel(int mouseX, int mouseY, int movement) { return false; }
	
	public boolean onKeyTyped(char characterTyped, int keyPressed) { return false; }

	public boolean isPointInBounds(int x, int y)
	{
		return x >= this.x - 1 && x < this.x + width + 1 && y >= this.y - 1 && y < this.y + height + 1;
	}
}
