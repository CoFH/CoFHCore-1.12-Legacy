package cofh.core;

import cofh.util.StringHelper;

import java.io.File;

public class CoFHProps {

	public static final String VERSION = "1.7.2R3.0.0B1";

	public static final String FML_REQ = "7.2.188.1074";
	public static final String FML_REQ_MAX = "7.3";
	public static final String FORGE_REQ = "10.12.1.1074";
	public static final String FORGE_REQ_MAX = "10.13";

	public static final String DEPENDENCIES = "required-after:FML@[" + CoFHProps.FML_REQ + "," + CoFHProps.FML_REQ_MAX + ");" + "required-after:Forge@["
			+ CoFHProps.FORGE_REQ + "," + CoFHProps.FORGE_REQ_MAX + ")";

	public static File configDir = null;

	/* Global Constants */
	public static final String DEFAULT_OWNER = "[None]";

	public static final int TIME_CONSTANT = 32;
	public static final int TIME_CONSTANT_HALF = TIME_CONSTANT / 2;
	public static final int TIME_CONSTANT_QUARTER = TIME_CONSTANT / 4;
	public static final int TIME_CONSTANT_EIGHTH = TIME_CONSTANT / 8;
	public static final int RF_PER_MJ = 10;
	public static final int ENTITY_TRACKING_DISTANCE = 64;

	/* Graphics */
	public static final String PATH_GFX = "cofh:textures/";
	public static final String PATH_ENTITY = PATH_GFX + "entity/";

	/* Global Localizations */
	public static String tutorialTabConfiguration = StringHelper.localize("info.cofh.tutorial.tabConfiguration");
	public static String tutorialTabOperation = StringHelper.localize("info.cofh.tutorial.tabConfiguration2");
	public static String tutorialTabRedstone = StringHelper.localize("info.cofh.tutorial.tabRedstone");
	public static String tutorialTabFluxRequired = StringHelper.localize("info.cofh.tutorial.fluxRequired");

	/* Network */
	public static final int NETWORK_UPDATE_RANGE = 192;

	/* Options */
	public static boolean enableInformationTabs = true;
	public static boolean enableTutorialTabs = true;
	public static boolean enableUpdateNotice = true;
	public static boolean enableItemPickupModule = true;

	public static boolean enableOpSecureAccess = false;
	public static boolean enableOpSecureAccessWarning = true;

	private CoFHProps() {

	}

}
