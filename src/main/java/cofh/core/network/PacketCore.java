package cofh.core.network;

import cofh.CoFHCore;
import cofh.api.core.IFilterable;
import cofh.api.core.ISecurable;
import cofh.api.core.ISecurable.AccessMode;
import cofh.api.tileentity.IRedstoneControl;
import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.api.tileentity.ITransferControl;
import cofh.core.gui.container.IAugmentableContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PacketCore extends PacketBase {

	public static void initialize() {

		PacketHandler.INSTANCE.registerPacket(PacketCore.class);
	}

	public enum PacketTypes {
		RS_POWER_UPDATE, RS_CONFIG_UPDATE, TRANSFER_UPDATE, SECURITY_UPDATE, FILTER_UPDATE, TAB_AUGMENT, CONFIG_SYNC
	}

	@Override
	public void handlePacket(EntityPlayer player, boolean isServer) {

		try {
			int type = getByte();
			switch (PacketTypes.values()[type]) {
				case RS_POWER_UPDATE:
					BlockPos pos = getCoords();
					if (player.world.isBlockLoaded(pos)) {
						TileEntity tile = player.world.getTileEntity(pos);
						if (tile instanceof IRedstoneControl) {
							IRedstoneControl rs = (IRedstoneControl) tile;
							rs.setPowered(getBool());
						}
					}
					return;
				case RS_CONFIG_UPDATE:
					pos = getCoords();
					if (player.world.isBlockLoaded(pos)) {
						TileEntity tile = player.world.getTileEntity(pos);
						if (tile instanceof IRedstoneControl) {
							IRedstoneControl rs = (IRedstoneControl) tile;
							rs.setControl(ControlMode.values()[getByte()]);
						}
					}
					return;
				case TRANSFER_UPDATE:
					pos = getCoords();
					if (player.world.isBlockLoaded(pos)) {
						TileEntity tile = player.world.getTileEntity(pos);
						if (tile instanceof ITransferControl) {
							ITransferControl transfer = (ITransferControl) tile;
							transfer.setTransferIn(getBool());
							transfer.setTransferOut(getBool());
						}
					}
					return;
				case SECURITY_UPDATE:
					if (player.openContainer instanceof ISecurable) {
						((ISecurable) player.openContainer).setAccess(AccessMode.values()[getByte()]);
					}
					return;
				case FILTER_UPDATE:
					if (player.openContainer instanceof IFilterable) {
						if (isServer) {
							((IFilterable) player.openContainer).setFlag(getInt(), getBool());
						}
					}
					return;
				case TAB_AUGMENT:
					if (player.openContainer instanceof IAugmentableContainer) {
						((IAugmentableContainer) player.openContainer).setAugmentLock(getBool());
					}
					return;
				case CONFIG_SYNC:
					return;
				default:
					CoFHCore.LOG.error("Unknown Packet! Internal: " + CoFHCore.MOD_ID + "; " + type);
			}
		} catch (Exception e) {
			CoFHCore.LOG.error("Packet payload failure! Please check your config files!", e);
		}

	}

	/* RS POWER */
	public static void sendRSPowerUpdatePacketToClients(IRedstoneControl rs, World world, BlockPos pos) {

		sendRSPowerUpdatePacketToClients(rs, world, pos.getX(), pos.getY(), pos.getZ());
	}

	private static void sendRSPowerUpdatePacketToClients(IRedstoneControl rs, World world, int x, int y, int z) {

		PacketHandler.sendToAllAround(getPacket(PacketTypes.RS_POWER_UPDATE).addCoords(x, y, z).addBool(rs.isPowered()), world, x, y, z);
	}

	/* RS CONFIG */
	public static void sendRSConfigUpdatePacketToServer(IRedstoneControl rs, BlockPos pos) {

		sendRSConfigUpdatePacketToServer(rs, pos.getX(), pos.getY(), pos.getZ());
	}

	private static void sendRSConfigUpdatePacketToServer(IRedstoneControl rs, int x, int y, int z) {

		PacketHandler.sendToServer(getPacket(PacketTypes.RS_CONFIG_UPDATE).addCoords(x, y, z).addByte(rs.getControl().ordinal()));
	}

	/* TRANSFER CONFIG */
	public static void sendTransferUpdatePacketToServer(ITransferControl transfer, BlockPos pos) {

		sendTransferUpdatePacketToServer(transfer, pos.getX(), pos.getY(), pos.getZ());
	}

	private static void sendTransferUpdatePacketToServer(ITransferControl transfer, int x, int y, int z) {

		PacketHandler.sendToServer(getPacket(PacketTypes.TRANSFER_UPDATE).addCoords(x, y, z).addBool(transfer.getTransferIn()).addBool(transfer.getTransferOut()));
	}

	/* SECURITY */
	public static void sendSecurityPacketToServer(ISecurable securable) {

		PacketHandler.sendToServer(getPacket(PacketTypes.SECURITY_UPDATE).addByte(securable.getAccess().ordinal()));
	}

	/* FILTER */
	public static void sendFilterPacketToServer(int flag, boolean value) {

		PacketHandler.sendToServer(getPacket(PacketTypes.FILTER_UPDATE).addInt(flag).addBool(value));
	}

	/* AUGMENT TAB */
	public static void sendTabAugmentPacketToServer(boolean lock) {

		PacketHandler.sendToServer(getPacket(PacketTypes.TAB_AUGMENT).addBool(lock));
	}

	public static void sendConfigSyncPacketToClient(EntityPlayer player) {

		// PacketHandler.sendTo(CoFHCore.instance.getConfigSync(), player);
	}

	public static PacketBase getPacket(PacketTypes type) {

		return new PacketCore().addByte(type.ordinal());
	}

}
