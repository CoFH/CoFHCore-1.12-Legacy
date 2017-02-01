package cofh.core.gui.element;

import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.IReconfigurableSides;
import cofh.api.tileentity.ISidedTexture;
import cofh.core.init.CoreTextures;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.TabBase;
import cofh.lib.render.RenderHelper;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class TabConfiguration extends TabBase {

	public static int defaultSide = 1;
	public static int defaultHeaderColor = 0xe1c92f;
	public static int defaultSubHeaderColor = 0xaaafb8;
	public static int defaultTextColor = 0x000000;
	public static int defaultBackgroundColor = 0x226688;

	private IReconfigurableFacing myTileFacing;
	private IReconfigurableSides myTileSides;
	private ISidedTexture myTileTexture;

	public TabConfiguration(GuiBase gui, IReconfigurableSides theTile) {

		this(gui, defaultSide, theTile);
	}

	public TabConfiguration(GuiBase gui, int side, IReconfigurableSides theTile) {

		super(gui, side);

		headerColor = defaultHeaderColor;
		subheaderColor = defaultSubHeaderColor;
		textColor = defaultTextColor;
		backgroundColor = defaultBackgroundColor;

		maxHeight = 92;
		maxWidth = 100;
		myTileSides = theTile;
		myTileFacing = (IReconfigurableFacing) theTile;
		myTileTexture = (ISidedTexture) theTile;
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(StringHelper.localize("info.cofh.configuration"));
			return;
		}
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {

		if (!isFullyOpened()) {
			return false;
		}
		if (side == LEFT) {
			mouseX += currentWidth;
		}
		mouseX -= currentShiftX;
		mouseY -= currentShiftY;

		if (mouseX < 16 || mouseX >= 80 || mouseY < 20 || mouseY >= 84) {
			return false;
		}
		if (40 <= mouseX && mouseX < 56 && 24 <= mouseY && mouseY < 40) {
			handleSideChange(BlockHelper.SIDE_ABOVE[myTileFacing.getFacing()], mouseButton);
		} else if (20 <= mouseX && mouseX < 36 && 44 <= mouseY && mouseY < 60) {
			handleSideChange(BlockHelper.SIDE_LEFT[myTileFacing.getFacing()], mouseButton);
		} else if (40 <= mouseX && mouseX < 56 && 44 <= mouseY && mouseY < 60) {
			handleSideChange(myTileFacing.getFacing(), mouseButton);
		} else if (60 <= mouseX && mouseX < 76 && 44 <= mouseY && mouseY < 60) {
			handleSideChange(BlockHelper.SIDE_RIGHT[myTileFacing.getFacing()], mouseButton);
		} else if (40 <= mouseX && mouseX < 56 && 64 <= mouseY && mouseY < 80) {
			handleSideChange(BlockHelper.SIDE_BELOW[myTileFacing.getFacing()], mouseButton);
		} else if (60 <= mouseX && mouseX < 76 && 64 <= mouseY && mouseY < 80) {
			handleSideChange(BlockHelper.SIDE_OPPOSITE[myTileFacing.getFacing()], mouseButton);
		}
		return true;
	}

	@Override
	protected void drawBackground() {

		super.drawBackground();

		if (!isFullyOpened()) {
			return;
		}
		float colorR = (backgroundColor >> 16 & 255) / 255.0F * 0.6F;
		float colorG = (backgroundColor >> 8 & 255) / 255.0F * 0.6F;
		float colorB = (backgroundColor & 255) / 255.0F * 0.6F;
		GlStateManager.color(colorR, colorG, colorB, 1.0F);
		gui.drawTexturedModalRect(posX() + 16, posY + 20, 16, 20, 64, 64);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	protected void drawForeground() {

		drawTabIcon(CoreTextures.ICON_CONFIG);
		if (!isFullyOpened()) {
			return;
		}
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.configuration"), posXOffset() + 18, posY + 6, headerColor);
		RenderHelper.setBlockTextureSheet();

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		for (int i = 0; i < 2; i++) {
			gui.drawIcon(myTileTexture.getTexture(BlockHelper.SIDE_ABOVE[myTileFacing.getFacing()], i, 0), posX() + 40, posY + 24);
			gui.drawIcon(myTileTexture.getTexture(BlockHelper.SIDE_LEFT[myTileFacing.getFacing()], i, 0), posX() + 20, posY + 44);
			gui.drawIcon(myTileTexture.getTexture(myTileFacing.getFacing(), i, 0), posX() + 40, posY + 44);
			gui.drawIcon(myTileTexture.getTexture(BlockHelper.SIDE_RIGHT[myTileFacing.getFacing()], i, 0), posX() + 60, posY + 44);
			gui.drawIcon(myTileTexture.getTexture(BlockHelper.SIDE_BELOW[myTileFacing.getFacing()], i, 0), posX() + 40, posY + 64);
			gui.drawIcon(myTileTexture.getTexture(BlockHelper.SIDE_OPPOSITE[myTileFacing.getFacing()], i, 0), posX() + 60, posY + 64);
		}
		GlStateManager.disableBlend();
		RenderHelper.setDefaultFontTextureSheet();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	void handleSideChange(int side, int mouseButton) {

		if (GuiScreen.isShiftKeyDown()) {
			if (side == myTileFacing.getFacing()) {
				if (myTileSides.resetSides()) {
					GuiBase.playClickSound(1.0F, 0.2F);
				}
			} else if (myTileSides.setSide(side, 0)) {
				GuiBase.playClickSound(1.0F, 0.4F);
			}
			return;
		}
		if (mouseButton == 0) {
			if (myTileSides.incrSide(side)) {
				GuiBase.playClickSound(1.0F, 0.8F);
			}
		} else if (mouseButton == 1) {
			if (myTileSides.decrSide(side)) {
				GuiBase.playClickSound(1.0F, 0.6F);
			}
		}
	}

}
