package cofh.core.world.decoration;

import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.IGeneratorParser;
import cofh.lib.world.WorldGenSmallTree;
import com.typesafe.config.Config;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class SmallTreeParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String name, Config genObject, Logger log, List<WeightedRandomBlock> resList, List<WeightedRandomBlock> matList) {

		ArrayList<WeightedRandomBlock> list = new ArrayList<>();
		ArrayList<WeightedRandomBlock> blocks = new ArrayList<>();
		if (genObject.hasPath("surface")) {
			if (!FeatureParser.parseResList(genObject.root().get("surface"), blocks, false)) {
				log.warn("Entry specifies invalid surface for 'smalltree' generator! Using dirt!");
				blocks.clear();
				blocks.add(new WeightedRandomBlock(Blocks.GRASS));
				blocks.add(new WeightedRandomBlock(Blocks.DIRT));
			}
		}

		if (genObject.hasPath("leaves")) {
			list = new ArrayList<>();
			if (!FeatureParser.parseResList(genObject.root().get("leaves"), list, true)) {
				log.warn("Entry specifies invalid leaves for 'smalltree' generator!");
				list.clear();
			}
		} else {
			log.info("Entry does not specify leaves for 'smalltree' generator! There are none!");
		}

		WorldGenSmallTree r = new WorldGenSmallTree(resList, list, matList);
		{
			if (blocks.size() > 0) {
				r.genSurface = blocks.toArray(new WeightedRandomBlock[blocks.size()]);
			}

			if (genObject.hasPath("min-height")) {
				r.minHeight = genObject.getInt("min-height");
			}
			if (genObject.hasPath("height-variance")) {
				r.heightVariance = genObject.getInt("height-variance");
			}

			if (genObject.hasPath("tree-checks")) {
				r.treeChecks = genObject.getBoolean("tree-checks");
			}
			if (genObject.hasPath("relaxed-growth")) {
				r.relaxedGrowth = genObject.getBoolean("relaxed-growth");
			}
			if (genObject.hasPath("water-loving")) {
				r.waterLoving = genObject.getBoolean("water-loving");
			}
			if (genObject.hasPath("leaf-variance")) {
				r.leafVariance = genObject.getBoolean("leaf-variance");
			}
		}
		return r;
	}

}
