package cofh.mod.updater;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.spi.AbstractLogger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateCheckThread extends Thread {

    private final String _releaseUrl, _downloadUrl;
    private final IUpdatableMod _mod;

    private boolean _checkComplete = false;
    private boolean _newVerAvailable = false;
    private boolean _criticalUpdate = false;
    private ModVersion _newVer;

    public UpdateCheckThread(IUpdatableMod mod) {

        this(mod, null);
    }

    public UpdateCheckThread(IUpdatableMod mod, String releaseUrl) {

        this(mod, null, null);
    }

    public UpdateCheckThread(IUpdatableMod mod, String releaseUrl, String downloadUrl) {

        super("CoFHUpdater:" + mod.getModId());
        _mod = mod;
        if (releaseUrl == null) {
            releaseUrl = "https://raw.github.com/skyboy/" + mod.getModId() + "/master/VERSION";
        }
        _releaseUrl = releaseUrl;
        _downloadUrl = downloadUrl;
    }

    @Override
    public void run() {

        l:
        try {
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
                    ReleaseVersion newv = newVer.minecraftVersion(), our = ourVer.minecraftVersion();
                    _newVerAvailable = newv.major() == our.major() && newv.minor() == our.minor();
                }
                if (critVer != null && ourVer.compareTo(critVer) >= 0) {
                    _criticalUpdate = Boolean.parseBoolean(critVer.description());
                    _criticalUpdate &= _newVerAvailable;
                }
            }
            if (_criticalUpdate) {
                _mod.getLogger().info("This update has been marked as CRITICAL and will ignore notification suppression.");
            }

            if (Loader.isModLoaded("VersionChecker") && _newVerAvailable) {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setString("modDisplayName", _mod.getModName());
                compound.setString("oldVersion", ourVer.toString());
                compound.setString("newVersion", newVer.toString());
                if (_downloadUrl != null) {
                    compound.setString("updateUrl", _downloadUrl);
                    compound.setBoolean("isDirectLink", false);
                }
                FMLInterModComms.sendRuntimeMessage(_mod.getModId(), "VersionChecker", "addUpdate", compound);
                _newVerAvailable &= _criticalUpdate;
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
