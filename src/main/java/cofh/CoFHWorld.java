package cofh;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cofh.api.world.WeightedRandomBlock;
import cofh.core.CoFHProps;
import cofh.util.ConfigHandler;
import cofh.util.CoreUtils;
import cofh.util.MathHelper;
import cofh.util.StringHelper;
import cofh.world.TickHandlerWorld;
import cofh.world.WorldHandler;
import cofh.world.feature.FeatureOreGenNormal;
import cofh.world.feature.FeatureOreGenUniform;
import cofh.world.feature.WorldGenMinableCluster;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "CoFHWorld", name = "CoFH World", version = CoFHProps.VERSION, dependencies = "required-after:CoFHCore@[" + CoFHProps.VERSION + ",)")
public class CoFHWorld {

	@Instance("CoFHWorld")
	public static CoFHWorld instance;

	/* INIT SEQUENCE */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		convertLegacyConfig(false);

		config.setConfiguration(new Configuration(new File(CoFHProps.configDir, "/cofh/CoFHWorld.cfg")));
		configGeneration.setConfiguration(new Configuration(new File(CoFHProps.configDir, "/cofh/CoFHWorld-Generation.cfg")));

		String category = "feature";
		String comment = null;

		comment = "This allows for custom generation to be specified in the WorldCustomGen.txt file.";
		allowCustomGen = config.get(category, "AllowCustomGeneration", false, comment);

		comment = "This allows for vanilla ore generation to be REPLACED. Configure in the 'world.vanilla' section of the CoFHWorld-Generation.cfg; vanilla defaults have been provided.";
		genReplaceVanilla = config.get(category, "ReplaceVanillaGeneration", false, comment);

		comment = "This will flatten the bedrock layer.";
		genFlatBedrock = config.get(category, "FlatBedrock", false, comment);

		comment = "The number of layers of bedrock to flatten to. (Max: " + MAX_BEDROCK_LAYERS + ")";
		layersBedrock = config.get(category, "FlatBedrockLayers", 1, comment);
		layersBedrock = MathHelper.clampI(layersBedrock, 1, MAX_BEDROCK_LAYERS);

		comment = "If FlatBedrock is enabled, this will enforce it in previously generated chunks.";
		retroFlatBedrock = config.get(category, "RetroactiveFlatBedrock", false, comment);

		comment = "This will retroactively generate ores in previously generated chunks.";
		retroOreGeneration = config.get(category, "RetroactiveOreGeneration", false, comment);

		loadWorldGeneration();
		GameRegistry.registerWorldGenerator(WorldHandler.instance, 0);
		MinecraftForge.EVENT_BUS.register(WorldHandler.instance);
		MinecraftForge.ORE_GEN_BUS.register(WorldHandler.instance);
		// MinecraftForge.TERRAIN_GEN_BUS.register(WorldHandler.instance);

