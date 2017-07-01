package cofh.core.gui.element.tab;

import cofh.api.tileentity.IReconfigurableFacing;
import cofh.core.gui.GuiCore;
import cofh.core.init.CoreTextures;
import cofh.core.render.ISidedTexture;
import cofh.core.util.helpers.BlockHelper;
import cofh.core.util.helpers.RenderHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.api.tileentity.IReconfigurableSides;
import cofh.api.tileentity.ITransferControl;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;

public class TabConfigurationTransfer extends TabBase {

	public static int defaultSide = 1;
	public static int defaultHeaderColor = 0xe1c92f;
	public static int defaultSubHeaderColor = 0xaaafb8;
	public static int defaultTextColor = 0x000000;
	public static int defaultBackgroundColor = 0x226688;

	private IReconfigurableFacing myTileFacing;
	private IReconfigurableSides myTileSides;
	private ISidedTexture myTileTexture;
	private ITransferControl myTileControl;

	public TabConfigurationTransfer(GuiCore gui, IReconfigurableSides theTile) {

		this(gui, defaultSide, theTile);
	}

	public TabConfigurationTransfer(GuiCore gui, int side, IReconfigurableSides theTile) {

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
		myTileControl = (ITransferControl) theTile;
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(StringHelper.localize("info.cofh.configuration"));
			return;
		}
		int x = gui.getMouseX() - currentShiftX;
		int y = gui.getMouseY() - currentShiftY;

		if (8 <= x && x < 24 && 34 <= y && y < 50) {
			if (myTileControl.hasTransferIn()) {
				list.add(myTileControl.getTransferIn() ? StringHelper.localize("gui.cofh.transferInEnabled") : StringHelper.localize("gui.cofh.transferInDisabled"));
			} else {
				list.add(StringHelper.localize("gui.cofh.transferInUnavailable"));
			}
		} else if (8 <= x && x < 24 && 54 <= y && y < 68) {
			if (myTileControl.hasTransferOut()) {
				list.add(myTileControl.getTransferOut() ? StringHelper.localize("gui.cofh.transferOutEnabled") : StringHelper.localize("gui.cofh.transferOutDisabled"));
			} else {
				list.add(StringHelper.localize("gui.cofh.transferOutUnavailable"));
			}
		}
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) throws IOException {

		if (!isFullyOpened()) {
			return false;
		}
		if (side == LEFT) {
			mouseX += currentWidth;
		}
		mouseX -= currentShiftX;
		mouseY -= currentShiftY;

		if (mouseX < 8 || mouseX >= 92 || mouseY < 20 || mouseY >= 84) {
			return super.onMousePressed(mouseX, mouseY, mouseButton);
		}
		if (8 <= mouseX && mouseX < 24 && 34 <= mouseY && mouseY < 50) {
			handleTransferChange(0, mouseButton);
		} else if (8 <= mouseX && mouseX < 24 && 54 <= mouseY && mouseY < 68) {
			handleTransferChange(1, mouseButton);
		} else if (52 <= mouseX && mouseX < 68 && 24 <= mouseY && mouseY < 40) {
			handleSideChange(BlockHelper.SIDE_ABOVE[myTileFacing.getFacing()], mouseButton);
		} else if (32 <= mouseX && mouseX < 48 && 44 <= mouseY && mouseY < 60) {
			handleSideChange(BlockHelper.SIDE_LEFT[myTileFacing.getFacing()], mouseButton);
		} else if (52 <= mouseX && mouseX < 68 && 44 <= mouseY && mouseY < 60) {
			handleSideChange(myTileFacing.getFacing(), mouseButton);
		} else if (72 <= mouseX && mouseX < 88 && 44 <= mouseY && mouseY < 60) {
			handleSideChange(BlockHelper.SIDE_RIGHT[myTileFacing.getFacing()], mouseButton);
		} else if (52 <= mouseX && mouseX < 68 && 64 <= mouseY && mouseY < 80) {
			handleSideChange(BlockHelper.SIDE_BELOW[myTileFacing.getFacing()], mouseButton);
		} else if (72 <= mouseX && mouseX < 88 && 64 <= mouseY && mouseY < 80) {
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
		gui.drawTexturedModalRect(posX() + 28, posY + 20, 16, 20, 64, 64);
		gui.drawTexturedModalRect(posX() + 6, posY + 32, 16, 20, 20, 40);
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

		if (myTileControl.hasTransferIn()) {
			gui.drawButton(CoreTextures.ICON_INPUT, posX() + 8, posY + 34, myTileControl.getTransferIn() ? 1 : 0);
		} else {
			gui.drawButton(CoreTextures.ICON_INPUT, posX() + 8, posY + 34, 2);
		}
		if (myTileControl.hasTransferOut()) {
			gui.drawButton(CoreTextures.ICON_OUTPUT, posX() + 8, posY + 54, myTileControl.getTransferOut() ? 1 : 0);
		} else {
			gui.drawButton(CoreTextures.ICON_OUTPUT, posX() + 8, posY + 54, 2);
		}
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		for (int pass = 0; pass < myTileTexture.getNumPasses(); pass++) {
			gui.drawIcon(myTileTexture.getTexture(BlockHelper.SIDE_ABOVE[myTileFacing.getFacing()], pass), posX() + 52, posY + 24);
			gui.drawIcon(myTileTexture.getTexture(BlockHelper.SIDE_LEFT[myTileFacing.getFacing()], pass), posX() + 32, posY + 44);
			gui.drawIcon(myTileTexture.getTexture(myTileFacing.getFacing(), pass), posX() + 52, posY + 44);
			gui.drawIcon(myTileTexture.getTexture(BlockHelper.SIDE_RIGHT[myTileFacing.getFacing()], pass), posX() + 72, posY + 44);
			gui.drawIcon(myTileTexture.getTexture(BlockHelper.SIDE_BELOW[myTileFacing.getFacing()], pass), posX() + 52, posY + 64);
			gui.drawIcon(myTileTexture.getTexture(BlockHelper.SIDE_OPPOSITE[myTileFacing.getFacing()], pass), posX() + 72, posY + 64);
		}
		GlStateManager.disableBlend();
		RenderHelper.setDefaultFontTextureSheet();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	void handleTransferChange(int direction, int mouseButton) {

		if (direction == 0) {
			if (myTileControl.setTransferIn(!myTileControl.getTransferIn())) {
				GuiCore.playClickSound(1.0F, myTileControl.getTransferIn() ? 0.8F : 0.4F);
			}
		} else {
			if (myTileControl.setTransferOut(!myTileControl.getTransferOut())) {
				GuiCore.playClickSound(1.0F, myTileControl.getTransferOut() ? 0.8F : 0.4F);
			}
		}
	}

	void handleSideChange(int side, int mouseButton) {

		if (GuiScreen.isShiftKeyDown()) {
			if (side == myTileFacing.getFacing()) {
				if (myTileSides.resetSides()) {
					GuiCore.playClickSound(1.0F, 0.2F);
				}
			} else if (myTileSides.setSide(side, 0)) {
				GuiCore.playClickSound(1.0F, 0.4F);
			}
			return;
		}
		if (mouseButton == 0) {
			if (myTileSides.incrSide(side)) {
				GuiCore.playClickSound(1.0F, 0.8F);
			}
		} else if (mouseButton == 1) {
			if (myTileSides.decrSide(side)) {
				GuiCore.playClickSound(1.0F, 0.6F);
			}
		}
	}

}
