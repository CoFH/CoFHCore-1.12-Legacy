package cofh.hud;

import net.minecraft.entity.player.EntityPlayer;

public interface IKeyBinding {

	public boolean keyDown(String key, boolean tickEnd, boolean isRepeat);

	public boolean keyUp(String key, boolean tickEnd);

	public void keyDownServer(String key, boolean tickEnd, boolean isRepeat, EntityPlayer player);

	public void keyUpServer(String key, boolean tickEnd, EntityPlayer player);

	public String getUUID();

}
