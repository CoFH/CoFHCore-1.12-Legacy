package cofh.masquerade;

import cofh.network.CoFHPacket;
import cofh.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public class MasqueradePacketHandler extends CoFHPacket {

	public void handlePacket(EntityPlayer thePlayer) {

		try {
			// TODO: update this to use payload properly, change this field to private
			switch (Type.values()[datain.readByte()]) {
				case CAPE_JOIN:
					RegistryCapes.readJoinPacket(datain);
					return;
				case CAPE_ADD:
					RegistryCapes.readAddPacket(datain);
					return;
				case CAPE_REMOVE:
					RegistryCapes.readRemovePacket(datain);
					return;
				case SKIN_JOIN:
					RegistrySkins.readJoinPacket(datain);
					return;
				case SKIN_ADD:
					RegistrySkins.readAddPacket(datain);
					return;
				case SKIN_REMOVE:
					RegistrySkins.readRemovePacket(datain);
					return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void initialize() {

		PacketHandler.cofhPacketHandler.registerPacket(MasqueradePacketHandler.class);
	}

	public static enum Type {
		CAPE_JOIN, CAPE_ADD, CAPE_REMOVE, SKIN_JOIN, SKIN_ADD, SKIN_REMOVE
	}

}
