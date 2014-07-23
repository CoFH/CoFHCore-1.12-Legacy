package cofh.network;

import net.minecraft.entity.player.EntityPlayer;

public interface ITileInfoPacketHandler {

	public void handleTileInfoPacket(PacketCoFHBase payload, boolean isServer, EntityPlayer thePlayer);

}
