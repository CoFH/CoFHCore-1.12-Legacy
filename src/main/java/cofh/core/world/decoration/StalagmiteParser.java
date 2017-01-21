package cofh.core.world.decoration;

import cofh.api.world.IGeneratorParser;
import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenStalactite;
import cofh.lib.world.WorldGenStalagmite;
import com.google.gson.JsonObject;
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

		ArrayList<WeightedRandomBlock> list = new ArrayList<WeightedRandomBlock>();
		if (!genObject.hasPath("genBody")) {
			log.info("Entry does not specify genBody for 'stalagmite' generator. Using air.");
			list.add(new WeightedRandomBlock(Blocks.AIR));
		} else {
			if (!FeatureParser.parseResList(genObject.root().get("genBody"), list, false)) {
				log.warn("Entry specifies invalid genBody for 'stalagmite' generator! Using air!");
				list.clear();
				list.add(new WeightedRandomBlock(Blocks.AIR));
			}
		}
		WorldGenStalagmite r = stalactite ? new WorldGenStalactite(resList, matList, list) : new WorldGenStalagmite(resList, matList, list);
		{
			if (genObject.hasPath("minHeight")) {
				r.minHeight = genObject.getInt("minHeight");
			}
			if (genObject.hasPath("heightVariance")) {
				r.heightVariance = genObject.getInt("heightVariance");
			}
			if (genObject.hasPath("sizeVariance")) {
				r.sizeVariance = genObject.getInt("sizeVariance");
			}
			if (genObject.hasPath("heightMod")) {
				r.heightMod = genObject.getInt("heightMod");
			}
			if (genObject.hasPath("genSize")) {
				r.genSize = genObject.getInt("genSize");
			}
			if (genObject.hasPath("smooth")) {
				r.smooth = genObject.getBoolean("smooth");
			}
			if (genObject.hasPath("fat")) {
				r.fat = genObject.getBoolean("fat");
			}
			if (genObject.hasPath("altSinc")) {
				r.altSinc = genObject.getBoolean("altSinc");
			}
		}
		return r;
	}

}
