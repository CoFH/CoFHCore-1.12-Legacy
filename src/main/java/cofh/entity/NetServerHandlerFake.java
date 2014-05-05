package cofh.entity;

import java.net.SocketAddress;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class NetServerHandlerFake extends NetServerHandler {

	public static class NetworkManagerFake implements INetworkManager {

		@Override
		public void setNetHandler(NetHandler nethandler) {

		}

		@Override
		public void addToSendQueue(Packet packet) {

		}

		@Override
		public void wakeThreads() {

		}

		@Override
		public void processReadPackets() {

		}

		@Override
		public SocketAddress getSocketAddress() {

			return null;
		}

		@Override
		public void serverShutdown() {

		}

		@Override
		public int packetSize() {

			return 0;
		}

		@Override
		public void networkShutdown(String s, Object... var2) {

		}

		@Override
		public void closeConnections() {

		}

	}

	public NetServerHandlerFake(MinecraftServer par1MinecraftServer, EntityPlayerMP par3EntityPlayerMP) {

		super(par1MinecraftServer, new NetworkManagerFake(), par3EntityPlayerMP);
	}

	@Override
	public void kickPlayerFromServer(String par1Str) {

	}

	@Override
	public void func_110774_a(Packet27PlayerInput par1Packet27PlayerInput) {

	}

	@Override
	public void handleFlying(Packet10Flying par1Packet10Flying) {

	}

	@Override
	public void setPlayerLocation(double par1, double par3, double par5, float par7, float par8) {

	}

	@Override
	public void handleBlockDig(Packet14BlockDig par1Packet14BlockDig) {

	}

	@Override
	public void handlePlace(Packet15Place par1Packet15Place) {

	}

	@Override
	public void handleErrorMessage(String par1Str, Object[] par2ArrayOfObj) {

	}

	@Override
	public void unexpectedPacket(Packet par1Packet) {

	}

	@Override
	public void sendPacketToPlayer(Packet par1Packet) {

	}

	@Override
	public void handleBlockItemSwitch(Packet16BlockItemSwitch par1Packet16BlockItemSwitch) {

	}

	@Override
	public void handleChat(Packet3Chat par1Packet3Chat) {

	}

	private void handleSlashCommand(String par1Str) {

	}

	@Override
	public void handleAnimation(Packet18Animation par1Packet18Animation) {

	}

	@Override
	public void handleEntityAction(Packet19EntityAction par1Packet19EntityAction) {

	}

	@Override
	public void handleKickDisconnect(Packet255KickDisconnect par1Packet255KickDisconnect) {

	}

	/**
	 * returns 0 for memoryMapped connections
	 */
	@Override
	public int packetSize() {

		return this.netManager.packetSize();
	}

	@Override
	public void handleUseEntity(Packet7UseEntity par1Packet7UseEntity) {

	}

	@Override
	public void handleClientCommand(Packet205ClientCommand par1Packet205ClientCommand) {

	}

	@Override
	public boolean canProcessPacketsAsync() {

		return true;
	}

	@Override
	public void handleRespawn(Packet9Respawn par1Packet9Respawn) {

	}

	@Override
	public void handleCloseWindow(Packet101CloseWindow par1Packet101CloseWindow) {

	}

	@Override
	public void handleWindowClick(Packet102WindowClick par1Packet102WindowClick) {

	}

	@Override
	public void handleEnchantItem(Packet108EnchantItem par1Packet108EnchantItem) {

	}

	@Override
	public void handleCreativeSetSlot(Packet107CreativeSetSlot par1Packet107CreativeSetSlot) {

	}

	@Override
	public void handleTransaction(Packet106Transaction par1Packet106Transaction) {

	}

	@Override
	public void handleUpdateSign(Packet130UpdateSign par1Packet130UpdateSign) {

	}

	@Override
	public void handleKeepAlive(Packet0KeepAlive par1Packet0KeepAlive) {

	}

	@Override
	public boolean isServerHandler() {

		return true;
	}

	@Override
	public void handlePlayerAbilities(Packet202PlayerAbilities par1Packet202PlayerAbilities) {

	}

	@Override
	public void handleAutoComplete(Packet203AutoComplete par1Packet203AutoComplete) {

	}

	@Override
	public void handleClientInfo(Packet204ClientInfo par1Packet204ClientInfo) {

	}

	@Override
	public void handleCustomPayload(Packet250CustomPayload par1Packet250CustomPayload) {

	}

	@Override
	public void handleVanilla250Packet(Packet250CustomPayload par1Packet250CustomPayload) {

	}

	@Override
	public boolean isConnectionClosed() {

		return true;
	}

	@Override
	public void handleMapData(Packet131MapData par1Packet131MapData) {

	}

}
