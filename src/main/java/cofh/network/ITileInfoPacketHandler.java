package cofh.network;

import io.netty.channel.ChannelHandlerContext;

public interface ITileInfoPacketHandler {

	public void handleTileInfoPacket(CoFHPacket payload, ChannelHandlerContext handler);

}
