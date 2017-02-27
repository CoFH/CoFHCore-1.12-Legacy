package cofh.core.command;

import cofh.core.init.CoreProps;
import com.google.common.base.Throwables;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CommandTPS implements ISubCommand {

	public static CommandTPS instance = new CommandTPS();

	private static DecimalFormat floatfmt = new DecimalFormat("##0.00");

	// private static final int MAX_TPS = 20;
	// private static final int MIN_TICK_MS = 50;

	private double getTickTimeSum(long[] times) {

		long timesum = 0L;
		if (times == null) {
			return 0.0D;
		}
		for (int i = 0; i < times.length; i++) {
			timesum += times[i];
		}

		return timesum / times.length;
	}

	private double getTickMs(World world) {

		return getTickTimeSum(world == null ? CoreProps.server.tickTimeArray : (long[]) CoreProps.server.worldTickTimes.get(Integer.valueOf(world.provider.getDimension()))) * 1.0E-006D;
	}

	private double getTps(World world) {

		double tps = 1000.0D / getTickMs(world);
		return tps > 20.0D ? 20.0D : tps;
	}

	/* ISubCommand */
	@Override
	public String getCommandName() {

		return "tps";
	}

	@Override
	public int getPermissionLevel() {

		return 0;
	}

	@Override
	public void handleCommand(MinecraftServer server, ICommandSender sender, String[] arguments) throws CommandException {

		if (arguments.length < 2) {
			double tps = getTps(null);
			double tickms = getTickMs(null);

			sender.addChatMessage(new TextComponentString("Overall: " + floatfmt.format(tps) + " TPS/" + floatfmt.format(tickms) + "MS (" + (int) (tps / 20.0D * 100.0D) + "%)"));

			for (World world : CoreProps.server.worldServers) {
				tps = getTps(world);
				tickms = getTickMs(world);
				sender.addChatMessage(new TextComponentString(world.provider.getDimensionType().getName() + " [" + world.provider.getDimension() + "]: " + floatfmt.format(tps) + " TPS/" + floatfmt.format(tickms) + "MS (" + (int) (tps / 20.0D * 100.0D) + "%)"));
			}
		} else if (arguments[1].toLowerCase(Locale.US).charAt(0) == 'o') {
			double tickms = getTickMs(null);
			double tps = getTps(null);

			sender.addChatMessage(new TextComponentString("Overall server tick"));
			sender.addChatMessage(new TextComponentString("TPS: " + floatfmt.format(tps) + " TPS of " + floatfmt.format(20L) + " TPS (" + (int) (tps / 20.0D * 100.0D) + "%)"));
			sender.addChatMessage(new TextComponentString("Tick time: " + floatfmt.format(tickms) + " ms of " + floatfmt.format(50L) + " ms"));
		} else if (arguments[1].toLowerCase(Locale.US).charAt(0) == 'a') {
			double tickms = getTickMs(null);
			double tps = getTps(null);

			sender.addChatMessage(new TextComponentString("Overall server tick"));
			sender.addChatMessage(new TextComponentString("TPS: " + floatfmt.format(tps) + " TPS of " + floatfmt.format(20L) + " TPS (" + (int) (tps / 20.0D * 100.0D) + "%)"));
			sender.addChatMessage(new TextComponentString("Tick time: " + floatfmt.format(tickms) + " ms of " + floatfmt.format(50L) + " ms"));
			int loadedChunks = 0;
			int entities = 0;
			int te = 0;
			int worlds = 0;

			for (WorldServer world : CoreProps.server.worldServers) {
				loadedChunks += world.getChunkProvider().getLoadedChunkCount();
				entities += world.loadedEntityList.size();
				te += world.loadedTileEntityList.size();
				worlds += 1;
			}
			sender.addChatMessage(new TextComponentString("Total Loaded Worlds/Chunks: " + worlds + "/" + loadedChunks));
			sender.addChatMessage(new TextComponentString("Total Entities/TileEntities: " + entities + "/" + te));
		} else {
			int dim = 0;
			try {
				dim = CommandBase.parseInt(arguments[1]);
			} catch (Throwable e) {
				sender.addChatMessage(new TextComponentTranslation("chat.cofh.command.syntaxError"));
				sender.addChatMessage(new TextComponentTranslation("chat.cofh.command." + getCommandName() + ".syntax"));
				Throwables.propagate(e);
			}

			WorldServer world = CoreProps.server.worldServerForDimension(dim);
			if (world == null) {
				throw new CommandException("chat.cofh.command.world.notFound");
			}

			double tickms = getTickMs(world);
			double tps = getTps(world);

			sender.addChatMessage(new TextComponentString("World " + world.provider.getDimension() + ": " + world.provider.getDimensionType().getName() + " - Loaded chunks: " + world.getChunkProvider().getLoadedChunkCount()));
			sender.addChatMessage(new TextComponentString("TPS: " + floatfmt.format(tps) + "/" + floatfmt.format(20L) + " TPS (" + (int) (tps / 20.0D * 100.0D) + "%) - Tick: " + floatfmt.format(tickms) + " ms of " + floatfmt.format(50L) + " ms"));
			sender.addChatMessage(new TextComponentString("Entities: " + world.loadedEntityList.size() + " - Tile entities: " + world.loadedTileEntityList.size()));
		}
	}

	@Override
	public List<String> addTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args) {

		if (args.length == 2) {
			List<String> worldIDs = new ArrayList<>();
			worldIDs.add("o");
			worldIDs.add("a");
			for (World world : CoreProps.server.worldServers) {
				worldIDs.add(Integer.toString(world.provider.getDimension()));
			}
			return CommandBase.getListOfStringsMatchingLastWord(args, worldIDs.toArray(new String[] { "" }));
		}
		return null;
	}

}
