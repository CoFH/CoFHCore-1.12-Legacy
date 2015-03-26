package cofh.core.world.decoration;

import cofh.api.world.IGeneratorParser;
import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenSmallTree;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenerator;

import org.apache.logging.log4j.Logger;

public class SmallTreeParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String generatorName, JsonObject genObject, Logger log, List<WeightedRandomBlock> resList, int clusterSize,
			List<WeightedRandomBlock> matList) {

		ArrayList<WeightedRandomBlock> list = new ArrayList<WeightedRandomBlock>();
		ArrayList<WeightedRandomBlock> blocks = new ArrayList<WeightedRandomBlock>();
		if (genObject.has("genMaterial")) {
			JsonElement entry = genObject.get("genMaterial");
			if (!entry.isJsonNull() && !FeatureParser.parseResList(entry, blocks, false)) {
				log.warn("Entry specifies invalid genMaterial for 'smalltree' generator! Using air!");
				blocks.clear();
				blocks.add(new WeightedRandomBlock(Blocks.air));
			}
		} else {
			log.info("Entry does not specify genMaterial for 'smalltree' generator! There are no restrictions!");
		}

		if (genObject.has("leaves")) {
			list = new ArrayList<WeightedRandomBlock>();
			JsonElement entry = genObject.get("leaves");
			if (!entry.isJsonNull() && !FeatureParser.parseResList(entry, list, true)) {
				log.warn("Entry specifies invalid leaves for 'smalltree' generator!");
				list.clear();
			}
		} else {
			log.info("Entry does not specify leaves for 'smalltree' generator! There are none!");
		}

		WorldGenSmallTree r = new WorldGenSmallTree(resList, list, blocks);
		{
			r.genSurface = matList.toArray(new WeightedRandomBlock[matList.size()]);

			if (genObject.has("minHeight")) {
				r.minHeight = genObject.get("minHeight").getAsInt();
			}
			if (genObject.has("heightVariance")) {
				r.heightVariance = genObject.get("heightVariance").getAsInt();
			}

			if (genObject.has("treeChecks")) {
				r.treeChecks = genObject.get("treeChecks").getAsBoolean();
			}
			if (genObject.has("relaxedGrowth")) {
				r.relaxedGrowth = genObject.get("relaxedGrowth").getAsBoolean();
			}
			if (genObject.has("waterLoving")) {
				r.waterLoving = genObject.get("waterLoving").getAsBoolean();
			}
			if (genObject.has("leafVariance")) {
				r.leafVariance = genObject.get("leafVariance").getAsBoolean();
			}
		}
		return r;
	}

}
