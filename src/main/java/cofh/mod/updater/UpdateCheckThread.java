package cofh.mod.updater;

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

		l: try {
			String id = _mod.getModName();
			ModVersion ourVer = ModVersion.parse(id, _mod.getModVersion());

			URL versionFile = new URL(_releaseUrl);

			BufferedReader reader = new BufferedReader(new InputStreamReader(versionFile.openStream()));
			ModVersion newVer = ModVersion.parse(id, reader.readLine());
			ModVersion critVer = ModVersion.parse(id, reader.readLine());
			reader.close();

			if (newVer == null) {
				break l;
			}

			_newVer = newVer;
			_newVerAvailable = ourVer.compareTo(newVer) < 0;

			if (_newVerAvailable) {
				_mod.getLogger().info("An updated version of " + _mod.getModName() + " is available: " + newVer + ".");

				if (ourVer.minecraftVersion().compareTo(newVer.minecraftVersion()) < 0) {
					ReleaseVersion crit = critVer.minecraftVersion(), our = ourVer.minecraftVersion();
					_newVerAvailable = crit.major() == our.major() && crit.minor() == our.minor();
				}
				if (critVer != null && ourVer.compareTo(critVer) >= 0) {
					_criticalUpdate = Boolean.parseBoolean(critVer.description());
					_criticalUpdate &= _newVerAvailable;
				}
			}
			if (_criticalUpdate) {
				_mod.getLogger().info("This update has been marked as CRITICAL and will ignore notification suppression.");
			}
		} catch (Exception e) {
			Level level = Level.WARN;
			String base = _mod.getClass().getPackage().getName();
			int i = base.indexOf('.');
			if (i > 0) {
				base = base.substring(0, i);
			}
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
