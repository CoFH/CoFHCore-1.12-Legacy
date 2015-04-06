package cofh.core.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class PacketTile extends PacketCoFHBase {

	public static void initialize() {

		PacketHandler.instance.registerPacket(PacketTile.class);
	}

	public PacketTile() {

	}

	public PacketTile(TileEntity theTile) {

		addInt(theTile.xCoord);
		addInt(theTile.yCoord);
		addInt(theTile.zCoord);

	}

	@Override
	public void handlePacket(EntityPlayer player, boolean isServer) {

		TileEntity tile = player.worldObj.getTileEntity(getInt(), getInt(), getInt());

		if (tile instanceof ITilePacketHandler) {
			((ITilePacketHandler) tile).handleTilePacket(this, isServer);
			tile.getWorldObj().markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
			if (isServer) {
				tile.getWorldObj().func_147453_f(tile.xCoord, tile.yCoord, tile.zCoord, tile.getBlockType());
			}
		} else {
			// TODO: Throw error, bad packet
		}
	}

	public static PacketTile newPacket(TileEntity theTile) {

		return new PacketTile(theTile);
	}

}
