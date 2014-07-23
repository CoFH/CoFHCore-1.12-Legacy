package cofh.network;

public interface ITilePacketHandler {

	public void handleTilePacket(PacketCoFHBase payload, boolean isServer);

}
