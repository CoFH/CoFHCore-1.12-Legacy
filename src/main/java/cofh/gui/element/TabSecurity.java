package cofh.gui.element;

import cofh.api.tileentity.ISecureTile;
import cofh.gui.GuiBase;
import cofh.util.StringHelper;

import java.util.List;

import org.lwjgl.opengl.GL11;

public class TabSecurity extends TabBase {

	public static final String[] TOOLTIPS = { StringHelper.localize("info.cofh.accessPublic"), StringHelper.localize("info.cofh.accessRestricted"),
			StringHelper.localize("info.cofh.accessPrivate") };

	ISecureTile myTile;
	String myPlayer;

	public TabSecurity(GuiBase gui, ISecureTile theTile, String playerName) {

		super(gui);

		myPlayer = playerName;
		myTile = theTile;
		maxHeight = 68;
		maxWidth = 112;
		backgroundColor = 0x4c99b2;
	}

	@Override
	public void draw() {

		if (!visible) {
			return;
		}
		drawBackground();

		if (myTile.getAccess().isPublic()) {
			drawTabIcon("IconAccessPublic");
		} else if (myTile.getAccess().isRestricted()) {
			drawTabIcon("IconAccessFriends");
		} else if (myTile.getAccess().isPrivate()) {
			drawTabIcon("IconAccessPrivate");
		}
		if (!isFullyOpened()) {
			return;
		}
		GuiBase.guiFontRenderer.drawStringWithShadow(StringHelper.localize("info.cofh.security"), posX + 20, posY + 6, headerColor);
		GuiBase.guiFontRenderer.drawStringWithShadow(StringHelper.localize("info.cofh.accessMode") + ":", posX + 8, posY + 42, subheaderColor);

		if (myTile.getAccess().isPublic()) {
			gui.drawButton("IconAccessPublic", posX + 28, posY + 20, 1, 1);
			gui.drawButton("IconAccessFriends", posX + 48, posY + 20, 1, 0);
			gui.drawButton("IconAccessPrivate", posX + 68, posY + 20, 1, 0);
			GuiBase.guiFontRenderer.drawString(TOOLTIPS[0], posX + 16, posY + 54, textColor);
		} else if (myTile.getAccess().isRestricted()) {
			gui.drawButton("IconAccessPublic", posX + 28, posY + 20, 1, 0);
			gui.drawButton("IconAccessFriends", posX + 48, posY + 20, 1, 1);
			gui.drawButton("IconAccessPrivate", posX + 68, posY + 20, 1, 0);
			GuiBase.guiFontRenderer.drawString(TOOLTIPS[1], posX + 16, posY + 54, textColor);
		} else if (myTile.getAccess().isPrivate()) {
			gui.drawButton("IconAccessPublic", posX + 28, posY + 20, 1, 0);
			gui.drawButton("IconAccessFriends", posX + 48, posY + 20, 1, 0);
			gui.drawButton("IconAccessPrivate", posX + 68, posY + 20, 1, 1);
			GuiBase.guiFontRenderer.drawString(TOOLTIPS[2], posX + 16, posY + 54, textColor);
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(StringHelper.localize("info.cofh.owner") + ": " + myTile.getOwnerName());
			return;
		}
		int x = gui.getMouseX() - currentShiftX;
		int y = gui.getMouseY() - currentShiftY;
		if (28 <= x && x < 44 && 20 <= y && y < 36) {
			list.add(TOOLTIPS[0]);
		} else if (48 <= x && x < 64 && 20 <= y && y < 36) {
			list.add(TOOLTIPS[1]);
		} else if (68 <= x && x < 84 && 20 <= y && y < 36) {
			list.add(TOOLTIPS[2]);
		}
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {

		if (!myPlayer.equals(myTile.getOwnerName())) {
			return true;
		}
		if (!isFullyOpened()) {
			return false;
		}
		mouseX -= currentShiftX;
		mouseY -= currentShiftY;

		if (mouseX < 24 || mouseX >= 88 || mouseY < 16 || mouseY >= 40) {
			return false;
		}
		if (28 <= mouseX && mouseX < 44 && 20 <= mouseY && mouseY < 36) {
			if (!myTile.getAccess().isPublic()) {
				myTile.setAccess(ISecureTile.AccessMode.PUBLIC);
				GuiBase.playSound("random.click", 1.0F, 0.4F);
			}
		} else if (48 <= mouseX && mouseX < 64 && 20 <= mouseY && mouseY < 36) {
			if (!myTile.getAccess().isRestricted()) {
				myTile.setAccess(ISecureTile.AccessMode.RESTRICTED);
				GuiBase.playSound("random.click", 1.0F, 0.6F);
			}
		} else if (68 <= mouseX && mouseX < 84 && 20 <= mouseY && mouseY < 36) {
			if (!myTile.getAccess().isPrivate()) {
				myTile.setAccess(ISecureTile.AccessMode.PRIVATE);
				GuiBase.playSound("random.click", 1.0F, 0.8F);
			}
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
		gui.drawTexturedModalRect(posX + 24, posY + 16, 16, 20, 64, 24);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void setFullyOpen() {

		if (!myPlayer.equals(myTile.getOwnerName())) {
			return;
		}
		super.setFullyOpen();
	}

}
