package cofh.network;

import net.minecraft.entity.player.EntityPlayer;

public interface ITileInfoPacketHandler {

	public void handleTileInfoPacket(CoFHPacket payload, boolean isServer, EntityPlayer thePlayer);

}
