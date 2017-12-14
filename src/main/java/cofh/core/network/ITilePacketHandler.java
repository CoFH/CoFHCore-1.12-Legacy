package cofh.core.network;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ITilePacketHandler {

	@SideOnly (Side.CLIENT)
	void handleTilePacket(PacketBase payload);

}
