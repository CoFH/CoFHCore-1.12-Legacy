package cofh.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class CoFHTileInfoPacket extends CoFHPacket {

	public static void initialize() {

		PacketHandler.instance.registerPacket(CoFHTileInfoPacket.class);
	}

	public CoFHTileInfoPacket() {

	}

	public CoFHTileInfoPacket(TileEntity theTile) {

		addInt(theTile.xCoord);
		addInt(theTile.yCoord);
		addInt(theTile.zCoord);

	}

	@Override
	public void handleClientSide(EntityPlayer player) {

		handlePacket(player, false);
	}

	@Override
	public void handleServerSide(EntityPlayer player) {

		handlePacket(player, true);
	}

	@Override
	public void handlePacket(EntityPlayer player, boolean isServer) {

		TileEntity tile = player.worldObj.getTileEntity(getInt(), getInt(), getInt());

		if (tile instanceof ITileInfoPacketHandler) {
			((ITileInfoPacketHandler) tile).handleTileInfoPacket(this, isServer, player);
		} else {
			// TODO: Throw error, bad packet
		}
	}

	public static CoFHTileInfoPacket newPacket(TileEntity theTile) {

		return new CoFHTileInfoPacket(theTile);
	}
}
