package cofh.core.command;

import cofh.core.world.TickHandlerWorld;
import cofh.lib.util.position.ChunkCoord;
import com.google.common.base.Throwables;

import java.util.ArrayDeque;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class CommandPregen implements ISubCommand {

	public static ISubCommand instance = new CommandPregen();

	@Override
	public String getCommandName() {

		return "pregen";
	}

	@Override
	public int getPermissionLevel() {

		return 4;
	}

	@Override
	public void handleCommand(ICommandSender sender, String[] args) {

		if (args.length < 4) {
			sender.addChatMessage(new ChatComponentTranslation("info.cofh.command.syntaxError"));
			throw new WrongUsageException("info.cofh.command." + getCommandName() + ".syntax");
		}
		World world = sender.getEntityWorld();
		if (world.isRemote) {
			return;
		}

		ChunkCoordinates center = null;
		int i = 1;
		int xS, xL;
		if ("@".equals(args[i])) {
			center = sender.getPlayerCoordinates();
			++i;
			xS = CommandBase.parseInt(sender, args[i++]);
		} else {
			try {
				xS = CommandBase.parseInt(sender, args[i++]);
			} catch (Throwable t) {
				center = CommandBase.getPlayer(sender, args[i - 1]).getPlayerCoordinates();
				xS = CommandBase.parseInt(sender, args[i++]);
			}
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
			xS = (center.posX / 16) - xS;
			zS = (center.posZ / 16) - zS;

			xL = (center.posX / 16) + xL;
			zL = (center.posZ / 16) + zL;
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
			CommandHandler.logAdminCommand(sender, this, "info.cofh.command.pregen.start", (xL - xS) * (zL - zS), xS, zS, xL, zL);
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
