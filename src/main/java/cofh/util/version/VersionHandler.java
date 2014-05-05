package cofh.util.version;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLLog;

/**
 * This class allows a mod to easily implement a version update checker.
 * 
 * It also contains some version comparison functions which may be used at any point.
 * 
 * Instances of this class should be registered with {@link TickHandlerVersion}.
 * 
 * @author King Lemming
 * 
 */
public class VersionHandler {

	public static final String MC_VERSION = "1.5.2";

	boolean criticalUpdate;
	boolean newVersion;
	boolean newMinecraftVersion;
	boolean versionCheckComplete;

	String latestModVersion;
	String latestMCVersion = MC_VERSION;
	String description = "";

	String modName;
	String modVersion;
	String releaseURL;
	Logger modLogger = FMLLog.getLogger();

	public static boolean beforeTargetVersion(String version, String target) {

		try {
			String[] versionTokens = version.trim().split("\\.");
			String[] targetTokens = target.trim().split("\\.");

			for (int i = 0; i < versionTokens.length; ++i) {
				if (versionTokens[i].startsWith("a")) {
					// alpha builds ignore updates unless behind by a lot
					return false;
				}
				if (versionTokens[i].startsWith("b")) {

					if (targetTokens[i].startsWith("b")) {
						versionTokens[i] = versionTokens[i].substring(1);
						targetTokens[i] = targetTokens[i].substring(1);
					} else {
						// if this is a beta and target is not
						return true;
					}
				}
				if (targetTokens[i].startsWith("a") || targetTokens[i].startsWith("b")) {
					// if target is alpha or beta and this is not
					return false;
				}
				int v = Integer.valueOf(versionTokens[i]).intValue();
				int t = Integer.valueOf(targetTokens[i]).intValue();

				if (v < t) {
					return true;
				} else if (v > t) {
					return false;
				}
			}
		} catch (Throwable t) {
			// pokemon!
		}
		return false;
	}

	public static boolean afterTargetVersion(String version, String target) {

		return beforeTargetVersion(target, version);
	}

	public VersionHandler(String name, String version, String url) {

		modName = name;
		modVersion = latestModVersion = version;
		releaseURL = url;
	}

	public VersionHandler(String name, String version, String url, Logger logger) {

		modName = name;
		modVersion = latestModVersion = version;
		releaseURL = url;
		modLogger = logger;
	}

	public void checkForNewVersion() {

		Thread versionCheckThread = new VersionCheckThread();
		versionCheckThread.start();
	}

	public String getCurrentVersion() {

		return modVersion;
	}

	public String getLatestVersion() {

		return latestModVersion;
	}

	public String getLatestMCVersion() {

		return latestMCVersion;
	}

	public String getVersionDescription() {

		return description;
	}

	public boolean isCriticalUpdate() {

		return criticalUpdate;
	}

	public boolean isNewVersionAvailable() {

		return newVersion;
	}

	public boolean isMinecraftOutdated() {

		return newMinecraftVersion;
	}

	public boolean isVersionCheckComplete() {

		return versionCheckComplete;
	}

	private class VersionCheckThread extends Thread {

		@Override
		public void run() {

			try {
				URL versionFile = new URL(releaseURL);
				BufferedReader reader = new BufferedReader(new InputStreamReader(versionFile.openStream()));
				latestModVersion = reader.readLine();
				description = reader.readLine();
				criticalUpdate = Boolean.parseBoolean(reader.readLine());
				latestMCVersion = reader.readLine();
				reader.close();

				if (beforeTargetVersion(modVersion, latestModVersion)) {
					modLogger.log(Level.INFO, "An updated version of " + modName + " is available: " + latestModVersion + ".");
					newVersion = true;

					if (criticalUpdate) {
						modLogger.log(Level.INFO, "This update has been marked as CRITICAL and will ignore notification suppression.");
					}
					if (beforeTargetVersion(MC_VERSION, latestMCVersion)) {
						newMinecraftVersion = true;
						modLogger.log(Level.INFO, "This update is for Minecraft " + latestMCVersion + ".");
					}
				}
			} catch (Exception e) {
				modLogger.log(Level.WARN, "Version Check Failed: " + e.getMessage());
			}
			versionCheckComplete = true;
		}
	}

}
