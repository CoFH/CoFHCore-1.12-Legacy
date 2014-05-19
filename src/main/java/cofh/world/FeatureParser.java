package cofh.world;

import cofh.CoFHCore;
import cofh.core.CoFHProps;
import cofh.util.WeightedRandomBlock;
import cofh.world.feature.FeatureOreGenNormal;
import cofh.world.feature.FeatureOreGenUniform;
import cofh.world.feature.WorldGenMinableCluster;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class FeatureParser {

	private static final File worldGen = new File(CoFHProps.configDir, "/cofh/WorldGeneration.json");

	private FeatureParser() {

	}

	public static void parseGenerationFile() {

		JsonParser parser = new JsonParser();
		JsonObject groupList;

		if (!worldGen.exists()) {
			try {
				createDefaultFile();
			} catch (Throwable t) {
				CoFHCore.log.error("Critical error opening the WorldGeneration.json file!");
				t.printStackTrace();
				return;
			}
		}
		try {
			groupList = (JsonObject) parser.parse(new FileReader(worldGen));
		} catch (Throwable t) {
			CoFHCore.log.error("Critical error reading from the WorldGeneration.json file!");
			t.printStackTrace();
			return;
		}
		for (Entry<String, JsonElement> group : groupList.entrySet()) {
			parseGenerationCategory(group.getValue());
		}
	}

	public static void parseGenerationCategory(JsonElement category) {

		JsonObject group = category.getAsJsonObject();

		for (Entry<String, JsonElement> entry : group.entrySet()) {
			parseGenerationEntry(entry.getValue());
		}
	}

	public static void parseGenerationEntry(JsonElement entry) {

		JsonObject entryObject = entry.getAsJsonObject();

		String featureName = entryObject.get("name").getAsString().toLowerCase();
		String template = entryObject.get("template").getAsString().toLowerCase();

		List<WeightedRandomBlock> resList = new ArrayList<WeightedRandomBlock>();

		if (entryObject.get("block").isJsonArray()) {

			// check blocks and metadata, form list
			// tokenize block (:), grab
			// get meta, make itemstack
		} else {
			// tokenize block (:), grab
			// get meta, make itemstack
		}
		int clusterSize = entryObject.get("clusterSize").getAsInt();
		int numClusters = entryObject.get("numClusters").getAsInt();
		boolean retrogen = entryObject.get("retrogen").getAsBoolean();

		if (template.equals("uniform")) {

			int minHeight = entryObject.get("minHeight").getAsInt();
			int maxHeight = entryObject.get("maxHeight").getAsInt();

			WorldHandler.addFeature(new FeatureOreGenUniform(featureName, new WorldGenMinableCluster(resList, clusterSize), numClusters, minHeight, maxHeight,
					retrogen));
		} else if (template.equals("normal")) {

			int meanHeight = entryObject.get("meanHeight").getAsInt();
			int maxVariance = entryObject.get("maxVariance").getAsInt();

			WorldHandler.addFeature(new FeatureOreGenNormal(featureName, new WorldGenMinableCluster(resList, clusterSize), numClusters, meanHeight,
					maxVariance, retrogen));
		}
	}

	public static void createDefaultFile() throws IOException {

		worldGen.createNewFile();
	}

}
