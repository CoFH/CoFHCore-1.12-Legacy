package cofh.core.entity;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;

import javax.crypto.SecretKey;
import java.net.SocketAddress;
import java.util.Set;

public class FakeNetServerHandler extends NetHandlerPlayServer {
	public FakeNetServerHandler(MinecraftServer server, FakePlayerCoFH fakePlayer) {
		super(server, new FakeNetworkManager(), fakePlayer);
	}

	@Override
	public void kickPlayerFromServer(String reason) {

	}

	@Override
	public void processInput(CPacketInput packetIn) {

	}

	@Override
	public void processPlayer(CPacketPlayer packetIn) {

	}

	@Override
	public void setPlayerLocation(double x, double y, double z, float yaw, float pitch) {

	}

	@Override
	public void setPlayerLocation(double x, double y, double z, float yaw, float pitch, Set<SPacketPlayerPosLook.EnumFlags> relativeSet) {

	}

	@Override
	public void processPlayerDigging(CPacketPlayerDigging packetIn) {

	}

	@Override
	public void processPlayerBlockPlacement(CPacketPlayerTryUseItem packetIn) {

	}

	@Override
	public void onDisconnect(ITextComponent reason) {

	}

	@Override
	public void sendPacket(Packet<?> packetIn) {

	}

	@Override
	public void processHeldItemChange(CPacketHeldItemChange packetIn) {

	}

	@Override
	public void processChatMessage(CPacketChatMessage packetIn) {

	}

	@Override
	public void handleAnimation(CPacketAnimation packetIn) {

	}

	@Override
	public void processEntityAction(CPacketEntityAction packetIn) {

	}

	@Override
	public void processUseEntity(CPacketUseEntity packetIn) {

	}

	@Override
	public void processClientStatus(CPacketClientStatus packetIn) {

	}

	@Override
	public void processCloseWindow(CPacketCloseWindow packetIn) {

	}

	@Override
	public void processClickWindow(CPacketClickWindow packetIn) {

	}

	@Override
	public void processEnchantItem(CPacketEnchantItem packetIn) {

	}

	@Override
	public void processCreativeInventoryAction(CPacketCreativeInventoryAction packetIn) {

	}

	@Override
	public void processConfirmTransaction(CPacketConfirmTransaction packetIn) {

	}

	@Override
	public void processUpdateSign(CPacketUpdateSign packetIn) {

	}

	@Override
	public void processKeepAlive(CPacketKeepAlive packetIn) {

	}

	@Override
	public void processPlayerAbilities(CPacketPlayerAbilities packetIn) {

	}

	@Override
	public void processTabComplete(CPacketTabComplete packetIn) {

	}

	@Override
	public void processClientSettings(CPacketClientSettings packetIn) {

	}

	@Override
	public void processCustomPayload(CPacketCustomPayload packetIn) {

	}

	@Override
	public void processRightClickBlock(CPacketPlayerTryUseItemOnBlock packetIn) {

	}

	private static class FakeNetworkManager extends NetworkManager {
		public FakeNetworkManager() {

				super(EnumPacketDirection.SERVERBOUND);
		}

		@Override
		public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception {

		}

		@Override
		public void setConnectionState(EnumConnectionState newState) {

		}

		@Override
		public void channelInactive(ChannelHandlerContext p_channelInactive_1_) throws Exception {

		}

		@Override
		public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_) throws Exception {

		}

		@Override
		public void setNetHandler(INetHandler handler) {

		}

		@Override
		public void sendPacket(Packet<?> packetIn) {

		}

		@Override
		public void sendPacket(Packet<?> packetIn, GenericFutureListener<? extends Future<? super Void>> listener, GenericFutureListener<? extends Future<? super Void>>... listeners) {

		}

		@Override
		public void processReceivedPackets() {

		}

		@Override
		public SocketAddress getRemoteAddress() {
			return null;
		}

		@Override
		public void closeChannel(ITextComponent message) {

		}

		@Override
		public boolean isLocalChannel() {
			return false;
		}

		@Override
		public void enableEncryption(SecretKey key) {

		}

		@Override
		public boolean isChannelOpen() {
			return false;
		}

		@Override
		public INetHandler getNetHandler() {
			return null;
		}

		@Override
		public ITextComponent getExitMessage() {
			return null;
		}

		@Override
		public void disableAutoRead() {

		}

		@Override
		public Channel channel() {
			return null;
		}
	}
}
