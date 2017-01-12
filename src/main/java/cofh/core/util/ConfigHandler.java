package cofh.core.util;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.Set;

/**
 * This is effectively a wrapper for Forge Configurations. It allows for easier manipulation of Config files.
 *
 * @author King Lemming
 */
public class ConfigHandler {

	Configuration modConfiguration;
	String modVersion;

	public ConfigHandler(String version) {

		modVersion = version;
	}

	public ConfigHandler setConfiguration(Configuration config) {

		modConfiguration = config;
		modConfiguration.load();

		return this;
	}

	public Configuration getConfiguration() {

		return modConfiguration;
	}

	public String getVersion() {

		return modVersion;
	}

	public String getConfigVersion() {

		return get("Version", "Identifier", modVersion);
	}

	public boolean isOldConfig() {

		return !getConfigVersion().equals(modVersion);
	}

	public void save() {

		modConfiguration.save();
	}

	/* Shortcuts */
	public double get(String category, String key, double defaultValue) {

		return modConfiguration.get(category, key, defaultValue, null).getDouble(0);
	}

	public double get(String category, String key, double defaultValue, String comment) {

		return modConfiguration.get(category, key, defaultValue, comment).getDouble(0);
	}

	public int get(String category, String key, int defaultValue) {

		return modConfiguration.get(category, key, defaultValue, null).getInt();
	}

	public int get(String category, String key, int defaultValue, String comment) {

		return modConfiguration.get(category, key, defaultValue, comment).getInt();
	}

	public boolean get(String category, String key, boolean defaultValue) {

		return modConfiguration.get(category, key, defaultValue, null).getBoolean(defaultValue);
	}

	public boolean get(String category, String key, boolean defaultValue, String comment) {

		return modConfiguration.get(category, key, defaultValue, comment).getBoolean(defaultValue);
	}

	public String get(String category, String key, String defaultValue) {

		return modConfiguration.get(category, key, defaultValue, null).getString();
	}

	public String get(String category, String key, String defaultValue, String comment) {

		return modConfiguration.get(category, key, defaultValue, comment).getString();
	}

	public void set(String category, String key, double value) {

		getProperty(category, key, value).set(value);
	}

	public void set(String category, String key, int value) {

		getProperty(category, key, value).set(value);
	}

	public void set(String category, String key, boolean value) {

		getProperty(category, key, value).set(value);
	}

	public void set(String category, String key, String value) {

		getProperty(category, key, value).set(value);
	}

	/* Properties */
	public Property getProperty(String category, String key, double defaultValue) {

		return modConfiguration.get(category, key, defaultValue);
	}

	public Property getProperty(String category, String key, int defaultValue) {

		return modConfiguration.get(category, key, defaultValue);
	}

	public Property getProperty(String category, String key, boolean defaultValue) {

		return modConfiguration.get(category, key, defaultValue);
	}

	public Property getProperty(String category, String key, String defaultValue) {

		return modConfiguration.get(category, key, defaultValue);
	}

	public ConfigCategory getCategory(String category) {

		return modConfiguration.getCategory(category);
	}

	public Set<String> getCategoryNames() {

		return modConfiguration.getCategoryNames();
	}

	public Set<String> getCategoryKeys(String category) {

		return modConfiguration.getCategory(category).getValues().keySet();
	}

	public boolean hasCategory(String category) {

		return modConfiguration.hasCategory(category);
	}

	public boolean hasKey(String category, String key) {

		return modConfiguration.hasKey(category, key);
	}

	public boolean copyProperty(String category, String key, String newCategory, String newKey, boolean forceValue) {

		if (modConfiguration.hasKey(category, key)) {
			Property prop = modConfiguration.getCategory(category).get(key);

			if (prop.isIntValue()) {
				int value = modConfiguration.getCategory(category).getValues().get(key).getInt();

				if (forceValue) {
					removeProperty(newCategory, newKey);
				}
				modConfiguration.get(newCategory, newKey, value);
			} else if (prop.isBooleanValue()) {
				boolean value = modConfiguration.getCategory(category).getValues().get(key).getBoolean(false);

				if (forceValue) {
					removeProperty(newCategory, newKey);
				}
				modConfiguration.get(newCategory, newKey, value);
			} else if (prop.isDoubleValue()) {
				double value = modConfiguration.getCategory(category).getValues().get(key).getDouble(0.0);

				if (forceValue) {
					removeProperty(newCategory, newKey);
				}
				modConfiguration.get(newCategory, newKey, value);
			} else {
				String value = modConfiguration.getCategory(category).getValues().get(key).getString();

				if (forceValue) {
					removeProperty(newCategory, newKey);
				}
				modConfiguration.get(newCategory, newKey, value);
			}
			return true;
		}
		return false;
	}

	public boolean renameProperty(String category, String key, String newCategory, String newKey, boolean forceValue) {

		if (modConfiguration.hasKey(category, key)) {
			Property prop = modConfiguration.getCategory(category).get(key);

			if (prop.isIntValue()) {
				int value = modConfiguration.getCategory(category).getValues().get(key).getInt();
				removeProperty(category, key);

				if (forceValue) {
					removeProperty(newCategory, newKey);
				}
				modConfiguration.get(newCategory, newKey, value);
			} else if (prop.isBooleanValue()) {
				boolean value = modConfiguration.getCategory(category).getValues().get(key).getBoolean(false);
				removeProperty(category, key);

				if (forceValue) {
					removeProperty(newCategory, newKey);
				}
				modConfiguration.get(newCategory, newKey, value);
			} else if (prop.isDoubleValue()) {
				double value = modConfiguration.getCategory(category).getValues().get(key).getDouble(0.0);
				removeProperty(category, key);

				if (forceValue) {
					removeProperty(newCategory, newKey);
				}
				modConfiguration.get(newCategory, newKey, value);
			} else {
				String value = modConfiguration.getCategory(category).getValues().get(key).getString();
				removeProperty(category, key);

				if (forceValue) {
					removeProperty(newCategory, newKey);
				}
				modConfiguration.get(newCategory, newKey, value);
			}
			return true;
		}
		return false;
	}

	public boolean removeProperty(String category, String key) {

		if (!modConfiguration.hasKey(category, key)) {
			return false;
		}
		modConfiguration.getCategory(category).remove(key);
		return true;
	}

	public boolean renameCategory(String category, String newCategory) {

		if (!modConfiguration.hasCategory(category)) {
			return false;
		}
		modConfiguration.getCategory(category);
		for (Property prop : modConfiguration.getCategory(category).values()) {
			copyProperty(category, prop.getName(), newCategory, prop.getName(), true);
		}
		removeCategory(category);
		return true;
	}

	public boolean removeCategory(String category) {

		if (!modConfiguration.hasCategory(category)) {
			return false;
		}
		modConfiguration.removeCategory(modConfiguration.getCategory(category));
		return true;
	}

	public void cleanUp(boolean delConfig, boolean saveVersion) {

		removeProperty("version", "version");
		removeProperty("version", "Version");
		removeProperty("general", "version");
		removeProperty("general", "Version");
		removeProperty("Version", "Identifier");

		if (saveVersion) {
			getConfigVersion();
		}
		modConfiguration.save();

		if (delConfig) {
			modConfiguration = null;
		}
	}

}
