package cofh.core.world.decoration;

import cofh.api.world.IGeneratorParser;
import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenAdvLakes;
import com.google.gson.JsonObject;

import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenerator;

import org.apache.logging.log4j.Logger;

public class LakeParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String generatorName, JsonObject genObject, Logger log,
			List<WeightedRandomBlock> resList, int clusterSize, List<WeightedRandomBlock> matList) {

		boolean useMaterial = false;
		{
			useMaterial = genObject.has("useMaterial") ? genObject.get("useMaterial").getAsBoolean() : useMaterial;
		}
		WorldGenAdvLakes r = new WorldGenAdvLakes(resList, useMaterial ? matList : null);
		{
			if (genObject.has("outlineWithStone")) {
				r.outlineBlock = genObject.get("outlineWithStone").getAsBoolean() ?
						new WeightedRandomBlock(Blocks.stone, 0) : null;
			}
			if (genObject.has("outlineBlock"))
				r.outlineBlock = FeatureParser.parseBlockEntry(genObject.get("outlineBlock"), true);
			if (genObject.has("gapBlock"))
				r.gapBlock = FeatureParser.parseBlockEntry(genObject.get("gapBlock"), true);
			// TODO: convert the above to to lists
		}
		return r;
	}

}
