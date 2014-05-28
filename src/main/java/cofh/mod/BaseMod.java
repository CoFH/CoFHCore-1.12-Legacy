package cofh.mod;

import cofh.updater.IUpdatableMod;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
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

	protected String getConfigBaseFolder() {

		String base = getClass().getPackage().getName();
		int i = base.indexOf('.');
		if (i >= 0) {
			return base.substring(0, base.indexOf('.'));
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

	@Override
	public Logger getLogger() {

		return _log;
	}

	private LinkedList<String> loadLanguageList(InputStream stream) throws Throwable {

		return loadLanguageList(stream, new LinkedList<String>());
	}

	private LinkedList<String> loadLanguageList(InputStream stream, LinkedList<String> list) throws Throwable {

		JsonReader r = new JsonReader(new InputStreamReader(stream, "UTF-8"));
		// doesn't matter if it's strictly JSON, so long as it's a list
		r.setLenient(true);
		if (r.peek() == JsonToken.BEGIN_ARRAY) {
			r.beginArray();
		}
		builder: while (true) {
			switch (r.peek()) {
			case END_ARRAY:
			case END_DOCUMENT: // doesn't matter if the array is valid
				break builder;
			default: // require only strings
			case STRING:
				list.add(r.nextString());
			}
		}
		r.close();

		return list;
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
			} catch (Throwable _) {
				_log.error(AbstractLogger.CATCHING_MARKER, "???", _);
			}
		}

		String path = "/assets/" + _modid + "/language/";
		InputStream s = null;
		try {
			s = Loader.getResource(path + ".languages", null).openStream();
			for (String lang : loadLanguageList(s)) {
				InputStream is = null;
				try {
					is = Loader.getResource(path + lang + ".lang", null).openStream();
					loadLanguageFile(lang, is);
				} catch (Throwable _) {
					_log.catching(Level.INFO, _);
				} finally {
					try {
						is.close();
					} catch (IOException _) {
						_log.catching(Level.INFO, _);
					}
				}
			}
		} catch (Throwable _) {
			_log.catching(Level.WARN, _);
		} finally {
			try {
				if (s != null) {
					s.close();
				}
			} catch (IOException _) {
				_log.catching(Level.WARN, _);
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

			_path = _modid + ":language/";
			loadAllLanguages(manager);
		}

		@Override
		public void onResourceManagerReload(IResourceManager manager) {

			try {
				String lang = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();

				try {
					loadLanguageFile(lang, manager.getResource(new ResourceLocation(_path + lang + ".lang")).getInputStream());
				} catch (Throwable _) {
					_log.info(AbstractLogger.CATCHING_MARKER, "No language data for '" + lang + "'", _);
					loadLanguageFile(lang, manager.getResource(new ResourceLocation(_path + "en_US.lang")).getInputStream());
				}
			} catch (Throwable _) {
				_log.catching(Level.WARN, _);
			}
		}

		public void loadAllLanguages(IResourceManager manager) {

			try {
				LinkedList<String> langs = new LinkedList<String>();
				List<IResource> files = manager.getAllResources(new ResourceLocation(_path + ".languages"));
				for (IResource lang : files) {
					try {
						loadLanguageList(lang.getInputStream(), langs);
					} catch (Throwable _) {
						_log.warn(AbstractLogger.CATCHING_MARKER, "A resource pack's .language file is invalid.", _);
					}
				}
				for (String lang : langs) {
					try {
						loadLanguageFile(lang, manager.getResource(new ResourceLocation(_path + lang + ".lang")).getInputStream());
					} catch (Throwable _) {
						_log.warn(AbstractLogger.CATCHING_MARKER, "A resource pack defines the language '" + lang + "' but the file does not exist.", _);
					}
				}
			} catch (Throwable _) {
				_log.catching(Level.INFO, _);
			}
		}
	}

}