		convertLegacyConfig(true);
		config.save();
	}

	@EventHandler
	public void initialize(FMLInitializationEvent event) {

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		parseCustomGeneration();

		if (genFlatBedrock && retroFlatBedrock || retroOreGeneration) {
			MinecraftForge.EVENT_BUS.register(TickHandlerWorld.instance);
		}
		// config.cleanUp(false, true);
	}

	void loadWorldGeneration() {

		String category = "world.vanilla";
		ConfigCategory cat = configGeneration.getCategory(category);
		cat.setComment("This section controls generation specifically related to vanilla Minecraft ores. THESE VALUES ARE USED INSTEAD OF STANDARD GENERATION IF REPLACEMENT IS ENABLED.");

		addFeature("world.vanilla", new ItemStack(Blocks.dirt), "dirt", 32, 20, 0, 128, ORE_UNIFORM, true, genReplaceVanilla);
		addFeature("world.vanilla", new ItemStack(Blocks.gravel), "gravel", 32, 10, 0, 128, ORE_UNIFORM, true, genReplaceVanilla);
		addFeature("world.vanilla", new ItemStack(Blocks.coal_ore), "coal", 16, 20, 0, 128, ORE_UNIFORM, true, genReplaceVanilla);
		addFeature("world.vanilla", new ItemStack(Blocks.iron_ore), "iron", 8, 20, 0, 64, ORE_UNIFORM, true, genReplaceVanilla);
		addFeature("world.vanilla", new ItemStack(Blocks.gold_ore), "gold", 8, 2, 0, 32, ORE_UNIFORM, true, genReplaceVanilla);
		addFeature("world.vanilla", new ItemStack(Blocks.redstone_ore), "redstone", 7, 8, 0, 16, ORE_UNIFORM, true, genReplaceVanilla);
		addFeature("world.vanilla", new ItemStack(Blocks.diamond_ore), "diamond", 7, 1, 0, 16, ORE_UNIFORM, true, genReplaceVanilla);
		addFeature("world.vanilla", new ItemStack(Blocks.lapis_ore), "lapis", 6, 1, 16, 16, ORE_NORMAL, true, genReplaceVanilla);

		configGeneration.save();
	}

	void parseCustomGeneration() {

		customGen = new File(CoFHProps.configDir, "/cofh/WorldCustomGen.txt");
		try {
			if (!customGen.exists()) {
				customGen.createNewFile();
				Files.write((CUSTOM_GEN_TEMPLATE + CUSTOM_GEN_FORMAT + CUSTOM_GEN_UNIFORM + CUSTOM_GEN_NORMAL).getBytes(), customGen);
			}
			List<String> lines = Files.readLines(customGen, Charsets.UTF_8);

			String[] tokens;
			String genType;
			@SuppressWarnings("unused")
			String name;
			int oreId;
			int oreMeta;
			int clusterSize;
			int numClusters;
			int minY;
			int maxY;
			boolean regen;
			int type;

			for (String line : lines) {
				if (line.startsWith("#")) {
					continue;
				}
				tokens = line.split("\t");

				if (tokens.length != 9) {
					continue;
				}
				try {
					genType = tokens[0].toLowerCase(Locale.ENGLISH);
					name = tokens[1].toLowerCase(Locale.ENGLISH);
					oreId = Integer.valueOf(tokens[2]);
					oreMeta = Integer.valueOf(tokens[3]);
					clusterSize = Integer.valueOf(tokens[4]);
					numClusters = Integer.valueOf(tokens[5]);
					minY = Integer.valueOf(tokens[6]);
					maxY = Integer.valueOf(tokens[7]);
					regen = Boolean.valueOf(tokens[8]);

					if (genType.equals("uniform")) {
						type = ORE_UNIFORM;
					} else if (genType.equals("normal")) {
						type = ORE_NORMAL;
					} else {
						log.log(Level.ERROR, "The WorldCustomGen.txt file has an invalid entry: '" + tokens[1] + "'.");
						continue;
					}
					if (oreId >= 4096) {
						log.log(Level.ERROR, "The WorldCustomGen.txt file has an invalid entry: '" + tokens[1] + "'. The ID is too high!");
					} else {
						addCustomFeature(oreId, oreMeta, tokens[1], clusterSize, numClusters, minY, maxY, type, regen, true);
					}
				} catch (NumberFormatException e) {
					log.log(Level.ERROR, "The WorldCustomGen.txt file has an invalid entry: '" + tokens[1] + "'.");
				}
			}
		} catch (IOException e) {
			log.log(Level.ERROR, "The WorldCustomGen.txt file could not be read! Skipping custom generation.");
		}
	}

	@Deprecated
	// until converted to strings so i know where it's used
	public static void addCustomFeature(int oreId, int oreMeta, String featureName, int clusterSize, int numClusters, int minY, int maxY, int feature,
			boolean regen, boolean enable) { // TODO: convert this to strings

		if (registeredFeatureNames.contains(featureName)) {
			log.log(Level.ERROR, "There is a duplicate feature entry name: '" + featureName + "' - only the first one will be used.");
			return;
		}
		if (true) {// Block.blocksList[oreId] == null) {
			log.log(Level.ERROR, "The entry for custom ore '" + featureName + "' is invalid - the block is null.");
			return;
		}
		@SuppressWarnings("unused")
		String category = "world.custom." + featureName.toLowerCase(Locale.ENGLISH);

		int bId = configGeneration.get(category, "BlockId", oreId);
		int bMeta = configGeneration.get(category, "BlockMeta", oreMeta);

		// addFeature("world.custom", new ItemStack(bId, 1, bMeta), featureName, clusterSize, numClusters, minY, maxY, feature, regen, enable);
	}

	public static void addFeature(String category, ItemStack stack, String featureName, int clusterSize, int numClusters, int minY, int maxY, int feature,
			boolean regen, boolean enable) {

		List<WeightedRandomBlock> resList = new ArrayList<WeightedRandomBlock>();
		resList.add(new WeightedRandomBlock(stack));
		addFeature(category, resList, featureName, clusterSize, numClusters, minY, maxY, feature, regen, enable);
	}

	public static void addFeature(String category, List<WeightedRandomBlock> resList, String featureName, int clusterSize, int numClusters, int minY, int maxY,
			int feature, boolean regen, boolean enable) {

		category += "." + featureName.toLowerCase(Locale.ENGLISH);
		ConfigCategory cat = configGeneration.getCategory(category);

		String featureType = "<UNIFORM>";
		String strMin = "MinY";
		String strMax = "MaxY";

		if (feature == ORE_NORMAL) {
			featureType = "<NORMAL>";
			strMin = "MeanY";
			strMax = "MaxVar";
		}
		cat.setComment(featureType + " Generation settings for " + StringHelper.titleCase(featureName) + "; Defaults: ClusterSize = " + clusterSize
				+ ", NumClusters = " + numClusters + ", " + strMin + " = " + minY + ", " + strMax + " = " + maxY);

		clusterSize = configGeneration.get(category, "ClusterSize", clusterSize);
		numClusters = configGeneration.get(category, "NumClusters", numClusters);
		minY = configGeneration.get(category, strMin, minY);
		maxY = configGeneration.get(category, strMax, maxY);
		regen = configGeneration.get(category, "RetroGen", regen);

		configGeneration.save();

		if (!enable) {
			return;
		}
		if (feature == ORE_UNIFORM) {
			WorldHandler.addFeature(new FeatureOreGenUniform(featureName, new WorldGenMinableCluster(resList, clusterSize), numClusters, minY, maxY, regen));
		} else if (feature == ORE_NORMAL) {
			WorldHandler.addFeature(new FeatureOreGenNormal(featureName, new WorldGenMinableCluster(resList, clusterSize), numClusters, minY, maxY, regen));
		}
	}

	public static void convertLegacyConfig(boolean stage) {

		if (!stage) {
			File oldFile = new File(CoFHProps.configDir, "/cofh/World.cfg");
			if (oldFile.exists()) {
				try {
					CoreUtils.copyFileUsingChannel(oldFile, new File(CoFHProps.configDir, "/cofh/CoFHWorld-Generation.cfg"));
				} catch (IOException e) {

				}
				oldFile.renameTo(new File(CoFHProps.configDir, "/cofh/CoFHWorld.cfg"));
			}
			return;
		}
		config.getCategory("world.feature").remove("Vanilla.Augment");
		config.removeCategory("world.feature");
		config.removeCategory("world.tweak");
		config.removeCategory("world");

		configGeneration.removeCategory("feature");
	}

	public static final int MAX_BEDROCK_LAYERS = 5;

	public static final int ORE_UNIFORM = 0;
	public static final int ORE_NORMAL = 1;

	public static final String CUSTOM_GEN_TEMPLATE = "#This file allows for the addition of custom generation TEMPLATES to World.cfg.\n#Generation parameters are changed inside World.cfg.\n#These values are only used as defaults and in the template comments.\n#\n";
	public static final String CUSTOM_GEN_FORMAT = "#Format (TAB DELIMITED): GenType\tName\tBlockID\tBlockMeta\tClusterSize\tNumClusters\tminY\tmaxY\tregen\n#GenType is either UNIFORM or NORMAL. If NORMAL, minY is the average height and maxY is the maximum variance.\n#\n";
	public static final String CUSTOM_GEN_UNIFORM = "#Creates a template with default values of 4 clusters of 4 ores (1000:0) randomly between y = 32 and y = 64. Retrogen Enabled:\n#UNIFORM\ttestUniform\t1000\t0\t4\t4\t32\t64\ttrue\n#\n";
	public static final String CUSTOM_GEN_NORMAL = "#Creates a template with default values of 6 clusters of 16 ores (1000:1) in a 32-height Normal Distribution centered at y = 32. RetroGen Disabled:\n#NORMAL\ttestNormal\t1000\t1\t16\t6\t32\t16\tfalse\n#\n";

	public static final Logger log = LogManager.getLogger("CoFHWorld");
	public static ConfigHandler config = new ConfigHandler(CoFHProps.VERSION);
	public static ConfigHandler configGeneration = new ConfigHandler(CoFHProps.VERSION);
	public static File customGen;

	static {
		// log.setParent(FMLLog.getLogger());
		// TODO: set parent?
	}

	public static boolean allowCustomGen = false;
	public static boolean genFlatBedrock = false;
	public static boolean genReplaceVanilla = false;
	public static boolean retroFlatBedrock = false;
	public static boolean retroOreGeneration = false;
	public static boolean forceFullRegeneration = false;

	public static int layersBedrock = 1;

	public static ArrayList<String> registeredFeatureNames = new ArrayList<String>();

}
