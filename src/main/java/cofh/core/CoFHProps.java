package cofh.core;

import com.mojang.authlib.GameProfile;

import java.io.File;
import java.util.UUID;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTankInfo;

public class CoFHProps {

	private CoFHProps() {

	}

	private static final String BUILD = "2094";
	public static final String FORGE_REQ = "12.18.1." + BUILD;
	public static final String FORGE_REQ_MAX = "12.20";

	public static final String FORGE_DEP = "required-after:Forge@[" + FORGE_REQ + "," + FORGE_REQ_MAX + ");";

	public static final String DOWNLOAD_URL = "http://teamcofh.com/downloads/";

	public static File configDir = null;

	/* Global Constants */
	public static final GameProfile DEFAULT_OWNER = new GameProfile(UUID.fromString("1ef1a6f0-87bc-4e78-0a0b-c6824eb787ea"), "[None]");;

	public static final int TIME_CONSTANT = 32;
	public static final int TIME_CONSTANT_HALF = TIME_CONSTANT / 2;
	public static final int TIME_CONSTANT_QUARTER = TIME_CONSTANT / 4;
	public static final int TIME_CONSTANT_EIGHTH = TIME_CONSTANT / 8;
	public static final int RF_PER_MJ = 10;
	public static final int LAVA_RF = 200000;
	public static final int ENTITY_TRACKING_DISTANCE = 64;

	public static final int[] STORAGE_SIZE = { 1, 9, 18, 27, 36, 45, 54, 63, 72, 80, 88, 96, 104 };
	public static final int[][] SLOTS = new int[STORAGE_SIZE.length][];

	static {
		for (int i = 0; i < STORAGE_SIZE.length; i++) {
			SLOTS[i] = new int[CoFHProps.STORAGE_SIZE[i]];
			for (int j = 0; j < CoFHProps.STORAGE_SIZE[i]; j++) {
				SLOTS[i][j] = j;
			}
		}
	}

	/* Dummy Inventories */
	public static int[] EMPTY_INVENTORY = new int[] {};
	public static FluidTankInfo[] EMPTY_TANK_INFO = new FluidTankInfo[] {};

	/* Graphics */
	public static final String PATH_GFX = "cofh:textures/";
	public static final String PATH_ENTITY = PATH_GFX + "entity/";
	public static final String PATH_GUI = PATH_GFX + "gui/";
	public static final String PATH_GUI_STORAGE = PATH_GUI + "storage/";

	public static final ResourceLocation[] TEXTURE_STORAGE = new ResourceLocation[STORAGE_SIZE.length];

	static {
		for (int i = 0; i < STORAGE_SIZE.length; i++) {
			TEXTURE_STORAGE[i] = new ResourceLocation(PATH_GUI_STORAGE + "storage" + STORAGE_SIZE[i] + ".png");
		}
	}

	/* Network */
	public static final int NETWORK_UPDATE_RANGE = 192;

	/* Audio */
	public static float soundVolume = 1;

	/* Options */
	public static int treeGrowthChance = 1;

	public static boolean enableUpdateNotice = true;
	public static boolean enableDebugOutput = false;
	public static boolean enableDismantleLogging = false;
	public static boolean enableOpSecureAccess = false;
	public static boolean enableOpSecureAccessWarning = true;

	public static boolean enableItemStacking = true;
	public static boolean enableLivingEntityDeathMessages = true;
	public static boolean enableRenderSorting = true;
	public static boolean enableAnimatedTextures = true;

	public static boolean enableShaderEffects = true;

	public static boolean enableColorBlindTextures = false;
	public static boolean enableGUISlotBorders = true;

	/* Holidays */
	public static boolean holidayAprilFools = true;
	public static boolean holidayChristmas = true;

}
