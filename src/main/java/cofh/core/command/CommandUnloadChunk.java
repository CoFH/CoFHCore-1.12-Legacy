package cofh.core.command;

import cofh.core.util.CoreUtils;
import cofh.repack.codechicken.lib.raytracer.RayTracer;
import com.google.common.base.Throwables;
import cpw.mods.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

public class CommandUnloadChunk implements ISubCommand {

	public static ISubCommand instance = new CommandUnloadChunk();

	@Override
	public String getCommandName() {

		return "unloadchunk";
	}

	Field chunksToUnload;

	@Override
	public void handleCommand(ICommandSender sender, String[] args) {

		if (!CoreUtils.isOpOrServer(sender.getCommandSenderName())) {
			sender.addChatMessage(new ChatComponentText(CommandHandler.COMMAND_DISALLOWED));
			return;
		}
		if (!(sender instanceof EntityPlayerMP))
			return;

		if (chunksToUnload == null) {
			chunksToUnload = ReflectionHelper.findField(ChunkProviderServer.class, "field_73248_b", "chunksToUnload");
		}

		EntityPlayerMP player = (EntityPlayerMP) sender;
		MovingObjectPosition trace = RayTracer.reTrace(player.worldObj, player, 100);
		Chunk chunk = player.worldObj.getChunkFromBlockCoords(trace.blockX, trace.blockZ);

		Set<Long> o;
		try {
			o = (Set<Long>) chunksToUnload.get(player.getServerForPlayer().theChunkProviderServer);
		} catch (IllegalAccessException e) {
			throw Throwables.propagate(e);
		}

		o.add(ChunkCoordIntPair.chunkXZ2Int(chunk.xPosition, chunk.zPosition));
		sender.addChatMessage(new ChatComponentText(String.format("Unloading %s,%s", chunk.xPosition, chunk.zPosition)));
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {

		return null;
	}

}
