package cofh.core.world.feature;

import cofh.api.world.IGeneratorParser;
import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenDecoration;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenerator;

import org.apache.logging.log4j.Logger;

public class DecorationParser extends SurfaceParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String generatorName, JsonObject genObject, Logger log,
			List<WeightedRandomBlock> resList, int clusterSize, List<WeightedRandomBlock> matList) {

		ArrayList<WeightedRandomBlock> list = new ArrayList<WeightedRandomBlock>();
		if (!genObject.has("genSurface")) {
			log.info("Entry does not specify genSurface for 'decoration' generator. Using grass.");
			list.add(new WeightedRandomBlock(Blocks.grass));
		} else {
			if (!FeatureParser.parseResList(genObject.get("genSurface"), list)) {
				log.warn("Entry specifies invalid genSurface for 'decoration' generator! Using grass!");
				list.clear();
				list.add(new WeightedRandomBlock(Blocks.grass));
			}
		}
		WorldGenDecoration r = new WorldGenDecoration(resList, clusterSize, matList, list);
		if (genObject.has("genSky"))
			r.seeSky = genObject.get("genSky").getAsBoolean();
		if (genObject.has("checkStay"))
			r.checkStay = genObject.get("checkStay").getAsBoolean();
		if (genObject.has("stackHeight"))
			r.stackHeight = genObject.get("stackHeight").getAsInt();
		if (genObject.has("xVariance"))
			r.xVar = genObject.get("xVariance").getAsInt();
		if (genObject.has("yVariance"))
			r.yVar = genObject.get("yVariance").getAsInt();
		if (genObject.has("zVariance"))
			r.zVar = genObject.get("zVariance").getAsInt();
		return r;
	}

	@Override
	protected List<WeightedRandomBlock> generateDefaultMaterial() {

		return Arrays.asList(new WeightedRandomBlock(Blocks.air, -1));
	}

	@Override
	protected String getDefaultTemplate() {
		return "decoration";
	}

}
