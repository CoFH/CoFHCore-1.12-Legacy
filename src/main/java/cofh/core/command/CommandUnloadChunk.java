package cofh.core.command;

import cofh.CoFHCore;
import cofh.core.util.RayTracer;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.chunk.Chunk;

import java.util.List;

public class CommandUnloadChunk implements ISubCommand {

	public static final CommandUnloadChunk INSTANCE = new CommandUnloadChunk();

	public static int permissionLevel = 4;

	public static void config() {

		String category = "Command." + INSTANCE.getCommandName();
		String comment = "Adjust this value to change the default permission level for the " + INSTANCE.getCommandName() + " command.";
		permissionLevel = CoFHCore.CONFIG_CORE.getConfiguration().getInt("PermissionLevel", category, permissionLevel, -1, 4, comment);
	}

	@Override
	public String getCommandName() {

		return "unloadchunk";
	}

	@Override
	public int getPermissionLevel() {

		return permissionLevel;
	}

	@Override
	public void handleCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

		if (!(sender instanceof EntityPlayerMP)) {
			return;
		}
		EntityPlayerMP player = (EntityPlayerMP) sender;
		RayTraceResult trace = RayTracer.retrace(player, 100);
		Chunk chunk = player.world.getChunkFromBlockCoords(trace.getBlockPos());
		player.getServerWorld().getChunkProvider().queueUnload(chunk);

		CommandHandler.logAdminCommand(sender, this, "chat.cofh.command.unloadchunk.success", chunk.x, chunk.z);
	}

	@Override
	public List<String> addTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args) {

		return null;
	}

}
