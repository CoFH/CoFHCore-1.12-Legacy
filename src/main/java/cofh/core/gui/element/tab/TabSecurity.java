package cofh.core.gui.element.tab;

import cofh.api.core.ISecurable;
import cofh.core.gui.GuiContainerCore;
import cofh.core.init.CoreTextures;
import cofh.core.util.helpers.StringHelper;
import net.minecraft.client.renderer.GlStateManager;

import java.util.List;
import java.util.UUID;

public class TabSecurity extends TabBase {

	public static int defaultSide = 0;
	public static int defaultHeaderColor = 0xe1c92f;
	public static int defaultSubHeaderColor = 0xaaafb8;
	public static int defaultTextColor = 0x000000;
	public static int defaultBackgroundColor = 0x888888;

	// public static int defaultBackgroundColor = 0xe66a10;

	private ISecurable myContainer;
	private UUID myPlayer;

	public TabSecurity(GuiContainerCore gui, ISecurable container, UUID playerName) {

		this(gui, defaultSide, container, playerName);
	}

	public TabSecurity(GuiContainerCore gui, int side, ISecurable container, UUID playerName) {

		super(gui, side);

		headerColor = defaultHeaderColor;
		subheaderColor = defaultSubHeaderColor;
		textColor = defaultTextColor;
		backgroundColor = defaultBackgroundColor;

		maxHeight = 92;
		maxWidth = 112;
		myContainer = container;
		myPlayer = playerName;
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(StringHelper.localize("info.cofh.owner") + ": " + myContainer.getOwnerName());
			return;
		}
		int x = gui.getMouseX() - currentShiftX;
		int y = gui.getMouseY() - currentShiftY;
		if (28 <= x && x < 44 && 20 <= y && y < 36) {
			list.add(StringHelper.localize("info.cofh.accessPublic"));
		} else if (48 <= x && x < 64 && 20 <= y && y < 36) {
			list.add(StringHelper.localize("info.cofh.accessRestricted"));
		} else if (68 <= x && x < 84 && 20 <= y && y < 36) {
			list.add(StringHelper.localize("info.cofh.accessPrivate"));
		}
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {

		if (!myPlayer.equals(myContainer.getOwner().getId())) {
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

		if (mouseX < 24 || mouseX >= 88 || mouseY < 16 || mouseY >= 40) {
			return false;
		}
		if (28 <= mouseX && mouseX < 44 && 20 <= mouseY && mouseY < 36) {
			if (!myContainer.getAccess().isPublic()) {
				myContainer.setAccess(ISecurable.AccessMode.PUBLIC);
				GuiContainerCore.playClickSound(0.4F);
			}
		} else if (48 <= mouseX && mouseX < 64 && 20 <= mouseY && mouseY < 36) {
			if (!myContainer.getAccess().isFriendsOnly()) {
				myContainer.setAccess(ISecurable.AccessMode.FRIENDS);
				GuiContainerCore.playClickSound(0.6F);
			}
		} else if (68 <= mouseX && mouseX < 84 && 20 <= mouseY && mouseY < 36) {
			if (!myContainer.getAccess().isPrivate()) {
				myContainer.setAccess(ISecurable.AccessMode.PRIVATE);
				GuiContainerCore.playClickSound(0.8F);
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
		GlStateManager.color(colorR, colorG, colorB, 1.0F);
		gui.drawTexturedModalRect(posX() + 24, posY + 16, 16, 20, 64, 24);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	protected void drawForeground() {

		if (myContainer.getAccess().isPublic()) {
			drawTabIcon(CoreTextures.ICON_ACCESS_PUBLIC);
		} else if (myContainer.getAccess().isFriendsOnly()) {
			drawTabIcon(CoreTextures.ICON_ACCESS_FRIENDS);
		} else if (myContainer.getAccess().isPrivate()) {
			drawTabIcon(CoreTextures.ICON_ACCESS_PRIVATE);
		}
		if (!isFullyOpened()) {
			return;
		}
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.security"), posXOffset() + 18, posY + 6, headerColor);
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.access") + ":", posXOffset() + 6, posY + 42, subheaderColor);

		if (myContainer.getAccess().isPublic()) {
			gui.drawButton(CoreTextures.ICON_ACCESS_PUBLIC, posX() + 28, posY + 20, 1);
			gui.drawButton(CoreTextures.ICON_ACCESS_FRIENDS, posX() + 48, posY + 20, 0);
			gui.drawButton(CoreTextures.ICON_ACCESS_PRIVATE, posX() + 68, posY + 20, 0);
			getFontRenderer().drawString(StringHelper.localize("info.cofh.accessPublic"), posXOffset() + 14, posY + 54, textColor);
		} else if (myContainer.getAccess().isFriendsOnly()) {
			gui.drawButton(CoreTextures.ICON_ACCESS_PUBLIC, posX() + 28, posY + 20, 0);
			gui.drawButton(CoreTextures.ICON_ACCESS_FRIENDS, posX() + 48, posY + 20, 1);
			gui.drawButton(CoreTextures.ICON_ACCESS_PRIVATE, posX() + 68, posY + 20, 0);
			getFontRenderer().drawString(StringHelper.localize("info.cofh.accessRestricted"), posXOffset() + 14, posY + 54, textColor);
		} else if (myContainer.getAccess().isPrivate()) {
			gui.drawButton(CoreTextures.ICON_ACCESS_PUBLIC, posX() + 28, posY + 20, 0);
			gui.drawButton(CoreTextures.ICON_ACCESS_FRIENDS, posX() + 48, posY + 20, 0);
			gui.drawButton(CoreTextures.ICON_ACCESS_PRIVATE, posX() + 68, posY + 20, 1);
			getFontRenderer().drawString(StringHelper.localize("info.cofh.accessPrivate"), posXOffset() + 14, posY + 54, textColor);
		}
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void setFullyOpen() {

		if (!myPlayer.equals(myContainer.getOwner().getId())) {
			return;
		}
		super.setFullyOpen();
	}

}
