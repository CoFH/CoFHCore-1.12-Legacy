package cofh.core.world.feature;

import cofh.api.world.IGeneratorParser;
import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.world.WorldGenDecoration;
import com.google.gson.JsonObject;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
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
		ConfigObject genData = genObject.root();
		if (!genObject.hasPath("genSurface")) {
			log.info("Entry does not specify genSurface for 'decoration' generator. Using grass.");
			list.add(new WeightedRandomBlock(Blocks.GRASS));
		} else {
			if (!FeatureParser.parseResList(genData.get("genSurface"), list, false)) {
				log.warn("Entry specifies invalid genSurface for 'decoration' generator! Using grass!");
				list.clear();
				list.add(new WeightedRandomBlock(Blocks.GRASS));
			}
		}
		WorldGenDecoration r = new WorldGenDecoration(resList, clusterSize, matList, list);
		if (genObject.hasPath("genSky")) {
			r.setSeeSky(genObject.getBoolean("genSky"));
		}
		if (genObject.hasPath("checkStay")) {
			r.setCheckStay(genObject.getBoolean("checkStay"));
		}
		if (genObject.hasPath("stackHeight")) {
			r.setStackHeight(FeatureParser.parseNumberValue(genData.get("stackHeight")));
		}
		if (genObject.hasPath("xVariance")) {
			r.setXVar(FeatureParser.parseNumberValue(genData.get("xVariance"), 1, 15));
		}
		if (genObject.hasPath("yVariance")) {
			r.setYVar(FeatureParser.parseNumberValue(genData.get("yVariance"), 0, 15));
		}
		if (genObject.hasPath("zVariance")) {
			r.setZVar(FeatureParser.parseNumberValue(genData.get("zVariance"), 1, 15));
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
