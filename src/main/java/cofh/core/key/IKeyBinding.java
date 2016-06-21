package cofh.core.key;

import net.minecraft.entity.player.EntityPlayer;

public interface IKeyBinding {

	public boolean keyPressClient();

	public void keyPressServer(EntityPlayer player);

	public String getUUID();

	public int getKey();

	public boolean hasServerSide();

}
