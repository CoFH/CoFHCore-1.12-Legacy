package cofh.core.util.helpers;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Contains various helper functions to assist with determining Server/Client status.
 *
 * @author King Lemming
 */
public final class ServerHelper {

	private ServerHelper() {

	}

	public static boolean isClientWorld(World world) {

		return world.isRemote;
	}

	public static boolean isServerWorld(World world) {

		return !world.isRemote;
	}

	public static boolean isSinglePlayerServer() {

		return FMLCommonHandler.instance().getMinecraftServerInstance() != null;
	}

	public static boolean isMultiPlayerServer() {

		return FMLCommonHandler.instance().getMinecraftServerInstance() == null;
	}

	/**
	 * This function circumvents a miserable failing.
	 */
	public static void sendItemUsePacket(World world, BlockPos pos, EnumFacing hitSide, EnumHand hand, float hitX, float hitY, float hitZ) {

		if (isServerWorld(world)) {
			return;
		}
		NetHandlerPlayClient netClientHandler = (NetHandlerPlayClient) FMLClientHandler.instance().getClientPlayHandler();
		netClientHandler.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, hitSide, hand, hitX, hitY, hitZ));
	}

}
