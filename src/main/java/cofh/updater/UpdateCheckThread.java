package cofh.updater;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.spi.AbstractLogger;

public class UpdateCheckThread extends Thread {

	private final String _releaseUrl;
	private final IUpdatableMod _mod;

	private boolean _checkComplete = false;
	private boolean _newVerAvailable = false;
	private boolean _criticalUpdate = false;
	private ModVersion _newVer;

	public UpdateCheckThread(IUpdatableMod mod) {

		this(mod, null);
	}

	public UpdateCheckThread(IUpdatableMod mod, String releaseUrl) {

		_mod = mod;
		if (releaseUrl == null) {
			releaseUrl = "https://raw.github.com/skyboy/" + mod.getModId() + "/master/VERSION";
		}
		_releaseUrl = releaseUrl;
	}

	@Override
	public void run() {

		try {
			ModVersion ourVer = ModVersion.parse(_mod.getModVersion());

			URL versionFile = new URL(_releaseUrl);

			BufferedReader reader = new BufferedReader(new InputStreamReader(versionFile.openStream()));
			ModVersion newVer = ModVersion.parse(reader.readLine());
			ModVersion critVer = ModVersion.parse(reader.readLine());
			reader.close();

			_newVer = newVer;
			_newVerAvailable = ourVer.compareTo(newVer) < 0;

			if (_newVerAvailable) {
				_mod.getLogger().info("An updated version of " + _mod.getModName() + " is available: " + newVer.modVersion().toString() + ".");

				if (ourVer.minecraftVersion().compareTo(newVer.minecraftVersion()) < 0) {
					_mod.getLogger().info("This update is for Minecraft " + newVer.minecraftVersion().toString() + ".");
				}
				if (ourVer.compareTo(critVer) > 0) {
					_criticalUpdate = Boolean.parseBoolean(critVer.description());
				}
			}
			if (_criticalUpdate) {
				_mod.getLogger().info("This update has been marked as CRITICAL " + "and will ignore notification suppression.");
			}
		} catch (Exception e) {
			Level level = Level.WARN;
			String base = _mod.getClass().getPackage().getName();
			base = base.substring(0, base.indexOf('.'));

			if (base.equals("cofh") || base.equals("powercrystals")) {
				level = Level.ERROR;
			}
			_mod.getLogger().log(level, AbstractLogger.CATCHING_MARKER, "Update check for " + _mod.getModName() + " failed.", e);
		}
		_checkComplete = true;
	}

	public boolean checkComplete() {

		return _checkComplete;
	}

	public boolean isCriticalUpdate() {

		return _criticalUpdate;
	}

	public boolean newVersionAvailable() {

		return _newVerAvailable;
	}

	public ModVersion newVersion() {

		return _newVer;
	}

}
