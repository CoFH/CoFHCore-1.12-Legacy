package cofh.core.world;

import cofh.api.world.IFeatureGenerator;
import cofh.api.world.IFeatureParser;
import cofh.core.CoFHProps;
import cofh.core.util.CoreUtils;
import cofh.core.world.feature.NormalParser;
import cofh.core.world.feature.UniformParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.helpers.MathHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cpw.mods.fml.common.registry.GameRegistry;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FeatureParser {

	private static File worldGenFolder;
	private static File vanillaGen;
	private static final String vanillaGenInternal = "assets/cofh/world/Vanilla.json";
	private static HashMap<String, IFeatureParser> templateHandlers = new HashMap<String, IFeatureParser>();
	private static Logger log = LogManager.getLogger("CoFHWorld");

	private FeatureParser() {

	}

	public static boolean registerTemplate(String template, IFeatureParser handler) {

		// TODO: provide this function through IFeatureHandler?
		if (!templateHandlers.containsKey(template)) {
			templateHandlers.put(template, handler);
			return true;
		}
		log.error("Attempted to register duplicate template '" + template + "'!");
		return false;
	}

	public static void initialize() {

		worldGenFolder = new File(CoFHProps.configDir, "/cofh/world/");

		if (!worldGenFolder.exists()) {
			try {
				worldGenFolder.mkdir();
			} catch (Throwable t) {
				// pokemon!
			}
		}
		vanillaGen = new File(CoFHProps.configDir, "/cofh/world/Vanilla.json");

		try {
			if (vanillaGen.createNewFile()) {
				CoreUtils.copyFileUsingStream(vanillaGenInternal, vanillaGen);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}

		log.info("Registering default templates");
		registerTemplate("uniform", new UniformParser());
		registerTemplate("normal", new NormalParser());
		registerTemplate("fractal", null);// FIXME: convert WorldGenMineableCell
	}

	private static void addFiles(ArrayList<File> list, File folder) {

		File[] fList = folder.listFiles();

		if (fList == null) {
			log.error("There are no World Generation files present in " + folder + ".");
			return;
		}
		list.addAll(Arrays.asList(fList));
	}

	public static void parseGenerationFile() {

		JsonParser parser = new JsonParser();

		ArrayList<File> worldGenList = new ArrayList<File>(5);
		addFiles(worldGenList, worldGenFolder);
		for (int i = 0, e = worldGenList.size(); i < e; ++i) {
			File genFile = worldGenList.get(i);
			if (genFile.equals(vanillaGen)) {
				if (!WorldHandler.genReplaceVanilla) {
					worldGenList.remove(i);
				}
				break;
			}
		}

		for (int i = 0; i < worldGenList.size(); ++i) {

			File genFile = worldGenList.get(i);
			if (genFile.isDirectory()) {
				addFiles(worldGenList, genFile);
				continue;
			}

			JsonObject genList;
			try {
				genList = (JsonObject) parser.parse(new FileReader(genFile));
			} catch (Throwable t) {
				log.error("Critical error reading from a world generation file: " + genFile + " > Please be sure the file is correct!", t);
				continue;
			}

			log.info("Reading world generation info from: " + genFile + ":");
			for (Entry<String, JsonElement> genEntry : genList.entrySet()) {
				if (parseGenerationEntry(genEntry.getKey(), genEntry.getValue())) {
					log.debug("Generation entry successfully parsed: \"" + genEntry.getKey() + "\"");
				} else {
					log.error("Error parsing generation entry: \"" + genEntry.getKey() + "\" > Please check the parameters. It *may* be a duplicate.");
				}
			}
		}
	}

	private static boolean parseGenerationEntry(String featureName, JsonElement genEntry) {

		JsonObject genObject = genEntry.getAsJsonObject();

		String templateName = parseTemplate(genObject);
		IFeatureParser template = templateHandlers.get(templateName);
		if (template != null) {
			IFeatureGenerator feature = template.parseFeature(featureName, genObject, log);
			if (feature != null) {
				return WorldHandler.addFeature(feature);
			}
			log.warn("Template '" + templateName + "' failed to parse its entry!");
		} else {
			log.warn("Unknown template + '" + templateName + "'.");
		}

		return false;
	}

	private static String parseTemplate(JsonObject genObject) {

		JsonElement genElement = genObject.get("template");
		if (genElement.isJsonObject()) {
			genObject = genElement.getAsJsonObject();

			return genObject.get("type").getAsString();
		} else {
			return genElement.getAsString();
		}
	}

	// TODO: move these helper functions outside core?

	public static Block parseBlockName(String blockRaw) {

		String[] blockTokens = blockRaw.split(":", 2);
		int i = 0;
		return GameRegistry.findBlock(blockTokens.length > 1 ? blockTokens[i++] : "minecraft", blockTokens[i]);
	}

	public static WeightedRandomBlock parseBlockEntry(JsonElement genElement) {

		if (genElement.isJsonObject()) {
			JsonObject blockElement = genElement.getAsJsonObject();
			if (!blockElement.has("name")) {
				log.error("Block entry needs a name!");
				return null;
			}
			Block block = parseBlockName(blockElement.get("name").getAsString());
			if (block == null) {
				log.error("Invalid block entry!");
				return null;
			}
			int metadata = blockElement.has("metadata") ? MathHelper.clampI(blockElement.get("metadata").getAsInt(), 0, 15) : 0;
			int weight = blockElement.has("weight") ? MathHelper.clampI(blockElement.get("weight").getAsInt(), 1, 1000000) : 100;
			return new WeightedRandomBlock(new ItemStack(block, 1, metadata), weight);
		} else {
			Block block = parseBlockName(genElement.getAsString());
			if (block == null) {
				log.error("Invalid block entry!");
				return null;
			}
			return new WeightedRandomBlock(new ItemStack(block, 1, 0));
		}
	}

	public static boolean parseResList(JsonElement genElement, List<WeightedRandomBlock> resList) {

		if (genElement.isJsonArray()) {
			JsonArray blockList = genElement.getAsJsonArray();

			for (int i = 0; i < blockList.size(); i++) {
				WeightedRandomBlock entry = parseBlockEntry(blockList.get(i));
				if (entry == null) {
					return false;
				}
				resList.add(entry);
			}
		} else {
			WeightedRandomBlock entry = parseBlockEntry(genElement);
			if (entry == null) {
				return false;
			}
			resList.add(entry);
		}
		return true;
	}

}
