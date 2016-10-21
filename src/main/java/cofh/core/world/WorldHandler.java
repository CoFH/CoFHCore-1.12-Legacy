package cofh.core.world;

import cofh.CoFHCore;
import cofh.api.world.IFeatureGenerator;
import cofh.api.world.IFeatureHandler;
import cofh.asmhooks.event.ModPopulateChunkEvent;
import cofh.core.world.TickHandlerWorld.RetroChunkCoord;
import cofh.lib.util.LinkedHashList;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.position.ChunkCoord;
import gnu.trove.set.hash.THashSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.*;

public class WorldHandler implements IWorldGenerator, IFeatureHandler {

    private static List<IFeatureGenerator> features = new ArrayList<IFeatureGenerator>();
    private static Set<String> featureNames = new THashSet<String>();
    private static Set<EventType> vanillaGenEvents = new THashSet<EventType>();
    private static LinkedHashList<ChunkReference> populatingChunks = new LinkedHashList<ChunkReference>();

    private static long genHash = 0;

    public static final int MAX_BEDROCK_LAYERS = 8;

    public static boolean allowCustomGen = false;
    public static boolean genFlatBedrock = false;
    public static boolean genReplaceVanilla = false;
    public static boolean retroFlatBedrock = false;
    public static boolean retroGeneration = false;
    public static boolean forceFullRegeneration = false;

    public static ArrayList<String> registeredFeatureNames = new ArrayList<String>();

    private static final String TAG_NAME = "CoFHWorld";

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

        String category = "World";
        String comment = null;

        comment = "This allows for vanilla Minecraft ore generation to be REPLACED. Configure in the Vanilla.json file; vanilla defaults have been provided. If you rename the Vanilla.json file, this option WILL NOT WORK.";
        genReplaceVanilla = CoFHCore.configCore.get(category, "ReplaceVanillaGeneration", false, comment);

        comment = "This will flatten the bedrock layer.";
        genFlatBedrock = CoFHCore.configCore.get(category, "FlatBedrock", false, comment);

        comment = "The number of layers of bedrock to flatten to. (Max: " + MAX_BEDROCK_LAYERS + ")";
        layersBedrock = CoFHCore.configCore.get(category, "FlatBedrockLayers", 1, comment);
        layersBedrock = MathHelper.clamp(layersBedrock, 1, MAX_BEDROCK_LAYERS);

        comment = "If FlatBedrock is enabled, this will enforce it in previously generated chunks.";
        retroFlatBedrock = CoFHCore.configCore.get(category, "RetroactiveFlatBedrock", false, comment);

        comment = "This will retroactively generate ores in previously generated chunks.";
        retroGeneration = CoFHCore.configCore.get(category, "RetroactiveOreGeneration", false, comment);

        GameRegistry.registerWorldGenerator(instance, 0);
        MinecraftForge.EVENT_BUS.register(instance);
        MinecraftForge.ORE_GEN_BUS.register(instance);

