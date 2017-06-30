package cofh.api.tileentity;

import net.minecraft.util.EnumFacing;

/**
 * Implement this interface on TileEntities which should connect to item transportation blocks.
 */
public interface IInventoryConnection {

	/**
	 * @param from Side to which a connector would connect
	 * @return DEFAULT if the connector should decide how to connect; FORCE if the connector should always connect; DENY if the connector should never connect.
	 */
	ConnectionType canConnectInventory(EnumFacing from);

	enum ConnectionType {
		DEFAULT, FORCE, DENY;

		public final boolean canConnect = ordinal() != 2;
		public final boolean forceConnect = ordinal() == 1;
	}

}
