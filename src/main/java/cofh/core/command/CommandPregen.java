package cofh.core.command;

import cofh.core.util.CoreUtils;
import cofh.core.world.TickHandlerWorld;
import cofh.lib.util.position.ChunkCoord;
import com.google.common.base.Throwables;

import java.util.ArrayDeque;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class CommandPregen implements ISubCommand {

	public static ISubCommand instance = new CommandPregen();

	@Override
	public String getCommandName() {

		return "pregen";
	}

	@Override
	public void handleCommand(ICommandSender sender, String[] args) {

		if (!CoreUtils.isOpOrServer(sender.getCommandSenderName())) {
			sender.addChatMessage(new ChatComponentText(CommandHandler.COMMAND_DISALLOWED));
			return;
		}
		if (args.length < 6) {
			// TODO: error
			return;
		}
		World world = CommandBase.getCommandSenderAsPlayer(sender).worldObj;
		if (world.isRemote) {
			return;
		}

		EntityPlayer center = null;
		int i = 1;
		int xS, xL;
		try {
			xS = CommandBase.parseInt(sender, args[i++]);
		} catch (Throwable t) {
			center = CommandBase.getPlayer(sender, args[i - 1]);
			xS = CommandBase.parseInt(sender, args[i++]);
		}
		int zS = CommandBase.parseInt(sender, args[i++]), zL;
		int t = i + 1;

		try {
			xL = CommandBase.parseInt(sender, args[i++]);
			zL = CommandBase.parseInt(sender, args[i++]);
		} catch (Throwable e) {
			if (i > t || center == null) {
				throw Throwables.propagate(e);
			}
			--i;
			xL = xS;
			zL = zS;
		}

		if (center != null) {
			xS = ((int) center.posX) / 16 - xS;
			zS = ((int) center.posZ) / 16 - zS;

			xL = ((int) center.posX) / 16 + xL;
			zL = ((int) center.posZ) / 16 + zL;
		}

		if (xL < xS) {
			t = xS;
			xS = xL;
			xL = t;
		}
		if (zL < zS) {
			t = zS;
			zS = zL;
			zL = t;
		}

		synchronized (TickHandlerWorld.chunksToPreGen) {
			ArrayDeque<ChunkCoord> chunks = TickHandlerWorld.chunksToPreGen.get(world.provider.dimensionId);
			if (chunks == null) {
				chunks = new ArrayDeque<ChunkCoord>();
			}

			for (int x = xS; x <= xL; ++x) {
				for (int z = zS; z <= zL; ++z) {
					chunks.addLast(new ChunkCoord(x, z));
				}
			}
			TickHandlerWorld.chunksToPreGen.put(world.provider.dimensionId, chunks);
		}
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {

		if (args.length == 2) {
			return CommandBase.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
		}
		return null;
	}

}
