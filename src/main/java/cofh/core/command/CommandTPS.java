package cofh.core.command;

import cofh.CoFHCore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class CommandTPS implements ISubCommand {

	public static CommandTPS instance = new CommandTPS();

	private static DecimalFormat floatfmt = new DecimalFormat("##0.00");
	private static final int MAX_TPS = 20;
	private static final int MIN_TICK_MS = 50;

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

		return getTickTimeSum(world == null ? CoFHCore.server.tickTimeArray : (long[]) CoFHCore.server.worldTickTimes.get(Integer
				.valueOf(world.provider.dimensionId))) * 1.0E-006D;
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
	public void handleCommand(ICommandSender sender, String[] arguments) {

		if (arguments.length < 2) {
			double tps = getTps(null);
			double tickms = getTickMs(null);

			sender.addChatMessage(new ChatComponentText("Overall: " + floatfmt.format(tps) + " TPS/" + floatfmt.format(tickms) + "MS ("
					+ (int) (tps / 20.0D * 100.0D) + "%)"));

			for (World world : CoFHCore.server.worldServers) {
				tps = getTps(world);
				tickms = getTickMs(world);
				sender.addChatMessage(new ChatComponentText(world.provider.getDimensionName() + " [" + world.provider.dimensionId + "]: "
						+ floatfmt.format(tps) + " TPS/" + floatfmt.format(tickms) + "MS (" + (int) (tps / 20.0D * 100.0D) + "%)"));
			}
		} else if (arguments[1].toLowerCase().charAt(0) == 'o') {
			double tickms = getTickMs(null);
			double tps = getTps(null);

			sender.addChatMessage(new ChatComponentText("Overall server tick"));
			sender.addChatMessage(new ChatComponentText("TPS: " + floatfmt.format(tps) + " TPS of " + floatfmt.format(20L) + " TPS ("
					+ (int) (tps / 20.0D * 100.0D) + "%)"));
			sender.addChatMessage(new ChatComponentText("Tick time: " + floatfmt.format(tickms) + " ms of " + floatfmt.format(50L) + " ms"));
		} else if (arguments[1].toLowerCase().charAt(0) == 'a') {
			double tickms = getTickMs(null);
			double tps = getTps(null);

			sender.addChatMessage(new ChatComponentText("Overall server tick"));
			sender.addChatMessage(new ChatComponentText("TPS: " + floatfmt.format(tps) + " TPS of " + floatfmt.format(20L) + " TPS ("
					+ (int) (tps / 20.0D * 100.0D) + "%)"));
			sender.addChatMessage(new ChatComponentText("Tick time: " + floatfmt.format(tickms) + " ms of " + floatfmt.format(50L) + " ms"));
			int loadedChunks = 0;
			int entities = 0;
			int te = 0;
			int worlds = 0;

			for (World world : CoFHCore.server.worldServers) {
				loadedChunks += world.getChunkProvider().getLoadedChunkCount();
				entities += world.loadedEntityList.size();
				te += world.loadedTileEntityList.size();
				worlds += 1;
			}
			sender.addChatMessage(new ChatComponentText("Total Loaded Worlds/Chunks: " + worlds + "/" + loadedChunks));
			sender.addChatMessage(new ChatComponentText("Total Entities/TileEntities: " + entities + "/" + te));
		} else {
			int dim = 0;
			try {
				dim = Integer.parseInt(arguments[1]);
			} catch (Throwable e) {
				sender.addChatMessage(new ChatComponentText(CommandHandler.instance.getCommandUsage(null)));
			}

			World world = CoFHCore.server.worldServerForDimension(dim);
			if (world == null) {
				throw new PlayerNotFoundException("World not found", new Object[0]);
			}

			double tickms = getTickMs(world);
			double tps = getTps(world);

			sender.addChatMessage(new ChatComponentText("World " + world.provider.dimensionId + ": " + world.provider.getDimensionName() + " - Loaded chunks: "
					+ world.getChunkProvider().getLoadedChunkCount()));
			sender.addChatMessage(new ChatComponentText("TPS: " + floatfmt.format(tps) + "/" + floatfmt.format(20L) + " TPS (" + (int) (tps / 20.0D * 100.0D)
					+ "%) - Tick: " + floatfmt.format(tickms) + " ms of " + floatfmt.format(50L) + " ms"));
			sender.addChatMessage(new ChatComponentText("Entities: " + world.loadedEntityList.size() + " - Tile entities: " + world.loadedTileEntityList.size()));
		}
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {

		if (args.length == 2) {
			List<String> worldIDs = new ArrayList<String>();
			worldIDs.add("o");
			worldIDs.add("a");
			for (World world : CoFHCore.server.worldServers) {
				worldIDs.add(Integer.toString(world.provider.dimensionId));
			}
			return CommandBase.getListOfStringsMatchingLastWord(args, worldIDs.toArray(new String[] { "" }));
		}
		return null;
	}

}
