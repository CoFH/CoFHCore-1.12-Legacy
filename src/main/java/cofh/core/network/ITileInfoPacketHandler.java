package cofh.core.network;

import net.minecraft.entity.player.EntityPlayer;

public interface ITileInfoPacketHandler {

	void handleTileInfoPacket(PacketCoFHBase payload, boolean isServer, EntityPlayer thePlayer);

}
