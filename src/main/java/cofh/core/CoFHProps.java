package cofh.core;

import java.io.File;

import cofh.util.StringHelper;

public class CoFHProps {

	public static final String NAME = "CoFH Core";
	public static final String VERSION = "1.7.2R2.1.0B1";
	public static final String FORGE_REQ = "10.0.12.1054";
	public static final String RELEASE_URL = "http://teamcofh.com/cofhcore/version/version.txt";

	public static File configDir = null;

	/* Global Constants */
	public static final String DEFAULT_OWNER = "[None]";

	public static final int TIME_CONSTANT = 32;
	public static final int TIME_CONSTANT_HALF = TIME_CONSTANT / 2;
	public static final int TIME_CONSTANT_QUARTER = TIME_CONSTANT / 4;
	public static final int TIME_CONSTANT_EIGHTH = TIME_CONSTANT / 8;
	public static final int RF_PER_MJ = 10;
	public static final int ENTITY_TRACKING_DISTANCE = 64;

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
