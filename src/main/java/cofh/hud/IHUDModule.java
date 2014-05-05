package cofh.hud;

import net.minecraft.client.Minecraft;

public interface IHUDModule {

	public void renderHUD(Minecraft mc, int scaledHeight, int scaledWidth);

	public void setModuleID(int i);

	public void clientTick(Minecraft mc);

}
