package cofh.core.gui.client;

import cofh.core.gui.GuiCore;
import cofh.core.gui.container.ContainerAugments;
import cofh.core.init.CoreProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiAugments extends GuiCore {

	public static final ResourceLocation TEXTURE = new ResourceLocation(CoreProps.PATH_GUI + "augments.png");

	public GuiAugments(InventoryPlayer inventory) {

		super(new ContainerAugments(inventory), TEXTURE);

		// generateInfo("tab.thermalexpansion.device.lexicon", 3);

		ySize = 197;
	}

	@Override
	public void initGui() {

		super.initGui();

	}

}
