package cofh.core.world.decoration;

import cofh.api.world.IGeneratorParser;
import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenSmallTree;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.typesafe.config.Config;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class SmallTreeParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String generatorName, Config genObject, Logger log, List<WeightedRandomBlock> resList, int clusterSize, List<WeightedRandomBlock> matList) {

		ArrayList<WeightedRandomBlock> list = new ArrayList<WeightedRandomBlock>();
		ArrayList<WeightedRandomBlock> blocks = new ArrayList<WeightedRandomBlock>();
		if (genObject.hasPath("genMaterial")) {
			if (!FeatureParser.parseResList(genObject.root().get("genMaterial"), blocks, false)) {
				log.warn("Entry specifies invalid genMaterial for 'smalltree' generator! Using air!");
				blocks.clear();
				blocks.add(new WeightedRandomBlock(Blocks.AIR));
			}
		} else {
			log.info("Entry does not specify genMaterial for 'smalltree' generator! There are no restrictions!");
		}

		if (genObject.hasPath("leaves")) {
			list = new ArrayList<WeightedRandomBlock>();
			if (!FeatureParser.parseResList(genObject.root().get("leaves"), list, true)) {
				log.warn("Entry specifies invalid leaves for 'smalltree' generator!");
				list.clear();
			}
		} else {
			log.info("Entry does not specify leaves for 'smalltree' generator! There are none!");
		}

		WorldGenSmallTree r = new WorldGenSmallTree(resList, list, blocks);
		{
			r.genSurface = matList.toArray(new WeightedRandomBlock[matList.size()]);

			if (genObject.hasPath("minHeight")) {
				r.minHeight = genObject.getInt("minHeight");
			}
			if (genObject.hasPath("heightVariance")) {
				r.heightVariance = genObject.getInt("heightVariance");
			}

			if (genObject.hasPath("treeChecks")) {
				r.treeChecks = genObject.getBoolean("treeChecks");
			}
			if (genObject.hasPath("relaxedGrowth")) {
				r.relaxedGrowth = genObject.getBoolean("relaxedGrowth");
			}
			if (genObject.hasPath("waterLoving")) {
				r.waterLoving = genObject.getBoolean("waterLoving");
			}
			if (genObject.hasPath("leafVariance")) {
				r.leafVariance = genObject.getBoolean("leafVariance");
			}
		}
		return r;
	}

}
