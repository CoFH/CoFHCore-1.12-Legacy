package cofh.core.network;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Interface for handling extra packets with potential player interaction.
 *
 * @author King Lemming
 *
 */
public interface ITileInfoPacketHandler {

	public void handleTileInfoPacket(PacketCoFHBase payload, boolean isServer, EntityPlayer player);

}
