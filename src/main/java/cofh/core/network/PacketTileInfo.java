package cofh.core.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class PacketTileInfo extends PacketCoFHBase {

    public static void initialize() {

        PacketHandler.instance.registerPacket(PacketTileInfo.class);
    }

    public PacketTileInfo() {

    }

    public PacketTileInfo(TileEntity theTile) {

        addInt(theTile.getPos().getX());
        addInt(theTile.getPos().getY());
        addInt(theTile.getPos().getZ());

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

        TileEntity tile = player.worldObj.getTileEntity(new BlockPos(getInt(), getInt(), getInt()));

        if (tile instanceof ITileInfoPacketHandler) {
            ((ITileInfoPacketHandler) tile).handleTileInfoPacket(this, isServer, player);
        } else {
            // TODO: Throw error, bad packet
        }
    }

    public static PacketTileInfo newPacket(TileEntity theTile) {

        return new PacketTileInfo(theTile);
    }

}
