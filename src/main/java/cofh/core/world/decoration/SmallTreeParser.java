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
	public WorldGenerator parseGenerator(String generatorName, JsonObject genObject, Logger log,
			List<WeightedRandomBlock> resList, int clusterSize, List<WeightedRandomBlock> matList) {

		ArrayList<WeightedRandomBlock> list = new ArrayList<WeightedRandomBlock>();
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

		WorldGenSmallTree r = new WorldGenSmallTree(resList, list, matList);
		{
			if (genObject.has("minHeight"))
				r.minHeight = genObject.get("minHeight").getAsInt();
			if (genObject.has("heightVariance"))
				r.heightVariance = genObject.get("heightVariance").getAsInt();

			if (genObject.has("treeChecks"))
				r.treeChecks = genObject.get("treeChecks").getAsBoolean();
			if (genObject.has("relaxedGrowth"))
				r.relaxedGrowth = genObject.get("relaxedGrowth").getAsBoolean();
			if (genObject.has("waterLoving"))
				r.waterLoving = genObject.get("waterLoving").getAsBoolean();

			if (genObject.has("genSurface")) {
				list = new ArrayList<WeightedRandomBlock>();
				JsonElement entry = genObject.get("genSurface");
				if (!entry.isJsonNull() && !FeatureParser.parseResList(entry, list, false)) {
					log.warn("Entry specifies invalid genSurface for 'smalltree' generator! Using grass!");
					list.clear();
					list.add(new WeightedRandomBlock(Blocks.grass));
					list.add(new WeightedRandomBlock(Blocks.dirt, -1));
				}
				if (list.size() > 0)
					r.genSurface = list.toArray(new WeightedRandomBlock[list.size()]);
			} else {
				log.info("Entry does not specify genSurface for 'smalltree' generator! There are no restrictions!");
			}
		}
		return r;
	}

}
