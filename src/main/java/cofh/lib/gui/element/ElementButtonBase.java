package cofh.lib.gui.element;

import cofh.lib.gui.GuiBase;
import cofh.lib.gui.GuiProps;

import net.minecraft.util.ResourceLocation;

public abstract class ElementButtonBase extends ElementBase {

	public static final ResourceLocation HOVER = new ResourceLocation(GuiProps.PATH_ELEMENTS + "Button_Hover.png");
	public static final ResourceLocation ENABLED = new ResourceLocation(GuiProps.PATH_ELEMENTS + "Button_Enabled.png");
	public static final ResourceLocation DISABLED = new ResourceLocation(GuiProps.PATH_ELEMENTS + "Button_Disabled.png");

	public ElementButtonBase(GuiBase containerScreen, int posX, int posY, int sizeX, int sizeY) {

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
			GuiBase.playSound("random.click", 1.0F, 1.0F);
		}
	}

	public void onClick() {

	}

	public void onRightClick() {

	}

	public void onMiddleClick() {

	}
}
