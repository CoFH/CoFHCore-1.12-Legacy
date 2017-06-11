package cofh.core.network;

public interface ITilePacketHandler {

	void handleTilePacket(PacketCoFHBase payload, boolean isServer);

}
