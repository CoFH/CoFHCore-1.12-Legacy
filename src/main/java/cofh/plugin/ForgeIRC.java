package cofh.plugin;

import cofh.CoFHCore;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.StringHelper;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.world.World;

public class ForgeIRC {

	public static List<String> onMessage(String n, String u, String h, String d, String m) {

		// n = colorNick(n, u, h);
		if (CoreUtils.isServer()) {
			String[] v = m.split(" ", 3);
			if (v[0].equals("!tps")) {
				return commandTps(v);
			}
		}
		return null;
	}

	static String colorNick(String n, String u, String h) {

		if (n.toLowerCase().equals("zeldokavira") || n.toLowerCase().equals("zeldo")) {
			return StringHelper.WHITE + n + StringHelper.END;
		}
		if (n.toLowerCase().equals("kinglemmingcofh") || n.toLowerCase().equals("kinglemming") || n.toLowerCase().equals("king_lemming")) {
			return StringHelper.BLUE + n + StringHelper.END;
		}
		if (n.toLowerCase().equals("jadedcat")) {
			return StringHelper.PURPLE + n + StringHelper.END;
		}
		if (n.toLowerCase().equals("morvelaira")) {
			return StringHelper.PINK + n + StringHelper.END;
		}
		return n;
	}

	public static List<String> onMessage(String s, String d, String m) {

		return null;
	}

	/* TPS */
	private static DecimalFormat floatfmt = new DecimalFormat("##0.00");

	// private static final int MAX_TPS = 20;
	// private static final int MIN_TICKMS = 50;

	public static List<String> commandTps(String[] arguments) {

		LinkedList<String> toReturn = new LinkedList<String>();
		if (arguments.length < 2) {
			double tps = getTps(null);
			double tickms = getTickMs(null);

			toReturn.add("Overall: " + floatfmt.format(tps) + " TPS/" + floatfmt.format(tickms) + "MS (" + (int) (tps / 20.0D * 100.0D) + "%)");

			for (World world : CoFHCore.server.worldServers) {
				tps = getTps(world);
				tickms = getTickMs(world);
				toReturn.add(world.provider.getDimensionName() + " [" + world.provider.dimensionId + "]: " + floatfmt.format(tps) + " TPS/"
						+ floatfmt.format(tickms) + "MS (" + (int) (tps / 20.0D * 100.0D) + "%)");
			}
		} else if (arguments[1].toLowerCase().charAt(0) == 'o') {
			double tickms = getTickMs(null);
			double tps = getTps(null);

			toReturn.add("Overall server tick");
			toReturn.add(StringHelper.END + StringHelper.END + StringHelper.END + "TPS: " + floatfmt.format(tps) + " TPS of " + floatfmt.format(20L) + " TPS ("
					+ (int) (tps / 20.0D * 100.0D) + "%)");
			toReturn.add(StringHelper.END + StringHelper.END + StringHelper.END + "Tick time: " + floatfmt.format(tickms) + " ms of " + floatfmt.format(50L)
					+ " ms");
		} else if (arguments[1].toLowerCase().charAt(0) == 'a') {
			double tickms = getTickMs(null);
			double tps = getTps(null);

			toReturn.add("Overall server tick");
			toReturn.add(StringHelper.END + StringHelper.END + StringHelper.END + "TPS: " + floatfmt.format(tps) + " TPS of " + floatfmt.format(20L) + " TPS ("
					+ (int) (tps / 20.0D * 100.0D) + "%)");
			toReturn.add(StringHelper.END + StringHelper.END + StringHelper.END + "Tick time: " + floatfmt.format(tickms) + " ms of " + floatfmt.format(50L)
					+ " ms");
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
			toReturn.add(StringHelper.END + StringHelper.END + StringHelper.END + "Total Loaded Worlds/Chunks: " + worlds + "/" + loadedChunks);
			toReturn.add(StringHelper.END + StringHelper.END + StringHelper.END + "Total Entities/TileEntities: " + entities + "/" + te);
		} else {
			int dim = 0;
			try {
				dim = Integer.parseInt(arguments[1]);
			} catch (Throwable e) {
				toReturn.add("");
				return toReturn;
			}

			World world = CoFHCore.server.worldServerForDimension(dim);
			if (world == null) {
				throw new PlayerNotFoundException("World not found", new Object[0]);
			}

			double tickms = getTickMs(world);
			double tps = getTps(world);

			toReturn.add("World " + world.provider.dimensionId + ": " + world.provider.getDimensionName() + " - Loaded chunks: "
					+ world.getChunkProvider().getLoadedChunkCount());
			toReturn.add(StringHelper.END + StringHelper.END + StringHelper.END + "TPS: " + floatfmt.format(tps) + "/" + floatfmt.format(20L) + " TPS ("
					+ (int) (tps / 20.0D * 100.0D) + "%) - Tick: " + floatfmt.format(tickms) + " ms of " + floatfmt.format(50L) + " ms");
			toReturn.add(StringHelper.END + StringHelper.END + StringHelper.END + "Entities: " + world.loadedEntityList.size() + " - Tile entities: "
					+ world.loadedTileEntityList.size());
		}
		return toReturn;
	}

	private static double getTickTimeSum(long[] times) {

		long timesum = 0L;
		if (times == null) {
			return 0.0D;
		}
		for (int i = 0; i < times.length; i++) {
			timesum += times[i];
		}

		return timesum / times.length;
	}

	private static double getTickMs(World world) {

		return getTickTimeSum(world == null ? CoFHCore.server.tickTimeArray : (long[]) CoFHCore.server.worldTickTimes.get(Integer
				.valueOf(world.provider.dimensionId))) * 1.0E-006D;
	}

	private static double getTps(World world) {

		double tps = 1000.0D / getTickMs(world);
		return tps > 20.0D ? 20.0D : tps;
	}
	/* END TPS */
}
