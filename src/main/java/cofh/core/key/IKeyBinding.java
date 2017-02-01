package cofh.core.key;

import net.minecraft.entity.player.EntityPlayer;

public interface IKeyBinding {

	public String getUUID();

	public int getKey();

	public boolean hasServerSide();

	public boolean keyPressClient();

	public boolean keyPressServer(EntityPlayer player);

}
