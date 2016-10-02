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

		// Empty constructor must exist!
	}

	public static PacketTile newPacket(TileEntity tile) {

		return new PacketTile(tile);
	}

	public PacketTile(TileEntity tile) {

		addInt(tile.getPos().getX());
		addInt(tile.getPos().getY());
		addInt(tile.getPos().getZ());
	}

	@Override
	public void handlePacket(EntityPlayer player, boolean isServer) {

		BlockPos pos = new BlockPos(getInt(), getInt(), getInt());
		TileEntity tile = player.worldObj.getTileEntity(pos);

		if (tile instanceof ITilePacketHandler) {
			((ITilePacketHandler) tile).handleTilePacket(this, isServer);
			IBlockState state = tile.getWorld().getBlockState(pos);
			tile.getWorld().notifyBlockUpdate(pos, state, state, 3);
			if (isServer) {
				tile.getWorld().updateComparatorOutputLevel(pos, tile.getBlockType());
			}
		} else {
			// TODO: Throw error, bad packet.
		}
	}

}
