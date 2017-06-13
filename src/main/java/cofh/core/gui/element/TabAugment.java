package cofh.core.gui.element;

import cofh.core.init.CoreTextures;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.GuiProps;
import cofh.lib.gui.container.IAugmentableContainer;
import cofh.lib.gui.element.TabBase;
import cofh.lib.util.helpers.RenderHelper;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class TabAugment extends TabBase {

	public static int defaultSide = 1;
	public static int defaultHeaderColor = 0xe1c92f;
	public static int defaultSubHeaderColor = 0xaaafb8;
	public static int defaultTextColor = 0x000000;
	public static int defaultBackgroundColor = 0x089e4c;

	public static ResourceLocation GRID_TEXTURE = new ResourceLocation(GuiProps.PATH_ELEMENTS + "slot_grid_augment.png");

	IAugmentableContainer myContainer;

	private int numAugments = 0;
	private int slotsBorderX1 = 18;
	private int slotsBorderX2 = slotsBorderX1 + 60;
	private int slotsBorderY1 = 20;
	private int slotsBorderY2 = slotsBorderY1 + 42;

	public TabAugment(GuiBase gui, IAugmentableContainer container) {

		this(gui, defaultSide, container);
	}

	public TabAugment(GuiBase gui, int side, IAugmentableContainer container) {

		super(gui, side);

		headerColor = defaultHeaderColor;
		subheaderColor = defaultSubHeaderColor;
		textColor = defaultTextColor;
		backgroundColor = defaultBackgroundColor;

		maxHeight = 92;
		maxWidth = 100;
		myContainer = container;

		numAugments = myContainer.getAugmentSlots().length;

		if (numAugments > 0) {
			for (int i = 0; i < numAugments; i++) {
				myContainer.getAugmentSlots()[i].xPos = -gui.getGuiLeft() - 16;
				myContainer.getAugmentSlots()[i].yPos = -gui.getGuiTop() - 16;
			}
			switch (numAugments) {
				case 4:
					slotsBorderX1 += 9;
				case 5:
				case 6:
					break;
				default:
					slotsBorderX1 += 9 * (3 - numAugments);
					slotsBorderX2 = slotsBorderX1 + 18 * numAugments + 6;
					slotsBorderY1 += 9;
					slotsBorderY2 -= 9;
			}
		}
		myContainer.setAugmentLock(true);
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(StringHelper.localize("info.cofh.augmentation"));
			if (numAugments == 0) {
				list.add(StringHelper.YELLOW + StringHelper.localize("info.cofh.upgradeRequired"));
			}
		}
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {

		if (numAugments == 0) {
			return true;
		}
		if (!isFullyOpened()) {
			return false;
		}
		if (side == LEFT) {
			mouseX += currentWidth;
		}
		mouseX -= currentShiftX;
		mouseY -= currentShiftY;

		if (mouseX < slotsBorderX1 + sideOffset() || mouseX >= slotsBorderX2 + sideOffset() || mouseY < slotsBorderY1 || mouseY >= slotsBorderY2) {
			return false;
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

		if (numAugments > 0) {
			if (numAugments > 3) {
				gui.drawTexturedModalRect(posXOffset() + slotsBorderX1, posY + slotsBorderY1, 16, 20, (numAugments > 4 ? 18 * 3 : 18 * 2) + 6, 24 + 18);
			} else {
				gui.drawTexturedModalRect(posXOffset() + slotsBorderX1, posY + slotsBorderY1, 16, 20, 18 * numAugments + 6, 24);
			}
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			RenderHelper.bindTexture(GRID_TEXTURE);

			switch (numAugments) {
				case 4:
					drawSlots(0, 0, 2);
					drawSlots(0, 1, 2);
					break;
				case 5:
					drawSlots(0, 0, 3);
					drawSlots(1, 1, 2);
					break;
				case 6:
					drawSlots(0, 0, 3);
					drawSlots(0, 1, 3);
					break;
				default:
					drawSlots(0, 0, numAugments);
			}
		}
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	protected void drawForeground() {

		drawTabIcon(CoreTextures.ICON_AUGMENT);
		if (!isFullyOpened()) {
			return;
		}
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.augmentation"), posXOffset() + 18, posY + 6, headerColor);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void setFullyOpen() {

		if (numAugments == 0) {
			return;
		}
		super.setFullyOpen();

		switch (numAugments) {
			case 4:
				for (int i = 0; i < numAugments; i++) {
					myContainer.getAugmentSlots()[i].xPos = posXOffset() + slotsBorderX1 + 4 + 18 * (i % 2);
					myContainer.getAugmentSlots()[i].yPos = posY + slotsBorderY1 + 4 + 18 * (i / 2);
				}
				break;
			case 5:
				for (int i = 0; i < numAugments; i++) {
					myContainer.getAugmentSlots()[i].xPos = posXOffset() + slotsBorderX1 + 4 + 18 * (i % 3) + 9 * (i / 3);
					myContainer.getAugmentSlots()[i].yPos = posY + slotsBorderY1 + 4 + 18 * (i / 3);
				}
				break;
			default:
				for (int i = 0; i < numAugments; i++) {
					myContainer.getAugmentSlots()[i].xPos = posXOffset() + slotsBorderX1 + 4 + 18 * (i % 3);
					myContainer.getAugmentSlots()[i].yPos = posY + slotsBorderY1 + 4 + 18 * (i / 3);
				}
		}
		myContainer.setAugmentLock(false);
	}

	@Override
	public void toggleOpen() {

		if (open) {
			for (int i = 0; i < numAugments; i++) {
				myContainer.getAugmentSlots()[i].xPos = -gui.getGuiLeft() - 16;
				myContainer.getAugmentSlots()[i].yPos = -gui.getGuiTop() - 16;
			}
			myContainer.setAugmentLock(true);
		}
		super.toggleOpen();
	}

	private void drawSlots(int xOffset, int yOffset, int slots) {

		gui.drawSizedTexturedModalRect(posXOffset() + slotsBorderX1 + 3 + 9 * xOffset, posY + slotsBorderY1 + 3 + 18 * yOffset, 0, 0, 18 * slots, 18, 96, 32);
	}

}
