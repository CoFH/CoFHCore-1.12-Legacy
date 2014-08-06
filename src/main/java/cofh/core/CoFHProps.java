package cofh.core;

import java.io.File;

import net.minecraft.util.ResourceLocation;

public class CoFHProps {

	private CoFHProps() {

	}

	public static final String VERSION = "1.7.10R3.0.0B3";

	private static final String BUILD = "1197";
	public static final String FML_REQ = "7.10.0." + BUILD;
	public static final String FML_REQ_MAX = "7.11";
	public static final String FORGE_REQ = "10.13.0." + BUILD;
	public static final String FORGE_REQ_MAX = "10.14";

	public static final String DEPENDENCIES = "required-after:FML@[" + CoFHProps.FML_REQ + "," + CoFHProps.FML_REQ_MAX + ");" + "required-after:Forge@["
			+ CoFHProps.FORGE_REQ + "," + CoFHProps.FORGE_REQ_MAX + ");";

	public static File configDir = null;

	/* Global Constants */
	public static final String DEFAULT_OWNER = "[None]";

	public static final int TIME_CONSTANT = 32;
	public static final int TIME_CONSTANT_HALF = TIME_CONSTANT / 2;
	public static final int TIME_CONSTANT_QUARTER = TIME_CONSTANT / 4;
	public static final int TIME_CONSTANT_EIGHTH = TIME_CONSTANT / 8;
	public static final int RF_PER_MJ = 10;
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

	/* Graphics */
	public static final String PATH_GFX = "cofh:textures/";
	public static final String PATH_ENTITY = PATH_GFX + "entity/";
	public static final String PATH_GUI = PATH_GFX + "gui/";
	public static final String PATH_GUI_STORAGE = PATH_GUI + "storage/";

	public static final ResourceLocation[] TEXTURE_STORAGE = new ResourceLocation[STORAGE_SIZE.length];

	static {
		for (int i = 0; i < STORAGE_SIZE.length; i++) {
			TEXTURE_STORAGE[i] = new ResourceLocation(PATH_GUI_STORAGE + "Storage" + STORAGE_SIZE[i] + ".png");
		}
	}

	/* Network */
	public static final int NETWORK_UPDATE_RANGE = 192;

	/* Audio */
	public static float soundVolume = 1;

	/* Options */
	public static boolean enableUpdateNotice = true;

	public static boolean enableDismantleLogging = false;
	public static boolean enableOpSecureAccess = false;
	public static boolean enableOpSecureAccessWarning = true;

	public static boolean enableLivingEntityDeathMessages = true;
	public static boolean enableRenderSorting = true;

}
