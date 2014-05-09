package cofh.gui.client;

import cofh.gui.GuiBaseAdv;
import cofh.gui.GuiProps;

import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GuiTPList extends GuiBaseAdv {

	static final String TEXTURE_PATH = GuiProps.PATH_GUI + "TPXList.png";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEXTURE_PATH);
	static final String INFO = "";

	public GuiTPList(Container container, ResourceLocation texture) {

		super(container, texture);
		name = "info.cofh.TPXList";
		drawInventory = false;

	}

	@Override
	public void initGui() {

		super.initGui();
	}

}
