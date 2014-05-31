package cofh.gui.element;

import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.IReconfigurableSides;
import cofh.api.tileentity.ISidedBlockTexture;
import cofh.gui.GuiBase;
import cofh.render.RenderHelper;
import cofh.util.BlockHelper;
import cofh.util.StringHelper;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.opengl.GL11;

public class TabConfiguration extends TabBase {

	IReconfigurableFacing myTile;
	IReconfigurableSides myTileSides;
	ISidedBlockTexture myTileTexture;

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
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.configuration"), posX + 20, posY + 6, headerColor);
		getFontRenderer().drawString("", posX, posY, 0xffffff);
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
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {

		if (!isFullyOpened()) {
			return false;
		}
		mouseX -= currentShiftX;
		mouseY -= currentShiftY;
		if (mouseX < 16 || mouseX >= 80 || mouseY < 20 || mouseY >= 84) {
			return false;
		}
		if (40 <= mouseX && mouseX < 56 && 24 <= mouseY && mouseY < 40) {
			handleSideChange(BlockHelper.SIDE_ABOVE[myTile.getFacing()], mouseButton);
		} else if (20 <= mouseX && mouseX < 36 && 44 <= mouseY && mouseY < 60) {
			handleSideChange(BlockHelper.SIDE_LEFT[myTile.getFacing()], mouseButton);
		} else if (40 <= mouseX && mouseX < 56 && 44 <= mouseY && mouseY < 60) {
			handleSideChange(myTile.getFacing(), mouseButton);
		} else if (60 <= mouseX && mouseX < 76 && 44 <= mouseY && mouseY < 60) {
			handleSideChange(BlockHelper.SIDE_RIGHT[myTile.getFacing()], mouseButton);
		} else if (40 <= mouseX && mouseX < 56 && 64 <= mouseY && mouseY < 80) {
			handleSideChange(BlockHelper.SIDE_BELOW[myTile.getFacing()], mouseButton);
		} else if (60 <= mouseX && mouseX < 76 && 64 <= mouseY && mouseY < 80) {
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
					GuiBase.playSound("random.click", 1.0F, 0.2F);
				}
			} else if (myTileSides.setSide(side, 0)) {
				GuiBase.playSound("random.click", 1.0F, 0.4F);
			}
			return;
		}
		if (mouseButton == 0) {
			if (myTileSides.incrSide(side)) {
				GuiBase.playSound("random.click", 1.0F, 0.8F);
			}
		} else if (mouseButton == 1) {
			if (myTileSides.decrSide(side)) {
				GuiBase.playSound("random.click", 1.0F, 0.6F);
			}
		}
	}

}
