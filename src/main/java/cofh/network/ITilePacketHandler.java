package cofh.network;


public interface ITilePacketHandler {

	public void handleTilePacket(CoFHPacket payload, boolean isServer);

}
