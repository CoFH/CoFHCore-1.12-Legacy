package cofh.core.gui.client;

import cofh.core.gui.GuiBaseAdv;
import cofh.core.gui.container.ContainerAugments;
import cofh.lib.gui.GuiProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiAugments extends GuiBaseAdv {

    public static final ResourceLocation TEXTURE = new ResourceLocation(GuiProps.PATH_GUI + "Augments.png");

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
