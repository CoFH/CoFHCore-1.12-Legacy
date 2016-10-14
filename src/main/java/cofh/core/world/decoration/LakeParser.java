package cofh.core.world.decoration;

import cofh.api.world.IGeneratorParser;
import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenAdvLakes;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenerator;

import org.apache.logging.log4j.Logger;

public class LakeParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String generatorName, JsonObject genObject, Logger log, List<WeightedRandomBlock> resList, int clusterSize,
			List<WeightedRandomBlock> matList) {

		boolean useMaterial = false;
		{
			useMaterial = genObject.has("useMaterial") ? genObject.get("useMaterial").getAsBoolean() : useMaterial;
		}
		WorldGenAdvLakes r = new WorldGenAdvLakes(resList, useMaterial ? matList : null);
		{
			if (genObject.has("outlineWithStone")) {
				r.outlineBlock = genObject.get("outlineWithStone").getAsBoolean() ? Arrays.asList(new WeightedRandomBlock(Blocks.STONE, 0)) : null;
			}
			ArrayList<WeightedRandomBlock> list = new ArrayList<WeightedRandomBlock>();
			if (genObject.has("outlineBlock")) {
				if (!FeatureParser.parseResList(genObject.get("outlineBlock"), list, true)) {
					log.warn("Entry specifies invalid outlineBlock for 'lake' generator! Not filling!");
				} else {
					r.outlineBlock = list;
				}
				list = new ArrayList<WeightedRandomBlock>();
			}
			if (genObject.has("gapBlock")) {
				if (!FeatureParser.parseResList(genObject.get("gapBlock"), list, true)) {
					log.warn("Entry specifies invalid gapBlock for 'lake' generator! Not filling!");
				} else {
					r.gapBlock = list;
				}
			}
			if (genObject.has("solidOutline")) {
				r.solidOutline = genObject.get("solidOutline").getAsBoolean();
			}
			if (genObject.has("totalOutline")) {
				r.totalOutline = genObject.get("totalOutline").getAsBoolean();
			}
		}
		return r;
	}

}
