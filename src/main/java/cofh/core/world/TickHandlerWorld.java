package cofh.core.world;

import cofh.CoFHCore;
import cofh.lib.util.position.ChunkCoord;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayDeque;
import java.util.Random;

import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class TickHandlerWorld {

	public static TickHandlerWorld instance = new TickHandlerWorld();

	public static TIntObjectHashMap<ArrayDeque<RetroChunkCoord>> chunksToGen = new TIntObjectHashMap<ArrayDeque<RetroChunkCoord>>();
	public static TIntObjectHashMap<ArrayDeque<ChunkCoord>> chunksToPreGen = new TIntObjectHashMap<ArrayDeque<ChunkCoord>>();

	// FIXME: put adding to these behind a function so we can remove the tick handler when there's nothing to do
	// size of the maps indicates how many dimensions are needing to gen/pregen, and will be 0 when no work is required

	private static byte pregenC, retroC;
	@SubscribeEvent
	public void tickEnd(WorldTickEvent event) {

		if (event.side != Side.SERVER) {
			return;
		}
		World world = event.world;
		int dim = world.provider.getDimension();

		if (event.phase == Phase.END) {
			ArrayDeque<RetroChunkCoord> chunks = chunksToGen.get(dim);

			if (chunks != null && chunks.size() > 0) {
				RetroChunkCoord r = chunks.pollFirst();
				ChunkCoord c = r.coord;
				if (retroC++ == 0 || chunks.size() < 3) {
					CoFHCore.log.info("RetroGening " + c.toString() + ".");
				} else {
					CoFHCore.log.debug("RetroGening " + c.toString() + ".");
				}
				retroC &= 31;
				long worldSeed = world.getSeed();
				Random rand = new Random(worldSeed);
				long xSeed = rand.nextLong() >> 2 + 1L;
				long zSeed = rand.nextLong() >> 2 + 1L;
				rand.setSeed(xSeed * c.chunkX + zSeed * c.chunkZ ^ worldSeed);
				WorldHandler.instance.generateWorld(rand, r, world, false);
				chunksToGen.put(dim, chunks);
			} else if (chunks != null) {
				chunksToGen.remove(dim);
			}
		} else {
			ArrayDeque<ChunkCoord> chunks = chunksToPreGen.get(dim);

			if (chunks != null && chunks.size() > 0) {
				ChunkCoord c = chunks.pollFirst();
				if (pregenC++ == 0 || chunks.size() < 5) {
					CoFHCore.log.info("PreGening " + c.toString() + ".");
				} else {
					CoFHCore.log.debug("PreGening " + c.toString() + ".");
				}
				pregenC &= 31;
				world.getChunkFromChunkCoords(c.chunkX, c.chunkZ);
			} else if (chunks != null) {
				chunksToPreGen.remove(dim);
			}
		}
	}

	public static class RetroChunkCoord {

		private static final THashSet<String> emptySet = new THashSet<String>(0);
		public final ChunkCoord coord;
		public final THashSet<String> generatedFeatures;

		public RetroChunkCoord(ChunkCoord pos, NBTTagList features) {

			coord = pos;
			if (features == null) {
				generatedFeatures = emptySet;
			} else {
				int i = 0, e = features.tagCount();
				generatedFeatures = new THashSet<String>(e);
				for (; i < e; ++i) {
					generatedFeatures.add(features.getStringTagAt(i));
				}
			}
		}

		@Override
		public boolean equals(Object o) {

			if (o instanceof RetroChunkCoord) {
				return ((RetroChunkCoord) o).coord.equals(coord);
			} else if (o instanceof ChunkCoord) {
				return o.equals(coord);
			}
			return false;
		}

		@Override
		public int hashCode() {

			return coord.hashCode();
		}
	}

}
