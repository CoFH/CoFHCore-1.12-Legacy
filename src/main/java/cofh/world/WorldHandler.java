package cofh.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.event.world.ChunkDataEvent;
import cofh.CoFHWorld;
import cofh.api.world.IFeatureGenerator;
import cofh.api.world.IFeatureHandler;
import cofh.util.ChunkCoord;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WorldHandler implements IWorldGenerator, IFeatureHandler {

	private static List<IFeatureGenerator> features = new ArrayList<IFeatureGenerator>();
	private static HashSet<String> featureNames = new HashSet<String>();
	private static HashSet<EventType> vanillaGenEvents = new HashSet<EventType>();
	private static HashSet<Integer> dimensionBlacklist = new HashSet<Integer>();

	private static long genHash = 0;

	static {
		vanillaGenEvents.add(OreGenEvent.GenerateMinable.EventType.COAL);
		vanillaGenEvents.add(OreGenEvent.GenerateMinable.EventType.DIAMOND);
		vanillaGenEvents.add(OreGenEvent.GenerateMinable.EventType.DIRT);
		vanillaGenEvents.add(OreGenEvent.GenerateMinable.EventType.GOLD);
		vanillaGenEvents.add(OreGenEvent.GenerateMinable.EventType.GRAVEL);
		vanillaGenEvents.add(OreGenEvent.GenerateMinable.EventType.IRON);
		vanillaGenEvents.add(OreGenEvent.GenerateMinable.EventType.LAPIS);
		vanillaGenEvents.add(OreGenEvent.GenerateMinable.EventType.REDSTONE);
	}

	public static WorldHandler instance = new WorldHandler();

	@SubscribeEvent
	public void handleChunkSaveEvent(ChunkDataEvent.Save event) {

		NBTTagCompound tag = new NBTTagCompound();

		if (CoFHWorld.retroFlatBedrock && CoFHWorld.genFlatBedrock) {
			tag.setBoolean("Bedrock", true);
		}
		if (CoFHWorld.retroOreGeneration) {
			tag.setLong("Features", genHash);
		}
		event.getData().setTag("CoFHWorld", tag);
	}

	@SubscribeEvent
	public void handleChunkLoadEvent(ChunkDataEvent.Load event) {

		int dim = event.world.provider.dimensionId;

		if (dimensionBlacklist.contains(dim)) {
			return;
		}
		boolean bedrock = false;
		boolean features = false;
		boolean regen = false;
		NBTTagCompound tag = (NBTTagCompound) event.getData().getTag("CoFHWorld");

		if (tag != null) {
			bedrock = !tag.hasKey("Bedrock") && CoFHWorld.retroFlatBedrock && CoFHWorld.genFlatBedrock;
			features = tag.getLong("Features") != genHash && CoFHWorld.retroOreGeneration;
		}
		ChunkCoord cCoord = new ChunkCoord(event.getChunk());

		if (tag == null && (CoFHWorld.retroFlatBedrock && CoFHWorld.genFlatBedrock || CoFHWorld.retroOreGeneration)) {
			regen = true;
		}
		if (bedrock) {
			CoFHWorld.log.info("Retroactively flattening bedrock for the chunk at " + cCoord.toString() + ".");
			regen = true;
		}
		if (features) {
			CoFHWorld.log.info("Retroactively generating features for the chunk at " + cCoord.toString() + ".");
			regen = true;
		}
		if (regen) {
			ArrayList<ChunkCoord> chunks = TickHandlerWorld.chunksToGen.get(Integer.valueOf(dim));

			if (chunks == null) {
				TickHandlerWorld.chunksToGen.put(Integer.valueOf(dim), new ArrayList<ChunkCoord>());
				chunks = TickHandlerWorld.chunksToGen.get(Integer.valueOf(dim));
			}
			if (chunks != null) {
				chunks.add(cCoord);
				TickHandlerWorld.chunksToGen.put(Integer.valueOf(dim), chunks);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void handleOreGenEvent(OreGenEvent.GenerateMinable event) {

		if (!CoFHWorld.genReplaceVanilla) {
			return;
		}
		if (vanillaGenEvents.contains(event.type)) {
			event.setResult(Result.DENY);
		}
	}

	/* IWorldGenerator */
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

		generateWorld(random, chunkX, chunkZ, world, true);
	}

	/* IFeatureHandler */
	@Override
	public boolean registerFeature(IFeatureGenerator feature) {

		if (featureNames.contains(feature.getFeatureName())) {
			return false;
		}
		featureNames.add(feature.getFeatureName());
		features.add(feature);
		genHash += feature.getFeatureName().hashCode();

		return true;
	}

	/* HELPER FUNCTIONS */
	public static boolean addFeature(IFeatureGenerator feature) {

		return instance.registerFeature(feature);
	}

	public void generateWorld(Random random, int chunkX, int chunkZ, World world, boolean newGen) {

		replaceBedrock(random, chunkX, chunkZ, world, newGen);

		if (!newGen && !CoFHWorld.retroOreGeneration) {
			return;
		}
		if (world.provider.dimensionId == 1 || world.provider.dimensionId == -1) {
			return;
		}
		for (IFeatureGenerator feature : features) {
			feature.generateFeature(random, chunkX, chunkZ, world, newGen);
		}
		if (!newGen) {
			world.getChunkFromChunkCoords(chunkX, chunkZ).setChunkModified();
		}
	}

	public void replaceBedrock(Random random, int chunkX, int chunkZ, World world, boolean newGen) {

		if (!CoFHWorld.genFlatBedrock || !newGen && !CoFHWorld.retroFlatBedrock) {
			return;
		}
		Block filler = world.getBiomeGenForCoords(chunkX, chunkZ).fillerBlock;

		for (int blockX = 0; blockX < 16; blockX++) {
			for (int blockZ = 0; blockZ < 16; blockZ++) {
				for (int blockY = CoFHWorld.MAX_BEDROCK_LAYERS; blockY > -1 + CoFHWorld.layersBedrock; blockY--) {
					if (world.getBlock(chunkX * 16 + blockX, blockY, chunkZ * 16 + blockZ) == Blocks.bedrock) {
						world.setBlock(chunkX * 16 + blockX, blockY, chunkZ * 16 + blockZ, filler, 0, 2);
					}
				}
				for (int blockY = CoFHWorld.layersBedrock - 1; blockY > 0; blockY--) {
					if (world.getBlock(chunkX * 16 + blockX, blockY, chunkZ * 16 + blockZ) != Blocks.air) {
						world.setBlock(chunkX * 16 + blockX, blockY, chunkZ * 16 + blockZ, Blocks.bedrock, 0, 2);
					}
				}
			}
		}
	}

}
