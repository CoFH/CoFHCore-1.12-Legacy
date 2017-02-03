package cofh.core.world.feature;

import cofh.api.world.IGeneratorParser;
import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenDecoration;
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
		if (!genObject.hasPath("surface")) {
			log.info("Entry does not specify surface for 'decoration' generator. Using grass.");
			list.add(new WeightedRandomBlock(Blocks.GRASS));
		} else {
			if (!FeatureParser.parseResList(genData.get("surface"), list, false)) {
				log.warn("Entry specifies invalid surface for 'decoration' generator! Using grass!");
				list.clear();
				list.add(new WeightedRandomBlock(Blocks.GRASS));
			}
		}
		WorldGenDecoration r = new WorldGenDecoration(resList, clusterSize, matList, list);
		if (genObject.hasPath("see-sky")) {
			r.setSeeSky(genObject.getBoolean("see-sky"));
		}
		if (genObject.hasPath("check-stay")) {
			r.setCheckStay(genObject.getBoolean("check-stay"));
		}
		if (genObject.hasPath("stack-height")) {
			r.setStackHeight(FeatureParser.parseNumberValue(genData.get("stack-height")));
		}
		if (genObject.hasPath("x-variance")) {
			r.setXVar(FeatureParser.parseNumberValue(genData.get("x-variance"), 1, 15));
		}
		if (genObject.hasPath("y-variance")) {
			r.setYVar(FeatureParser.parseNumberValue(genData.get("y-variance"), 0, 15));
		}
		if (genObject.hasPath("z-variance")) {
			r.setZVar(FeatureParser.parseNumberValue(genData.get("z-variance"), 1, 15));
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
