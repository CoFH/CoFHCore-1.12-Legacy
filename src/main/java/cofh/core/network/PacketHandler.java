package cofh.core.network;

import cofh.CoFHCore;
import cofh.core.init.CoreProps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Packet pipeline class. Directs all registered packet data to be handled by the packets themselves.
 *
 * @author sirgingalot, cpw, Zeldo
 */

@ChannelHandler.Sharable
public class PacketHandler extends MessageToMessageCodec<FMLProxyPacket, PacketBase> {

	public static final PacketHandler INSTANCE = new PacketHandler();

	private EnumMap<Side, FMLEmbeddedChannel> channels;
	private final LinkedList<Class<? extends PacketBase>> packets = new LinkedList<>();
	private boolean isPostInitialised = false;

	/* INIT */
	public static void preInit() {

		INSTANCE.channels = NetworkRegistry.INSTANCE.newChannel("CoFH", INSTANCE);
	}

	public static void postInit() {

		if (INSTANCE.isPostInitialised) {
			return;
		}
		INSTANCE.isPostInitialised = true;
		(INSTANCE.packets).sort((packetClass1, packetClass2) -> {

			int com = String.CASE_INSENSITIVE_ORDER.compare(packetClass1.getCanonicalName(), packetClass2.getCanonicalName());
			if (com == 0) {
				com = packetClass1.getCanonicalName().compareTo(packetClass2.getCanonicalName());
			}
			return com;
		});
	}

	/* ENCODE / DECODE */
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
		switch (ctx.channel().attr(NetworkRegistry.CHANNEL_SOURCE).get()) {
			case CLIENT:
				player = CoFHCore.proxy.getClientPlayer();
				handlePacketClient(pkt, player);
				break;
			case SERVER:
				INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
				player = ((NetHandlerPlayServer) netHandler).player;
				handlePacketServer(pkt, player);
				break;
			default:
		}
	}

	/* HELPERS */
	private void handlePacketClient(final PacketBase packet, final EntityPlayer player) {

		IThreadListener threadListener = CoFHCore.proxy.getClientListener();
		if (!threadListener.isCallingFromMinecraftThread()) {
			threadListener.addScheduledTask(() -> handlePacketClient(packet, player));
		} else {
			packet.handleClientSide(player);
		}
	}

	private void handlePacketServer(final PacketBase packet, final EntityPlayer player) {

		IThreadListener threadListener = CoFHCore.proxy.getServerListener();
		if (!threadListener.isCallingFromMinecraftThread()) {
			threadListener.addScheduledTask(() -> handlePacketServer(packet, player));
		} else {
			packet.handleServerSide(player);
		}
	}

	private void injectPacket(byte[] data) throws Exception {

		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			throw new RuntimeException("Packet hack only works for the client end.");
		}
		ByteBuf buf = Unpooled.copiedBuffer(data);
		byte discriminator = buf.readByte();
		Class<? extends PacketBase> packetClass = this.packets.get(discriminator);

		if (packetClass == null) {
			throw new NullPointerException("No packet registered for discriminator: " + discriminator);
		}
		PacketBase pkt = packetClass.newInstance();
		pkt.decodeInto(null, buf.slice());
		handlePacketClient(pkt, CoFHCore.proxy.getClientPlayer());
	}

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

	public static void handleNBTPacket(NBTTagCompound tagCompound) {

		try {
			INSTANCE.injectPacket(tagCompound.getByteArray("CoFH:data"));
		} catch (Exception e) {
			FMLLog.severe("Unable to handle CoFH packet!");
			e.printStackTrace();
		}
	}

	/* TRANSMISSION */
	public static void sendToAll(PacketBase message) {

		INSTANCE.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
		INSTANCE.channels.get(Side.SERVER).writeAndFlush(message);
	}

	public static void sendTo(PacketBase message, EntityPlayerMP player) {

		INSTANCE.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		INSTANCE.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		INSTANCE.channels.get(Side.SERVER).writeAndFlush(message);
	}

	public static void sendTo(PacketBase message, EntityPlayer player) {

		INSTANCE.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		INSTANCE.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		INSTANCE.channels.get(Side.SERVER).writeAndFlush(message);
	}

	public static void sendToAllAround(PacketBase message, NetworkRegistry.TargetPoint point) {

		INSTANCE.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		INSTANCE.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
		INSTANCE.channels.get(Side.SERVER).writeAndFlush(message);
	}

	public static void sendToAllAround(PacketBase message, TileEntity theTile) {

		INSTANCE.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		INSTANCE.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(new TargetPoint(theTile.getWorld().provider.getDimension(), theTile.getPos().getX(), theTile.getPos().getY(), theTile.getPos().getZ(), CoreProps.NETWORK_UPDATE_RANGE));
		INSTANCE.channels.get(Side.SERVER).writeAndFlush(message);
	}

	public static void sendToAllAround(PacketBase message, World world, int x, int y, int z) {

		INSTANCE.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		INSTANCE.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(new TargetPoint(world.provider.getDimension(), x, y, z, CoreProps.NETWORK_UPDATE_RANGE));
		INSTANCE.channels.get(Side.SERVER).writeAndFlush(message);
	}

	public static void sendToDimension(PacketBase message, int dimensionId) {

		INSTANCE.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
		INSTANCE.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionId);
		INSTANCE.channels.get(Side.SERVER).writeAndFlush(message);
	}

	public static void sendToServer(PacketBase message) {

		INSTANCE.channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		INSTANCE.channels.get(Side.CLIENT).writeAndFlush(message);
	}

	/* CONVERSION */
	public static Packet toMCPacket(PacketBase packet) {

		return INSTANCE.channels.get(FMLCommonHandler.instance().getEffectiveSide()).generatePacketFrom(packet);
	}

	public static NBTTagCompound toNBTTag(PacketBase packetBase, NBTTagCompound inputTag) {

		ByteBuf buf = Unpooled.buffer();
		byte discriminator = (byte) INSTANCE.packets.indexOf(packetBase.getClass());
		buf.writeByte(discriminator);
		packetBase.encodeInto(null, buf);
		inputTag.setByteArray("CoFH:data", buf.array());
		return inputTag;
	}

	public static SPacketUpdateTileEntity toTilePacket(PacketBase packet, BlockPos pos) {

		return new SPacketUpdateTileEntity(pos, 0, toNBTTag(packet, new NBTTagCompound()));
	}

}
