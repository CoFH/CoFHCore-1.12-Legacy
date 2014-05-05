package cofh.gui.element;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.opengl.GL11;

import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.IReconfigurableSides;
import cofh.api.tileentity.ISidedBlockTexture;
import cofh.gui.GuiBase;
import cofh.render.RenderHelper;
import cofh.util.BlockHelper;
import cofh.util.StringHelper;

public class TabConfiguration extends TabBase {

	IReconfigurableFacing myTile;
	IReconfigurableSides myTileSides;
	ISidedBlockTexture myTileTexture;
	int headerColor = 0xe1c92f;
	int subheaderColor = 0xaaafb8;
	int textColor = 0x000000;

	public TabConfiguration(GuiBase gui, IReconfigurableFacing theTile) {

		super(gui);

		myTile = theTile;
		myTileSides = (IReconfigurableSides) theTile;
		myTileTexture = (ISidedBlockTexture) theTile;
		maxHeight = 92;
		maxWidth = 100;
		backgroundColor = 0x089e4c;
	}

	@Override
	public void draw() {

		drawBackground();
		drawTabIcon("IconConfigMachine");
		if (!isFullyOpened()) {
			return;
		}
		elementFontRenderer.drawStringWithShadow(StringHelper.localize("info.cofh.configuration"), posX + 20, posY + 6, headerColor);
		elementFontRenderer.drawString("", posX, posY, 0xffffff);
		RenderHelper.setBlockTextureSheet();

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		for (int i = 0; i < 2; i++) {
			gui.drawIcon(myTileTexture.getBlockTexture(BlockHelper.SIDE_ABOVE[myTile.getFacing()], i), posX + 40, posY + 24, 0);
			gui.drawIcon(myTileTexture.getBlockTexture(BlockHelper.SIDE_LEFT[myTile.getFacing()], i), posX + 20, posY + 44, 0);
			gui.drawIcon(myTileTexture.getBlockTexture(myTile.getFacing(), i), posX + 40, posY + 44, 0);
			gui.drawIcon(myTileTexture.getBlockTexture(BlockHelper.SIDE_RIGHT[myTile.getFacing()], i), posX + 60, posY + 44, 0);
			gui.drawIcon(myTileTexture.getBlockTexture(BlockHelper.SIDE_BELOW[myTile.getFacing()], i), posX + 40, posY + 64, 0);
			gui.drawIcon(myTileTexture.getBlockTexture(BlockHelper.SIDE_OPPOSITE[myTile.getFacing()], i), posX + 60, posY + 64, 0);
		}
		GL11.glDisable(GL11.GL_BLEND);
		RenderHelper.setDefaultFontTextureSheet();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(StringHelper.localize("info.cofh.configuration"));
			return;
		}
	}

	@Override
	public boolean handleMouseClicked(int x, int y, int mouseButton) {

		if (!isFullyOpened()) {
			return false;
		}
		x -= currentShiftX;
		y -= currentShiftY;
		if (x < 16 || x >= 80 || y < 20 || y >= 84) {
			return false;
		}
		if (40 <= x && x < 56 && 24 <= y && y < 40) {
			handleSideChange(BlockHelper.SIDE_ABOVE[myTile.getFacing()], mouseButton);
		} else if (20 <= x && x < 36 && 44 <= y && y < 60) {
			handleSideChange(BlockHelper.SIDE_LEFT[myTile.getFacing()], mouseButton);
		} else if (40 <= x && x < 56 && 44 <= y && y < 60) {
			handleSideChange(myTile.getFacing(), mouseButton);
		} else if (60 <= x && x < 76 && 44 <= y && y < 60) {
			handleSideChange(BlockHelper.SIDE_RIGHT[myTile.getFacing()], mouseButton);
		} else if (40 <= x && x < 56 && 64 <= y && y < 80) {
			handleSideChange(BlockHelper.SIDE_BELOW[myTile.getFacing()], mouseButton);
		} else if (60 <= x && x < 76 && 64 <= y && y < 80) {
			handleSideChange(BlockHelper.SIDE_OPPOSITE[myTile.getFacing()], mouseButton);
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
		GL11.glColor4f(colorR, colorG, colorB, 1.0F);
		gui.drawTexturedModalRect(posX + 16, posY + 20, 16, 20, 64, 64);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	void handleSideChange(int side, int mouseButton) {

		if (GuiScreen.isShiftKeyDown()) {
			if (side == myTile.getFacing()) {
				if (myTileSides.resetSides()) {
					playSound("random.click", 1.0F, 0.2F);
				}
			} else if (myTileSides.setSide(side, 0)) {
				playSound("random.click", 1.0F, 0.4F);
			}
			return;
		}
		if (mouseButton == 0) {
			if (myTileSides.incrSide(side)) {
				playSound("random.click", 1.0F, 0.8F);
			}
		} else if (mouseButton == 1) {
			if (myTileSides.decrSide(side)) {
				playSound("random.click", 1.0F, 0.6F);
			}
		}
	}

}
