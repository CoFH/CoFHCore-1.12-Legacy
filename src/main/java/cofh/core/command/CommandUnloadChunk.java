package cofh.core.command;

import cofh.lib.util.RayTracer;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.chunk.Chunk;

import java.util.List;

public class CommandUnloadChunk implements ISubCommand {

	public static ISubCommand instance = new CommandUnloadChunk();

	@Override
	public String getCommandName() {

		return "unloadchunk";
	}

	@Override
	public int getPermissionLevel() {

		return 4;
	}

	@Override
	public void handleCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

		if (!(sender instanceof EntityPlayerMP)) {
			return;
		}

		EntityPlayerMP player = (EntityPlayerMP) sender;
		RayTraceResult trace = RayTracer.retrace(player, 100);
		Chunk chunk = player.world.getChunkFromBlockCoords(trace.getBlockPos());

		// TODO: Old way of doing it - is there a specific reason?
		//		Set<Long> o = player.getServerWorld().getChunkProvider().droppedChunksSet;
		//		o.add(ChunkPos.asLong(chunk.xPosition, chunk.zPosition));

		player.getServerWorld().getChunkProvider().unload(chunk);

		CommandHandler.logAdminCommand(sender, this, "chat.cofh.command.unloadchunk.success", chunk.xPosition, chunk.zPosition);
	}

	@Override
	public List<String> addTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args) {

		return null;
	}

}
