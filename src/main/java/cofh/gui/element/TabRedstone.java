package cofh.gui.element;

import cofh.api.tileentity.IRedstoneControl;
import cofh.gui.GuiBase;
import cofh.util.StringHelper;

import java.util.List;

import org.lwjgl.opengl.GL11;

public class TabRedstone extends TabBase {

	public static final String[] TOOLTIPS = { StringHelper.localize("info.cofh.redstoneStateIgnored"), StringHelper.localize("info.cofh.redstoneStateLow"),
			StringHelper.localize("info.cofh.redstoneStateHigh") };

	IRedstoneControl myTile;

	public TabRedstone(GuiBase gui, IRedstoneControl theTile) {

		super(gui);

		myTile = theTile;
		maxHeight = 92;
		maxWidth = 112;
		backgroundColor = 0xd0230a;
	}

	@Override
	public void draw() {

		drawBackground();
		drawTabIcon("IconRedstone");
		if (!isFullyOpened()) {
			return;
		}
		GuiBase.guiFontRenderer.drawStringWithShadow(StringHelper.localize("info.cofh.redstoneControl"), posX + 20, posY + 6, headerColor);
		GuiBase.guiFontRenderer.drawStringWithShadow(StringHelper.localize("info.cofh.controlStatus") + ":", posX + 8, posY + 42, subheaderColor);
		GuiBase.guiFontRenderer.drawStringWithShadow(StringHelper.localize("info.cofh.signalRequired") + ":", posX + 8, posY + 66, subheaderColor);

		if (myTile.getControl().isDisabled()) {
			gui.drawButton("IconGunpowder", posX + 28, posY + 20, 1, 1);
			gui.drawButton("IconRSTorchOff", posX + 48, posY + 20, 1, 0);
			gui.drawButton("IconRSTorchOn", posX + 68, posY + 20, 1, 0);
			GuiBase.guiFontRenderer.drawString(StringHelper.localize("info.cofh.redstoneControlOff"), posX + 16, posY + 54, textColor);
			GuiBase.guiFontRenderer.drawString(TOOLTIPS[0], posX + 16, posY + 78, textColor);
		} else {
			GuiBase.guiFontRenderer.drawString(StringHelper.localize("info.cofh.redstoneControlOn"), posX + 16, posY + 54, textColor);

			if (myTile.getControl().isLow()) {
				gui.drawButton("IconRedstone", posX + 28, posY + 20, 1, 0);
				gui.drawButton("IconRSTorchOff", posX + 48, posY + 20, 1, 1);
				gui.drawButton("IconRSTorchOn", posX + 68, posY + 20, 1, 0);
				GuiBase.guiFontRenderer.drawString(TOOLTIPS[1], posX + 16, posY + 78, textColor);
			} else {
				gui.drawButton("IconRedstone", posX + 28, posY + 20, 1, 0);
				gui.drawButton("IconRSTorchOff", posX + 48, posY + 20, 1, 0);
				gui.drawButton("IconRSTorchOn", posX + 68, posY + 20, 1, 1);
				GuiBase.guiFontRenderer.drawString(TOOLTIPS[2], posX + 16, posY + 78, textColor);
			}
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			if (myTile.getControl().isDisabled()) {
				list.add(StringHelper.localize("info.cofh.redstoneControlOff"));
				return;
			} else if (myTile.getControl().isLow()) {
				list.add(StringHelper.localize("info.cofh.redstoneControlOn") + ", " + StringHelper.localize("info.cofh.redstoneStateLow"));
				return;
			}
			list.add(StringHelper.localize("info.cofh.redstoneControlOn") + ", " + StringHelper.localize("info.cofh.redstoneStateHigh"));
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
	public boolean onMousePressed(int x, int y, int mouseButton) {

		if (!isFullyOpened()) {
			return false;
		}
		x -= currentShiftX;
		y -= currentShiftY;

		if (x < 24 || x >= 88 || y < 16 || y >= 40) {
			return false;
		}
		if (28 <= x && x < 44 && 20 <= y && y < 36) {
			if (!myTile.getControl().isDisabled()) {
				myTile.setControl(IRedstoneControl.ControlMode.DISABLED);
				GuiBase.playSound("random.click", 1.0F, 0.4F);
			}
		} else if (48 <= x && x < 64 && 20 <= y && y < 36) {
			if (!myTile.getControl().isLow()) {
				myTile.setControl(IRedstoneControl.ControlMode.LOW);
				GuiBase.playSound("random.click", 1.0F, 0.6F);
			}
		} else if (68 <= x && x < 84 && 20 <= y && y < 36) {
			if (!myTile.getControl().isHigh()) {
				myTile.setControl(IRedstoneControl.ControlMode.HIGH);
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

}
