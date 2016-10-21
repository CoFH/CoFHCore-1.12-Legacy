package cofh.core.world.feature;

import cofh.api.world.IGeneratorParser;
import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.world.WorldGenDecoration;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenerator;

import org.apache.logging.log4j.Logger;

public class DecorationParser extends SurfaceParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String generatorName, JsonObject genObject, Logger log, List<WeightedRandomBlock> resList, int clusterSize,
			List<WeightedRandomBlock> matList) {

		ArrayList<WeightedRandomBlock> list = new ArrayList<WeightedRandomBlock>();
		if (!genObject.has("genSurface")) {
			log.info("Entry does not specify genSurface for 'decoration' generator. Using grass.");
			list.add(new WeightedRandomBlock(Blocks.GRASS));
		} else {
			if (!FeatureParser.parseResList(genObject.get("genSurface"), list, false)) {
				log.warn("Entry specifies invalid genSurface for 'decoration' generator! Using grass!");
				list.clear();
				list.add(new WeightedRandomBlock(Blocks.GRASS));
			}
		}
		WorldGenDecoration r = new WorldGenDecoration(resList, clusterSize, matList, list);
		if (genObject.has("genSky")) {
			r.seeSky = genObject.get("genSky").getAsBoolean();
		}
		if (genObject.has("checkStay")) {
			r.checkStay = genObject.get("checkStay").getAsBoolean();
		}
		if (genObject.has("stackHeight")) {
			r.stackHeight = genObject.get("stackHeight").getAsInt();
		}
		if (genObject.has("xVariance")) {
			r.xVar = MathHelper.clamp(genObject.get("xVariance").getAsInt(), 1, 15);
		}
		if (genObject.has("yVariance")) {
			r.yVar = MathHelper.clamp(genObject.get("yVariance").getAsInt(), 0, 15);
		}
		if (genObject.has("zVariance")) {
			r.zVar = MathHelper.clamp(genObject.get("zVariance").getAsInt(), 1, 15);
		}
		return r;
	}

	@Override
	protected List<WeightedRandomBlock> generateDefaultMaterial() {

		return Collections.singletonList(new WeightedRandomBlock(Blocks.AIR, -1));
	}

	@Override
	protected String getDefaultTemplate() {

		return "decoration";
	}

}