        if (genFlatBedrock & retroFlatBedrock | retroGeneration) {
            // TODO: remove this condition when pregen works? (see handler for alternate)
            FMLCommonHandler.instance().bus().register(TickHandlerWorld.instance);
        }
    }

    public boolean removeFeature(IFeatureGenerator feature) {

        String featureName = feature.getFeatureName();
        if (featureName == null) {
            return false;
        }
        if (featureNames.contains(featureName)) {
            featureNames.remove(featureName);
            features.remove(feature);
            genHash -= featureName.hashCode();
        }

        return true;
    }

    private WorldHandler() {

    }

    @SubscribeEvent
    public void populateChunkEvent(PopulateChunkEvent.Pre event) {

        populatingChunks.add(new ChunkReference(event.getWorld().provider.getDimension(), event.getChunkX(), event.getChunkZ()));
    }

    @SubscribeEvent
    public void populateChunkEvent(ModPopulateChunkEvent.Post event) {

        populatingChunks.remove(new ChunkReference(event.world.provider.getDimension(), event.chunkX, event.chunkZ));
    }

    @SubscribeEvent
    public void handleChunkSaveEvent(ChunkDataEvent.Save event) {

        NBTTagCompound genTag = event.getData().getCompoundTag(TAG_NAME);

        if (populatingChunks.contains(event.getChunk())) {
            genTag.setBoolean("Populating", true);
            return;
        }

        if (genFlatBedrock) {
            genTag.setBoolean("Bedrock", true);
        }
        NBTTagList featureList = new NBTTagList();
        for (int i = 0; i < features.size(); i++) {
            featureList.appendTag(new NBTTagString(features.get(i).getFeatureName()));
        }
        genTag.setTag("List", featureList);
        genTag.setLong("Hash", genHash);

        event.getData().setTag(TAG_NAME, genTag);
    }

    @SubscribeEvent
    public void handleChunkLoadEvent(ChunkDataEvent.Load event) {

        int dim = event.getWorld().provider.getDimension();

        boolean regen = false;
        NBTTagCompound tag = (NBTTagCompound) event.getData().getTag(TAG_NAME);

        if (tag != null && tag.getBoolean("Populating")) {
            populatingChunks.add(new ChunkReference(dim, event.getChunk().xPosition, event.getChunk().zPosition));
            return;
        }

        NBTTagList list = null;
        ChunkCoord cCoord = new ChunkCoord(event.getChunk());

        if (tag != null) {
            boolean genFeatures = false;
            boolean bedrock = retroFlatBedrock & genFlatBedrock && !tag.hasKey("Bedrock");
            if (retroGeneration) {
                genFeatures = tag.getLong("Hash") != genHash;
                if (tag.hasKey("List")) {
                    list = tag.getTagList("List", Constants.NBT.TAG_STRING);
                    genFeatures |= list.tagCount() != features.size();
                }
            }

            if (bedrock) {
                CoFHCore.log.debug("Queuing RetroGen for flattening bedrock for the chunk at " + cCoord.toString() + ".");
                regen = true;
            }
            if (genFeatures) {
                CoFHCore.log.debug("Queuing RetroGen for features for the chunk at " + cCoord.toString() + ".");
                regen = true;
            }
        } else {
            regen = retroFlatBedrock & genFlatBedrock | retroGeneration;
        }

        if (regen) {
            // TODO: this is threaded. should we synchronize?
            ArrayDeque<RetroChunkCoord> chunks = TickHandlerWorld.chunksToGen.get(dim);

            if (chunks == null) {
                TickHandlerWorld.chunksToGen.put(dim, new ArrayDeque<RetroChunkCoord>(128));
                chunks = TickHandlerWorld.chunksToGen.get(dim);
            }
            if (chunks != null) {
                chunks.addLast(new RetroChunkCoord(cCoord, list));
                TickHandlerWorld.chunksToGen.put(dim, chunks);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void handleOreGenEvent(OreGenEvent.GenerateMinable event) {

        if (!genReplaceVanilla) {
            return;
        }
        if (vanillaGenEvents.contains(event.getType())) {
            event.setResult(Result.DENY);
        }
    }

    /* IWorldGenerator */
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

        generateWorld(random, chunkX, chunkZ, world, true);
    }

    /* IFeatureHandler */
    @Override
    public boolean registerFeature(IFeatureGenerator feature) {

        String featureName = feature.getFeatureName();
        if (featureName == null) {
            CoFHCore.log.error("Feature attempted to register without providing a valid name... ignoring.");
            return false;
        }
        if (featureNames.contains(featureName)) {
            CoFHCore.log.debug("Feature " + featureName + " was attempting to register a second time... ignoring.");
            return false;
        }
        featureNames.add(featureName);
        features.add(feature);
        genHash += featureName.hashCode();

        return true;
    }

    /* HELPER FUNCTIONS */
    public static boolean addFeature(IFeatureGenerator feature) {

        return instance.registerFeature(feature);
    }

    public void generateWorld(Random random, int chunkX, int chunkZ, World world, boolean newGen) {

        replaceBedrock(random, chunkX, chunkZ, world, newGen | forceFullRegeneration);

        if (!newGen & !retroGeneration) {
            return;
        }
        for (IFeatureGenerator feature : features) {
            feature.generateFeature(random, chunkX, chunkZ, world, newGen | forceFullRegeneration);
        }
        if (!newGen) {
            world.getChunkFromChunkCoords(chunkX, chunkZ).setChunkModified();
        }
    }

    public void generateWorld(Random random, RetroChunkCoord chunk, World world, boolean newGen) {

        int chunkX = chunk.coord.chunkX, chunkZ = chunk.coord.chunkZ;
        if ((newGen | retroGeneration) & forceFullRegeneration) {
            generateWorld(random, chunkX, chunkZ, world, newGen);
            return;
        }

        replaceBedrock(random, chunkX, chunkZ, world, newGen | forceFullRegeneration);

        if (!newGen & !retroGeneration) {
            return;
        }
        THashSet<String> genned = chunk.generatedFeatures;
        for (IFeatureGenerator feature : features) {
            if (genned.contains(feature.getFeatureName())) {
                continue;
            }
            feature.generateFeature(random, chunkX, chunkZ, world, newGen | forceFullRegeneration);
        }
        if (!newGen) {
            world.getChunkFromChunkCoords(chunkX, chunkZ).setChunkModified();
        }
    }

    public void replaceBedrock(Random random, int chunkX, int chunkZ, World world, boolean newGen) {

        if (!genFlatBedrock | !newGen & !retroFlatBedrock) {
            return;
        }
        int offsetX = chunkX * 16;
        int offsetZ = chunkZ * 16;

		/* Determine if this is a void age; halt if so. */
        boolean isVoidAge = world.isAirBlock(new BlockPos(offsetX, 0, offsetZ));

        if (isVoidAge) {
            return;
        }
        // TODO: pull out the ExtendedStorageArray and edit that directly. faster.
        IBlockState filler = world.getBiomeGenForCoords(new BlockPos(offsetX, 0, offsetZ)).fillerBlock;
        // NOTE: filler block is dirt by default, the actual filler block for the biome is part of a method body
        int meta = 0; // no meta field for filler
        switch (world.provider.getDimension()) {
            case -1:
            /* This is a hack because Mojang coded the Nether wrong. Are you surprised? */
                filler = Blocks.NETHERRACK.getDefaultState();
                break;
            case 0:
			/*
			 * Due to above note, overworld gets replaced with stone. other dimensions are on their own for helping us with the filler block
			 */
                filler = Blocks.STONE.getDefaultState();
                break;
            case 1:
			/* This is a hack because Mojang coded The End wrong. Are you surprised? */
                filler = Blocks.END_STONE.getDefaultState();
                break;
        }
        for (int blockX = 0; blockX < 16; blockX++) {
            for (int blockZ = 0; blockZ < 16; blockZ++) {
                for (int blockY = 5; blockY > layersBedrock - 1; blockY--) {
                    BlockPos pos = new BlockPos(offsetX + blockX, blockY, offsetZ + blockZ);
                    IBlockState state = world.getBlockState(pos);
                    if (state.getBlock().isAssociatedBlock(Blocks.BEDROCK)) {
                        world.setBlockState(pos, filler, 2);
                    }
                }
                for (int blockY = layersBedrock - 1; blockY > 0; blockY--) {
                    BlockPos pos = new BlockPos(offsetX + blockX, blockY, offsetZ + blockZ);
                    IBlockState state = world.getBlockState(pos);
                    if (!state.getBlock().isAssociatedBlock(Blocks.BEDROCK)) {
                        world.setBlockState(pos, Blocks.BEDROCK.getDefaultState(), 2);
                    }
                }
            }
        }
		/* Flatten bedrock on the top as well */
        int worldHeight = world.getActualHeight();

        if (world.getBlockState(new BlockPos(offsetX, worldHeight - 1, offsetZ)).getBlock().isAssociatedBlock(Blocks.BEDROCK)) {
            for (int blockX = 0; blockX < 16; blockX++) {
                for (int blockZ = 0; blockZ < 16; blockZ++) {
                    for (int blockY = worldHeight - 2; blockY > worldHeight - 6; blockY--) {
                        BlockPos pos = new BlockPos(offsetX + blockX, blockY, offsetZ + blockZ);
                        IBlockState state = world.getBlockState(pos);
                        if (state.getBlock().isAssociatedBlock(Blocks.BEDROCK)) {
                            world.setBlockState(pos, filler, 2);
                        }
                    }
                    for (int blockY = worldHeight - layersBedrock; blockY < worldHeight - 1; blockY++) {
                        BlockPos pos = new BlockPos(offsetX + blockX, blockY, offsetZ + blockZ);
                        IBlockState state = world.getBlockState(pos);
                        if (!state.getBlock().isAssociatedBlock(Blocks.BEDROCK)) {
                            world.setBlockState(pos, Blocks.BEDROCK.getDefaultState(), 2);
                        }
                    }
                }
            }
        }
    }

    private static class ChunkReference {

        public final int dimension;
        public final int xPos;
        public final int zPos;

        public ChunkReference(int dim, int x, int z) {

            dimension = dim;
            xPos = x;
            zPos = z;
        }

        @Override
        public int hashCode() {

            return xPos * 43 + zPos * 3 + dimension;
        }

        @Override
        public boolean equals(Object o) {

            if (o == null || o.getClass() != getClass()) {
                if (o instanceof Chunk) {
                    Chunk other = (Chunk) o;
                    return xPos == other.xPosition && zPos == other.zPosition && dimension == other.getWorld().provider.getDimension();
                }
                return false;
            }
            ChunkReference other = (ChunkReference) o;
            return other.dimension == dimension && other.xPos == xPos && other.zPos == zPos;
        }

    }

}
