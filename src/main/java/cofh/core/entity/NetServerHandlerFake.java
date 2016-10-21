package cofh.core.entity;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketPlayerPosLook.EnumFlags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.crypto.SecretKey;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Set;

public class NetServerHandlerFake extends NetHandlerPlayServer {

    public static class NetworkManagerFake extends NetworkManager {

        public NetworkManagerFake() {
            super(EnumPacketDirection.CLIENTBOUND);
        }

        @Override
        public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception {
        }

        @Override
        public void setConnectionState(EnumConnectionState p_150723_1_) {
        }

        @Override
        public void channelInactive(ChannelHandlerContext p_channelInactive_1_) {
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_) {
        }

        @Override
        public void setNetHandler(INetHandler p_150719_1_) {
        }

        @Override
        public void sendPacket(Packet p_150725_1_) {
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
        public boolean isLocalChannel() {
            return false;
        }

        @SideOnly(Side.CLIENT)
        public static NetworkManager provideLanClient(InetAddress p_150726_0_, int p_150726_1_) {
            return null;
        }

        @SideOnly(Side.CLIENT)
        public static NetworkManager provideLocalClient(SocketAddress p_150722_0_) {
            return null;
        }

        @Override
        public void enableEncryption(SecretKey p_150727_1_) {
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
        public void setCompressionThreshold(int threshold) {
        }

        @Override
        public void disableAutoRead() {
        }

        @Override
        public void checkDisconnected() {
        }

        @Override
        public Channel channel() {
            return null;
        }

    }

    public NetServerHandlerFake(MinecraftServer par1MinecraftServer, EntityPlayerMP par3EntityPlayerMP) {
        super(par1MinecraftServer, new NetworkManagerFake(), par3EntityPlayerMP);
    }

    @Override
    public void update() {
    }

    @Override
    public void kickPlayerFromServer(String p_147360_1_) {
    }

    @Override
    public void processInput(CPacketInput p_147358_1_) {
    }

    @Override
    public void processVehicleMove(CPacketVehicleMove packetIn) {
    }

    @Override
    public void processConfirmTeleport(CPacketConfirmTeleport packetIn) {
    }

    @Override
    public void processPlayer(CPacketPlayer p_147347_1_) {
    }

    @Override
    public void setPlayerLocation(double p_147364_1_, double p_147364_3_, double p_147364_5_, float p_147364_7_, float p_147364_8_) {
    }

    @Override
    public void setPlayerLocation(double x, double y, double z, float yaw, float pitch, Set<EnumFlags> relativeSet) {
    }

    @Override
    public void processPlayerDigging(CPacketPlayerDigging p_147345_1_) {
    }

    @Override
    public void processRightClickBlock(CPacketPlayerTryUseItemOnBlock packetIn) {
    }

    @Override
    public void processPlayerBlockPlacement(CPacketPlayerTryUseItem p_147346_1_) {
    }

    @Override
    public void handleSpectate(CPacketSpectate packetIn) {
    }

    @Override
    public void handleResourcePackStatus(CPacketResourcePackStatus packetIn) {
    }

    @Override
    public void processSteerBoat(CPacketSteerBoat packetIn) {
    }

    @Override
    public void onDisconnect(ITextComponent p_147231_1_) {
    }

    @Override
    public void sendPacket(final Packet p_147359_1_) {
    }

    @Override
    public void processHeldItemChange(CPacketHeldItemChange p_147355_1_) {
    }

    @Override
    public void processChatMessage(CPacketChatMessage p_147354_1_) {
    }

    @Override
    public void handleAnimation(CPacketAnimation p_147350_1_) {
    }

    @Override
    public void processEntityAction(CPacketEntityAction p_147357_1_) {
    }

    @Override
    public void processUseEntity(CPacketUseEntity p_147340_1_) {
    }

    @Override
    public void processClientStatus(CPacketClientStatus p_147342_1_) {
    }

    @Override
    public void processCloseWindow(CPacketCloseWindow p_147356_1_) {
    }

    @Override
    public void processClickWindow(CPacketClickWindow p_147351_1_) {
    }

    @Override
    public void processEnchantItem(CPacketEnchantItem p_147338_1_) {
    }

    @Override
    public void processCreativeInventoryAction(CPacketCreativeInventoryAction p_147344_1_) {
    }

    @Override
    public void processConfirmTransaction(CPacketConfirmTransaction p_147339_1_) {
    }

    @Override
    public void processUpdateSign(CPacketUpdateSign p_147343_1_) {
    }

    @Override
    public void processKeepAlive(CPacketKeepAlive p_147353_1_) {
    }

    @Override
    public void processPlayerAbilities(CPacketPlayerAbilities p_147348_1_) {
    }

    @Override
    public void processTabComplete(CPacketTabComplete p_147341_1_) {
    }

    @Override
    public void processClientSettings(CPacketClientSettings p_147352_1_) {
    }

    @Override
    public void processCustomPayload(CPacketCustomPayload p_147349_1_) {
    }
}
