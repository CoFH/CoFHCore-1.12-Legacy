package cofh.core.gui;

import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class GuiConfigCoreFactory implements IModGuiFactory {

	/* IModGuiFactory */
	@Override
	public void initialize(Minecraft minecraftInstance) {

	}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {

		return GuiConfigCore.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {

		return null;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {

		return null;
	}

}
