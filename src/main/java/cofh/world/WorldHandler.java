package cofh.world;

import cofh.CoFHCore;
import cofh.api.world.IFeatureGenerator;
import cofh.api.world.IFeatureHandler;
import cofh.util.MathHelper;
import cofh.util.position.ChunkCoord;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;

import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.event.world.ChunkDataEvent;

public class WorldHandler implements IWorldGenerator, IFeatureHandler {

	private static List<IFeatureGenerator> features = new ArrayList<IFeatureGenerator>();
	private static Set<String> featureNames = new THashSet<String>();
	private static Set<EventType> vanillaGenEvents = new THashSet<EventType>();

	private static long genHash = 0;

	public static final int MAX_BEDROCK_LAYERS = 8;

	public static boolean allowCustomGen = false;
	public static boolean genFlatBedrock = false;
	public static boolean genReplaceVanilla = false;
	public static boolean retroFlatBedrock = false;
	public static boolean retroGeneration = false;
	public static boolean forceFullRegeneration = false;

	public static ArrayList<String> registeredFeatureNames = new ArrayList<String>();

	public static int layersBedrock = 1;

	public static WorldHandler instance = new WorldHandler();

	static {
		vanillaGenEvents.add(OreGenEvent.GenerateMinable.EventType.COAL);
		vanillaGenEvents.add(OreGenEvent.GenerateMinable.EventType.DIAMOND);
		vanillaGenEvents.add(OreGenEvent.GenerateMinable.EventType.DIRT);
		vanillaGenEvents.add(OreGenEvent.GenerateMinable.EventType.GOLD);
		vanillaGenEvents.add(OreGenEvent.GenerateMinable.EventType.GRAVEL);
		vanillaGenEvents.add(OreGenEvent.GenerateMinable.EventType.IRON);
		vanillaGenEvents.add(OreGenEvent.GenerateMinable.EventType.LAPIS);
		vanillaGenEvents.add(OreGenEvent.GenerateMinable.EventType.REDSTONE);
		vanillaGenEvents.add(OreGenEvent.GenerateMinable.EventType.QUARTZ);
	}

	public static void initialize() {

		String category = "feature";
		String comment = null;

		comment = "This allows for vanilla Minecraft ore generation to be REPLACED. Configure in the Vanilla.json file; vanilla defaults have been provided. If you rename the Vanilla.json file, this option WILL NOT WORK.";
		genReplaceVanilla = CoFHCore.configCore.get(category, "ReplaceVanillaGeneration", false, comment);

		comment = "This will flatten the bedrock layer.";
		genFlatBedrock = CoFHCore.configCore.get(category, "FlatBedrock", false, comment);

		comment = "The number of layers of bedrock to flatten to. (Max: " + MAX_BEDROCK_LAYERS + ")";
		layersBedrock = CoFHCore.configCore.get(category, "FlatBedrockLayers", 1, comment);
		layersBedrock = MathHelper.clampI(layersBedrock, 1, MAX_BEDROCK_LAYERS);

		comment = "If FlatBedrock is enabled, this will enforce it in previously generated chunks.";
		retroFlatBedrock = CoFHCore.configCore.get(category, "RetroactiveFlatBedrock", false, comment);

		comment = "This will retroactively generate ores in previously generated chunks.";
		retroGeneration = CoFHCore.configCore.get(category, "RetroactiveOreGeneration", false, comment);

		GameRegistry.registerWorldGenerator(instance, 0);
		MinecraftForge.EVENT_BUS.register(instance);
		MinecraftForge.ORE_GEN_BUS.register(instance);

		if (genFlatBedrock && retroFlatBedrock || retroGeneration) {
			MinecraftForge.EVENT_BUS.register(TickHandlerWorld.instance);
		}
	}

	private WorldHandler() {

	}

	@SubscribeEvent
	public void handleChunkSaveEvent(ChunkDataEvent.Save event) {

		NBTTagCompound tag = new NBTTagCompound();

		if (retroFlatBedrock && genFlatBedrock) {
			tag.setBoolean("Bedrock", true);
		}
		if (retroGeneration) {
			for (int i = 0; i < features.size(); i++) {
				tag.setBoolean(features.get(i).getFeatureName(), true);
			}
			tag.setLong("Features", genHash);
		}
		event.getData().setTag("CoFHWorld", tag);
	}

