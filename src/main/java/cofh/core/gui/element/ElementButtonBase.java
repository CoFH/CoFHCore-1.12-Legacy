package cofh.core.gui.element;

import cofh.core.gui.GuiCore;
import cofh.core.init.CoreProps;
import net.minecraft.util.ResourceLocation;

public abstract class ElementButtonBase extends ElementBase {

	public static final ResourceLocation HOVER = new ResourceLocation(CoreProps.PATH_ELEMENTS + "button_hover.png");
	public static final ResourceLocation ENABLED = new ResourceLocation(CoreProps.PATH_ELEMENTS + "button_enabled.png");
	public static final ResourceLocation DISABLED = new ResourceLocation(CoreProps.PATH_ELEMENTS + "button_disabled.png");

	public ElementButtonBase(GuiCore containerScreen, int posX, int posY, int sizeX, int sizeY) {

		super(containerScreen, posX, posY, sizeX, sizeY);
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {

		playSound(mouseButton);
		switch (mouseButton) {
			case 0:
				onClick();
				break;
			case 1:
				onRightClick();
				break;
			case 2:
				onMiddleClick();
				break;
		}
		return true;
	}

	protected void playSound(int button) {

		if (button == 0) {
			GuiCore.playClickSound(1.0F, 1.0F);
		}
	}

	public void onClick() {

	}

	public void onRightClick() {

	}

	public void onMiddleClick() {

	}
}
