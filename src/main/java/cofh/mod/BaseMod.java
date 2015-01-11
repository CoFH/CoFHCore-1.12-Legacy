package cofh.mod;

import cofh.mod.updater.IUpdatableMod;
import cofh.mod.updater.ModRange;
import cofh.mod.updater.ModVersion;
import com.google.common.base.Strings;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.versioning.InvalidVersionSpecificationException;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.helpers.Loader;
import org.apache.logging.log4j.spi.AbstractLogger;

public abstract class BaseMod implements IUpdatableMod {

	protected File _configFolder;
	protected final String _modid;
	protected final Logger _log;

	protected BaseMod(Logger log) {

		String name = getModId();
		_modid = name.toLowerCase();
		_log = log;
	}

	protected BaseMod() {

		String name = getModId();
		_modid = name.toLowerCase();
		_log = LogManager.getLogger(name);
	}

	@NetworkCheckHandler
	public final boolean networkCheck(Map<String, String> remoteVersions, Side side) throws InvalidVersionSpecificationException {

		if (!requiresRemoteFrom(side)) {
			return true;
		}
		Mod mod = getClass().getAnnotation(Mod.class);
		String _modid = mod.modid();
		if (!remoteVersions.containsKey(_modid)) {
			return false;
		}
		String remotes = mod.acceptableRemoteVersions();
		if (!"*".equals(remotes)) {

			String remote = remoteVersions.get(_modid);
			if (Strings.isNullOrEmpty(remotes)) {
				return getModVersion().equalsIgnoreCase(remote);
			}

			return ModRange.createFromVersionSpec(_modid, remotes).containsVersion(new ModVersion(_modid, remote));
		}
		return true;
	}

	protected boolean requiresRemoteFrom(Side side) {

		return true;
	}

	protected String getConfigBaseFolder() {

		String base = getClass().getPackage().getName();
		int i = base.indexOf('.');
		if (i >= 0) {
			return base.substring(0, i);
		}
		return "";
	}

	protected void setConfigFolderBase(File folder) {

		_configFolder = new File(folder, getConfigBaseFolder() + "/" + _modid + "/");
	}

	protected File getConfig(String name) {

		return new File(_configFolder, name + ".cfg");
	}

	protected File getClientConfig() {

		return getConfig("client");
	}

	protected File getCommonConfig() {

		return getConfig("common");
	}

	protected String getAssetDir() {

		return _modid;
	}

	@Override
	public Logger getLogger() {

		return _log;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadLanguageFile(String lang, InputStream stream) throws Throwable {

		InputStreamReader is = new InputStreamReader(stream, "UTF-8");

		Properties langPack = new Properties();
		langPack.load(is);

		HashMap<String, String> parsedLangFile = new HashMap<String, String>();
		parsedLangFile.putAll((Map) langPack); // lovely casting hack

		LanguageRegistry.instance().injectLanguage(lang.intern(), parsedLangFile);
	}

	@SuppressWarnings("resource")
	protected void loadLang() {

		if (FMLLaunchHandler.side() == Side.CLIENT) {
			try {
				loadClientLang();
				return;
			} catch (Throwable t) {
				_log.error(AbstractLogger.CATCHING_MARKER, "???", t);
			}
		}

		String path = "assets/" + getAssetDir() + "/language/";
		InputStream is = null;
		String lang = "en_US";
		try {
			is = Loader.getResource(path + lang + ".lang", null).openStream();
			loadLanguageFile(lang, is);
		} catch (Throwable t) {
			_log.catching(Level.INFO, t);
		} finally {
			try {
				is.close();
			} catch (IOException t) {
				_log.catching(Level.INFO, t);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void loadClientLang() {

		IReloadableResourceManager manager = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
		manager.registerReloadListener(new LangManager(manager));
	}

	@SideOnly(Side.CLIENT)
	private class LangManager implements IResourceManagerReloadListener {

		private final String _path;

		public LangManager(IResourceManager manager) {

			_path = getAssetDir() + ":language/";
			onResourceManagerReload(manager);
		}

		@Override
		public void onResourceManagerReload(IResourceManager manager) {

			String l = null;
			try {
				l = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
			} catch (Throwable t) {
				_log.catching(Level.WARN, t);
			}

			for (String lang : Arrays.asList("en_US", l)) {
				if (lang != null) {
					try {
						List<IResource> files = manager.getAllResources(new ResourceLocation(_path + lang + ".lang"));
						for (IResource file : files) {
							if (file.getInputStream() == null) {
								_log.warn("A resource pack defines an entry for language '" + lang + "' but the InputStream is null.");
								continue;
							}
							try {
								loadLanguageFile(lang, file.getInputStream());
							} catch (Throwable t) {
								_log.warn(AbstractLogger.CATCHING_MARKER, "A resource pack has a file for language '" + lang + "' but the file is invalid.", t);
							}
						}
					} catch (Throwable t) {
						_log.info(AbstractLogger.CATCHING_MARKER, "No language data for '" + lang + "'", t);
					}
				}
			}
		}
	}
}
