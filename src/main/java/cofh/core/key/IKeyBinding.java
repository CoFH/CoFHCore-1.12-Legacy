package cofh.core.key;

import net.minecraft.entity.player.EntityPlayer;

public interface IKeyBinding {

	String getUUID();

	int getKey();

	boolean hasServerSide();

	boolean keyPressClient();

	boolean keyPressServer(EntityPlayer player);

}
