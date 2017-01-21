package cofh.core.world.decoration;

import cofh.api.world.IGeneratorParser;
import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenAdvLakes;
import com.google.gson.JsonObject;
import com.typesafe.config.Config;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LakeParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String generatorName, Config genObject, Logger log, List<WeightedRandomBlock> resList, int clusterSize, List<WeightedRandomBlock> matList) {

		boolean useMaterial = false;
		{
			useMaterial = genObject.hasPath("useMaterial") ? genObject.getBoolean("useMaterial") : useMaterial;
		}
		WorldGenAdvLakes r = new WorldGenAdvLakes(resList, useMaterial ? matList : null);
		{
			if (genObject.hasPath("outlineWithStone")) {
				r.outlineBlock = genObject.getBoolean("outlineWithStone") ? Arrays.asList(new WeightedRandomBlock(Blocks.STONE, 0)) : null;
			}
			ArrayList<WeightedRandomBlock> list = new ArrayList<WeightedRandomBlock>();
			if (genObject.hasPath("outlineBlock")) {
				if (!FeatureParser.parseResList(genObject.root().get("outlineBlock"), list, true)) {
					log.warn("Entry specifies invalid outlineBlock for 'lake' generator! Not filling!");
				} else {
					r.outlineBlock = list;
				}
				list = new ArrayList<WeightedRandomBlock>();
			}
			if (genObject.hasPath("gapBlock")) {
				if (!FeatureParser.parseResList(genObject.root().get("gapBlock"), list, true)) {
					log.warn("Entry specifies invalid gapBlock for 'lake' generator! Not filling!");
				} else {
					r.gapBlock = list;
				}
			}
			if (genObject.hasPath("solidOutline")) {
				r.solidOutline = genObject.getBoolean("solidOutline");
			}
			if (genObject.hasPath("totalOutline")) {
				r.totalOutline = genObject.getBoolean("totalOutline");
			}
		}
		return r;
	}

}
