package cofh.core.network;

import cofh.CoFHCore;
import net.minecraft.entity.player.EntityPlayer;

public class PacketCore extends PacketCoFHBase {

	public static void initialize() {

		PacketHandler.instance.registerPacket(PacketCore.class);
	}

	public enum PacketTypes {
		CONFIG_SYNC
	}

	@Override
	public void handlePacket(EntityPlayer player, boolean isServer) {

		try {
			int type = getByte();

			switch (PacketTypes.values()[type]) {
				case CONFIG_SYNC:
					CoFHCore.instance.handleConfigSync(this);
					return;
				default:
					CoFHCore.log.error("Unknown Packet! Internal: COFH, ID: " + type);
			}
		} catch (Exception e) {
			CoFHCore.log.error("Packet payload failure! Please check your config files!");
			e.printStackTrace();
		}

	}

	public static void sendConfigSyncPacketToClient(EntityPlayer player) {

		PacketHandler.sendTo(CoFHCore.instance.getConfigSync(), player);
	}

	public static PacketCoFHBase getPacket(PacketTypes theType) {

		return new PacketCore().addByte(theType.ordinal());
	}

}
