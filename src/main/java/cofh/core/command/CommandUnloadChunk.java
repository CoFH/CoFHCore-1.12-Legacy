package cofh.core.command;

import cofh.lib.util.RayTracer;
import com.google.common.base.Throwables;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

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

	Field chunksToUnload;

	@Override
	public void handleCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

		if (!(sender instanceof EntityPlayerMP)) {
			return;
		}

		if (chunksToUnload == null) {
			chunksToUnload = ReflectionHelper.findField(ChunkProviderServer.class, "field_73248_b", "chunksToUnload");
		}

		EntityPlayerMP player = (EntityPlayerMP) sender;
		RayTraceResult trace = RayTracer.retrace(player, 100);
		Chunk chunk = player.worldObj.getChunkFromBlockCoords(trace.getBlockPos());

		Set<Long> o;
		try {
			o = (Set<Long>) chunksToUnload.get(player.getServerWorld().getChunkProvider());
		} catch (IllegalAccessException e) {
			throw Throwables.propagate(e);
		}

		o.add(ChunkPos.asLong(chunk.xPosition, chunk.zPosition));
		CommandHandler.logAdminCommand(sender, this, "info.cofh.command.unloadchunk.success", chunk.xPosition, chunk.zPosition);
	}

	@Override
	public List<String> addTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args) {

		return null;
	}

}
