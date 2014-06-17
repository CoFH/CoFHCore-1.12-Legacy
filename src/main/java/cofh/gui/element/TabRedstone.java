package cofh.gui.element;

import cofh.api.tileentity.IRedstoneControl;
import cofh.gui.GuiBase;
import cofh.util.StringHelper;

import java.util.List;

import org.lwjgl.opengl.GL11;

public class TabRedstone extends TabBase {

	public static int defaultSide = 1;
	IRedstoneControl myContainer;

	public TabRedstone(GuiBase gui, IRedstoneControl container) {

		this(gui, defaultSide, container);
	}

	public TabRedstone(GuiBase gui, int side, IRedstoneControl container) {

		super(gui, side);

		myContainer = container;
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
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.redstoneControl"), posXOffset() + 18, posY + 6, headerColor);
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.controlStatus") + ":", posXOffset() + 6, posY + 42, subheaderColor);
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.signalRequired") + ":", posXOffset() + 6, posY + 66, subheaderColor);

		if (myContainer.getControl().isDisabled()) {
			gui.drawButton("IconGunpowder", posX() + 28, posY + 20, 1, 1);
			gui.drawButton("IconRSTorchOff", posX() + 48, posY + 20, 1, 0);
			gui.drawButton("IconRSTorchOn", posX() + 68, posY + 20, 1, 0);
			getFontRenderer().drawString(StringHelper.localize("info.cofh.disabled"), posXOffset() + 14, posY + 54, textColor);
			getFontRenderer().drawString(StringHelper.localize("info.cofh.ignored"), posXOffset() + 14, posY + 78, textColor);
		} else {
			getFontRenderer().drawString(StringHelper.localize("info.cofh.enabled"), posXOffset() + 14, posY + 54, textColor);

			if (myContainer.getControl().isLow()) {
				gui.drawButton("IconRedstone", posX() + 28, posY + 20, 1, 0);
				gui.drawButton("IconRSTorchOff", posX() + 48, posY + 20, 1, 1);
				gui.drawButton("IconRSTorchOn", posX() + 68, posY + 20, 1, 0);
				getFontRenderer().drawString(StringHelper.localize("info.cofh.low"), posXOffset() + 14, posY + 78, textColor);
			} else {
				gui.drawButton("IconRedstone", posX() + 28, posY + 20, 1, 0);
				gui.drawButton("IconRSTorchOff", posX() + 48, posY + 20, 1, 0);
				gui.drawButton("IconRSTorchOn", posX() + 68, posY + 20, 1, 1);
				getFontRenderer().drawString(StringHelper.localize("info.cofh.high"), posXOffset() + 14, posY + 78, textColor);
			}
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			if (myContainer.getControl().isDisabled()) {
				list.add(StringHelper.localize("info.cofh.disabled"));
				return;
			} else if (myContainer.getControl().isLow()) {
				list.add(StringHelper.localize("info.cofh.enabled") + ", " + StringHelper.localize("info.cofh.low"));
				return;
			}
			list.add(StringHelper.localize("info.cofh.enabled") + ", " + StringHelper.localize("info.cofh.high"));
			return;
		}
		int x = gui.getMouseX() - currentShiftX;
		int y = gui.getMouseY() - currentShiftY;
		if (28 <= x && x < 44 && 20 <= y && y < 36) {
			list.add(StringHelper.localize("info.cofh.ignored"));
		} else if (48 <= x && x < 64 && 20 <= y && y < 36) {
			list.add(StringHelper.localize("info.cofh.low"));
		} else if (68 <= x && x < 84 && 20 <= y && y < 36) {
			list.add(StringHelper.localize("info.cofh.high"));
		}
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {

		if (!isFullyOpened()) {
			return false;
		}
		mouseX -= currentShiftX;
		mouseY -= currentShiftY;

		if (mouseX < 24 || mouseX >= 88 || mouseY < 16 || mouseY >= 40) {
			return false;
		}
		if (28 <= mouseX && mouseX < 44 && 20 <= mouseY && mouseY < 36) {
			if (!myContainer.getControl().isDisabled()) {
				myContainer.setControl(IRedstoneControl.ControlMode.DISABLED);
				GuiBase.playSound("random.click", 1.0F, 0.4F);
			}
		} else if (48 <= mouseX && mouseX < 64 && 20 <= mouseY && mouseY < 36) {
			if (!myContainer.getControl().isLow()) {
				myContainer.setControl(IRedstoneControl.ControlMode.LOW);
				GuiBase.playSound("random.click", 1.0F, 0.6F);
			}
		} else if (68 <= mouseX && mouseX < 84 && 20 <= mouseY && mouseY < 36) {
			if (!myContainer.getControl().isHigh()) {
				myContainer.setControl(IRedstoneControl.ControlMode.HIGH);
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
		gui.drawTexturedModalRect(posX() + 24, posY + 16, 16, 20, 64, 24);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

}
