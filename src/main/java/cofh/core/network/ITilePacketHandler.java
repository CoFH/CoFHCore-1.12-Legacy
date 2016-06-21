package cofh.core.network;

/**
 * Basic interface for handling update packets.
 *
 * @author King Lemming
 *
 */
public interface ITilePacketHandler {

	public void handleTilePacket(PacketCoFHBase payload, boolean isServer);

}
