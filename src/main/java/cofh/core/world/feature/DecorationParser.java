package cofh.core.world.feature;

import cofh.api.world.IGeneratorParser;
import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.world.WorldGenDecoration;
import com.google.gson.JsonObject;
import com.typesafe.config.Config;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DecorationParser extends SurfaceParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String generatorName, Config genObject, Logger log, List<WeightedRandomBlock> resList, int clusterSize, List<WeightedRandomBlock> matList) {

		ArrayList<WeightedRandomBlock> list = new ArrayList<WeightedRandomBlock>();
		if (!genObject.hasPath("genSurface")) {
			log.info("Entry does not specify genSurface for 'decoration' generator. Using grass.");
			list.add(new WeightedRandomBlock(Blocks.GRASS));
		} else {
			if (!FeatureParser.parseResList(genObject.root().get("genSurface"), list, false)) {
				log.warn("Entry specifies invalid genSurface for 'decoration' generator! Using grass!");
				list.clear();
				list.add(new WeightedRandomBlock(Blocks.GRASS));
			}
		}
		WorldGenDecoration r = new WorldGenDecoration(resList, clusterSize, matList, list);
		if (genObject.hasPath("genSky")) {
			r.seeSky = genObject.getBoolean("genSky");
		}
		if (genObject.hasPath("checkStay")) {
			r.checkStay = genObject.getBoolean("checkStay");
		}
		if (genObject.hasPath("stackHeight")) {
			r.stackHeight = genObject.getInt("stackHeight");
		}
		if (genObject.hasPath("xVariance")) {
			r.xVar = MathHelper.clamp(genObject.getInt("xVariance"), 1, 15);
		}
		if (genObject.hasPath("yVariance")) {
			r.yVar = MathHelper.clamp(genObject.getInt("yVariance"), 0, 15);
		}
		if (genObject.hasPath("zVariance")) {
			r.zVar = MathHelper.clamp(genObject.getInt("zVariance"), 1, 15);
		}
		return r;
	}

	@Override
	protected List<WeightedRandomBlock> generateDefaultMaterial() {

		return Collections.singletonList(new WeightedRandomBlock(Blocks.AIR, -1));
	}

	@Override
	protected String getDefaultGenerator() {

		return "decoration";
	}

}
