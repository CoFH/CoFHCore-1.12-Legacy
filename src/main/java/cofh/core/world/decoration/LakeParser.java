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
				r.outlineBlock = genObject.get("outlineWithStone").getAsBoolean() ? Arrays.asList(new WeightedRandomBlock(Blocks.stone, 0)) : null;
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
		if (genObject.has("boxWidth")) {
			r.width = genObject.get("boxWidth").getAsInt();
		}
		if (genObject.has("boxHeight")) {
			r.height = genObject.get("boxHeight").getAsInt();
		}
		if (genObject.has("blobMinWidth")) {
			r.blobMinWidth = genObject.get("blobMinWidth").getAsDouble();
		}
		if (genObject.has("blobMinHeight")) {
			r.blobMinHeight = genObject.get("blobMinHeight").getAsDouble();
		}
		if (genObject.has("blobWidthVariance")) {
			r.blobWidthVariance = genObject.get("blobWidthVariance")
					.getAsDouble();
		}
		if (genObject.has("blobHeightVariance")) {
			r.blobHeightVariance = genObject.get("blobHeightVariance")
					.getAsDouble();
		}
		if (genObject.has("minBlobs")) {
			r.minBlobs = genObject.get("minBlobs").getAsInt();
		}
		if (genObject.has("blobVariance")) {
			r.blobVariance = genObject.get("blobVariance").getAsInt();
		}
		if (genObject.has("boxOffsetHorizontal")) {
			r.boxOffsetHorizontal = genObject.get("boxOffsetHorizontal")
					.getAsDouble();
		}
		if (genObject.has("boxOffsetVertical")) {
			r.boxOffsetVertical = genObject.get("boxOffsetVertical")
					.getAsDouble();
		}
		return r;
	}

}
