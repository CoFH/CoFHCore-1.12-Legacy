package cofh.core.world.decoration;

import cofh.api.world.IGeneratorParser;
import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenStalactite;
import cofh.lib.world.WorldGenStalagmite;
import com.typesafe.config.Config;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class StalagmiteParser implements IGeneratorParser {

	private final boolean stalactite;

	public StalagmiteParser(boolean stalactite) {

		this.stalactite = stalactite;
	}

	@Override
	public WorldGenerator parseGenerator(String generatorName, Config genObject, Logger log, List<WeightedRandomBlock> resList, int clusterSize, List<WeightedRandomBlock> matList) {

		// TODO: these names need revised
		ArrayList<WeightedRandomBlock> list = new ArrayList<WeightedRandomBlock>();
		if (!genObject.hasPath("gen-body")) {
			log.info("Entry does not specify gen body for 'stalagmite' generator. Using air.");
			list.add(new WeightedRandomBlock(Blocks.AIR));
		} else {
			if (!FeatureParser.parseResList(genObject.root().get("gen-body"), list, false)) {
				log.warn("Entry specifies invalid gen body for 'stalagmite' generator! Using air!");
				list.clear();
				list.add(new WeightedRandomBlock(Blocks.AIR));
			}
		}
		WorldGenStalagmite r = stalactite ? new WorldGenStalactite(resList, matList, list) : new WorldGenStalagmite(resList, matList, list);
		{
			if (genObject.hasPath("min-height")) {
				r.minHeight = genObject.getInt("min-height");
			}
			if (genObject.hasPath("height-variance")) {
				r.heightVariance = genObject.getInt("height-variance");
			}
			if (genObject.hasPath("size-variance")) {
				r.sizeVariance = genObject.getInt("size-variance");
			}
			if (genObject.hasPath("height-mod")) {
				r.heightMod = genObject.getInt("height-mod");
			}
			if (genObject.hasPath("gen-size")) {
				r.genSize = genObject.getInt("gen-size");
			}
			if (genObject.hasPath("smooth")) {
				r.smooth = genObject.getBoolean("smooth");
			}
			if (genObject.hasPath("fat")) {
				r.fat = genObject.getBoolean("fat");
			}
			if (genObject.hasPath("alt-sinc")) {
				r.altSinc = genObject.getBoolean("alt-sinc");
			}
		}
		return r;
	}

}
