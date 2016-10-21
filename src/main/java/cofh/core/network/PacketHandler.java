package cofh.core.network;

import cofh.core.CoFHProps;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Packet pipeline class. Directs all registered packet data to be handled by the packets themselves.
 *
 * @author sirgingalot, cpw, Zeldo
 */

@ChannelHandler.Sharable
public class PacketHandler extends MessageToMessageCodec<FMLProxyPacket, PacketBase> {

	public static final PacketHandler instance = new PacketHandler();

	private EnumMap<Side, FMLEmbeddedChannel> channels;
	private final LinkedList<Class<? extends PacketBase>> packets = new LinkedList<Class<? extends PacketBase>>();
	private boolean isPostInitialised = false;

	public boolean registerPacket(Class<? extends PacketBase> packet) {

		if (this.packets.size() > 256) {
			return false;
		}
		if (this.packets.contains(packet)) {
			return false;
		}
		if (this.isPostInitialised) {
			// TODO: Resort or throw error
			return false;
		}
		this.packets.add(packet);
		return true;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, PacketBase msg, List<Object> out) throws Exception {

		ByteBuf buffer = Unpooled.buffer();
		Class<? extends PacketBase> packetClass = msg.getClass();

		if (!this.packets.contains(msg.getClass())) {
			throw new NullPointerException("No Packet Registered for: " + msg.getClass().getCanonicalName());
		}
		byte discriminator = (byte) this.packets.indexOf(packetClass);
		buffer.writeByte(discriminator);
		msg.encodeInto(ctx, buffer);
		FMLProxyPacket proxyPacket = new FMLProxyPacket(new PacketBuffer(buffer.copy()), ctx.channel().attr(NetworkRegistry.FML_CHANNEL).get());
		out.add(proxyPacket);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, FMLProxyPacket msg, List<Object> out) throws Exception {

		ByteBuf payload = msg.payload();
		byte discriminator = payload.readByte();
		Class<? extends PacketBase> packetClass = this.packets.get(discriminator);

		if (packetClass == null) {
			throw new NullPointerException("No packet registered for discriminator: " + discriminator);
		}
		PacketBase pkt = packetClass.newInstance();
		pkt.decodeInto(ctx, payload.slice());

		EntityPlayer player;
		switch (FMLCommonHandler.instance().getEffectiveSide()) {
		case CLIENT:
			player = this.getClientPlayer();
			handlePacketClient(pkt, player);
			break;

		case SERVER:
			INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
			player = ((NetHandlerPlayServer) netHandler).playerEntity;
			handlePacketServer(pkt, player);
			break;

		default:
		}
	}

	private void handlePacketClient(final PacketBase packet, final EntityPlayer player){
        if (!Minecraft.getMinecraft().isCallingFromMinecraftThread()){
            Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    handlePacketClient(packet, player);
                }
            });
        } else {
            packet.handleClientSide(player);
        }
    }

    private void handlePacketServer(final PacketBase packet, final EntityPlayer player){
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (!server.isCallingFromMinecraftThread()){
            server.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    handlePacketServer(packet, player);
                }
            });
        } else {
            packet.handleClientSide(player);
        }
    }

	// Method to call from FMLInitializationEvent
	public void initialize() {

		this.channels = NetworkRegistry.INSTANCE.newChannel("CoFH", this);
	}

	// Method to call from FMLPostInitializationEvent
	// Ensures that packet discriminators are common between server and client
	// by using logical sorting
	public void postInit() {

		if (this.isPostInitialised) {
			return;
		}
		this.isPostInitialised = true;
		Collections.sort(this.packets, new Comparator<Class<? extends PacketBase>>() {

			@Override
			public int compare(Class<? extends PacketBase> packetClass1, Class<? extends PacketBase> packetClass2) {

				int com = String.CASE_INSENSITIVE_ORDER.compare(packetClass1.getCanonicalName(), packetClass2.getCanonicalName());
				if (com == 0) {
					com = packetClass1.getCanonicalName().compareTo(packetClass2.getCanonicalName());
				}
				return com;
			}
		});
	}

	@SideOnly(Side.CLIENT)
	private EntityPlayer getClientPlayer() {

		return Minecraft.getMinecraft().thePlayer;
	}

	public static void sendToAll(PacketBase message) {

		instance.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
		instance.channels.get(Side.SERVER).writeAndFlush(message);
	}

	public static void sendTo(PacketBase message, EntityPlayerMP player) {

		instance.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		instance.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		instance.channels.get(Side.SERVER).writeAndFlush(message);
	}

	public static void sendTo(PacketBase message, EntityPlayer player) {

		instance.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		instance.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		instance.channels.get(Side.SERVER).writeAndFlush(message);
	}

	public static void sendToAllAround(PacketBase message, NetworkRegistry.TargetPoint point) {

		instance.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		instance.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
		instance.channels.get(Side.SERVER).writeAndFlush(message);
	}

	public static void sendToAllAround(PacketBase message, TileEntity theTile) {

		instance.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		instance.channels
				.get(Side.SERVER)
				.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS)
				.set(new TargetPoint(theTile.getWorld().provider.getDimension(), theTile.getPos().getX(), theTile.getPos().getY(), theTile.getPos().getZ(), CoFHProps.NETWORK_UPDATE_RANGE));
		instance.channels.get(Side.SERVER).writeAndFlush(message);
	}

	public static void sendToAllAround(PacketBase message, World world, int x, int y, int z) {

		instance.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		instance.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS)
				.set(new TargetPoint(world.provider.getDimension(), x, y, z, CoFHProps.NETWORK_UPDATE_RANGE));
		instance.channels.get(Side.SERVER).writeAndFlush(message);
	}

	public static void sendToDimension(PacketBase message, int dimensionId) {

		instance.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
		instance.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionId);
		instance.channels.get(Side.SERVER).writeAndFlush(message);
	}

	public static void sendToServer(PacketBase message) {

		instance.channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		instance.channels.get(Side.CLIENT).writeAndFlush(message);
	}

	public static Packet toMCPacket(PacketBase packet) {

		return instance.channels.get(FMLCommonHandler.instance().getEffectiveSide()).generatePacketFrom(packet);
	}

}