	@SubscribeEvent
	public void handleChunkLoadEvent(ChunkDataEvent.Load event) {

		int dim = event.world.provider.dimensionId;

		boolean bedrock = false;
		boolean features = false;
		boolean regen = false;
		NBTTagCompound tag = (NBTTagCompound) event.getData().getTag("CoFHCore");

		if (tag != null) {
			bedrock = !tag.hasKey("Bedrock") && retroFlatBedrock && genFlatBedrock;
			features = tag.getLong("Features") != genHash && retroGeneration;
		}
		ChunkCoord cCoord = new ChunkCoord(event.getChunk());

		if (tag == null && (retroFlatBedrock && genFlatBedrock || retroGeneration)) {
			regen = true;
		}
		if (bedrock) {
			CoFHCore.log.info("Retroactively flattening bedrock for the chunk at " + cCoord.toString() + ".");
			regen = true;
		}
		if (features) {
			CoFHCore.log.info("Retroactively generating features for the chunk at " + cCoord.toString() + ".");
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

		if (!genReplaceVanilla) {
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

		replaceBedrock(random, chunkX, chunkZ, world, newGen | forceFullRegeneration);

		if (!newGen && !retroGeneration) {
			return;
		}
		for (IFeatureGenerator feature : features) {
			feature.generateFeature(random, chunkX, chunkZ, world, newGen | forceFullRegeneration);
		}
		if (!newGen) {
			world.getChunkFromChunkCoords(chunkX, chunkZ).setChunkModified();
		}
	}

	public void replaceBedrock(Random random, int chunkX, int chunkZ, World world, boolean newGen) {

		if (!genFlatBedrock || !newGen && !retroFlatBedrock) {
			return;
		}
		Block filler = world.getBiomeGenForCoords(chunkX, chunkZ).fillerBlock;

		int offsetX = chunkX * 16;
		int offsetZ = chunkZ * 16;

		for (int blockX = 0; blockX < 16; blockX++) {
			for (int blockZ = 0; blockZ < 16; blockZ++) {
				for (int blockY = 5; blockY > layersBedrock - 1; blockY--) {
					if (world.getBlock(offsetX + blockX, blockY, offsetZ + blockZ) == Blocks.bedrock) {
						world.setBlock(offsetX + blockX, blockY, offsetZ + blockZ, filler, 0, 2);
					}
				}
				for (int blockY = layersBedrock - 1; blockY > 0; blockY--) {
					if (world.getBlock(offsetX + blockX, blockY, offsetZ + blockZ) != Blocks.air) {
						world.setBlock(offsetX + blockX, blockY, offsetZ + blockZ, Blocks.bedrock, 0, 2);
					}
				}
			}
		}
		/* Flatten bedrock on the top as well */
		int worldHeight = world.getActualHeight();

		if (world.getBlock(offsetX, worldHeight - 1, offsetZ) == Blocks.bedrock) {
			/* This is a hack because Mojang coded the Nether wrong. Are you surprised? */
			if (world.provider.dimensionId == 1) {
				filler = Blocks.netherrack;
			}
			for (int blockX = 0; blockX < 16; blockX++) {
				for (int blockZ = 0; blockZ < 16; blockZ++) {
					for (int blockY = worldHeight - 2; blockY > worldHeight - 6; blockY--) {
						if (world.getBlock(offsetX + blockX, blockY, offsetZ + blockZ) == Blocks.bedrock) {
							world.setBlock(offsetX + blockX, blockY, offsetZ + blockZ, filler, 0, 2);
						}
					}
					for (int blockY = worldHeight - layersBedrock; blockY < worldHeight - 1; blockY++) {
						if (world.getBlock(offsetX + blockX, blockY, offsetZ + blockZ) != Blocks.air) {
							world.setBlock(offsetX + blockX, blockY, offsetZ + blockZ, Blocks.bedrock, 0, 2);
						}
					}
				}
			}
		}
	}

}
