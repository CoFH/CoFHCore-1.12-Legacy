package cofh.lib.world;

import com.google.gson.JsonObject;
import com.typesafe.config.Config;
import org.apache.logging.log4j.Logger;

public interface IFeatureParser {

	/**
	 * Parse a {@link JsonObject} for registration with an with an {@link IFeatureHandler}.
	 *
	 * @param featureName The name of the feature to register.
	 * @param genObject   The JsonObject to parse.
	 * @param log         The {@link Logger} to log debug/error/etc. messages to.
	 * @return The {@link IFeatureGenerator} to be registered with an IFeatureHandler
	 */
	IFeatureGenerator parseFeature(String featureName, Config genObject, Logger log);

}
