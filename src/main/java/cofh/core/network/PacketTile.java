package cofh.core.network;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class PacketTile extends PacketCoFHBase {

	public static void initialize() {

		PacketHandler.instance.registerPacket(PacketTile.class);
	}

	public PacketTile() {

	}

	public PacketTile(TileEntity theTile) {

		addInt(theTile.getPos().getX());
		addInt(theTile.getPos().getY());
		addInt(theTile.getPos().getZ());

	}

	@Override
	public void handlePacket(EntityPlayer player, boolean isServer) {

		TileEntity tile = player.worldObj.getTileEntity(new BlockPos(getInt(), getInt(), getInt()));

		if (tile instanceof ITilePacketHandler) {
			((ITilePacketHandler) tile).handleTilePacket(this, isServer);
            IBlockState state = tile.getWorld().getBlockState(tile.getPos());
			tile.getWorld().notifyBlockUpdate(tile.getPos(), state, state, 3);
			if (isServer) {
				tile.getWorld().updateComparatorOutputLevel(tile.getPos(), tile.getBlockType());
			}
		} else {
			// TODO: Throw error, bad packet
		}
	}

	public static PacketTile newPacket(TileEntity theTile) {

		return new PacketTile(theTile);
	}

}
