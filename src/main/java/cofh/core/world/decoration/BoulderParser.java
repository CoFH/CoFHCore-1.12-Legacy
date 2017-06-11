package cofh.core.world.decoration;

import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.IGeneratorParser;
import cofh.lib.world.WorldGenBoulder;
import com.typesafe.config.Config;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class BoulderParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String name, Config genObject, Logger log, List<WeightedRandomBlock> resList, List<WeightedRandomBlock> matList) {

		int clusterSize = genObject.getInt("diameter");
		if (clusterSize <= 0) {
			log.warn("Invalid diameter for generator '%s'", name);
			return null;
		}

		WorldGenBoulder r = new WorldGenBoulder(resList, clusterSize, matList);
		{
			if (genObject.hasPath("size-variance")) {
				r.sizeVariance = genObject.getInt("size-variance");
			}
			if (genObject.hasPath("count")) {
				r.clusters = genObject.getInt("count");
			}
			if (genObject.hasPath("count-variance")) {
				r.clusterVariance = genObject.getInt("count-variance");
			}
			if (genObject.hasPath("hollow")) {
				r.hollow = genObject.getBoolean("hollow");
			}
			if (genObject.hasPath("hollow-size")) {
				r.hollowAmt = (float) genObject.getDouble("hollow-size");
			}
			if (genObject.hasPath("hollow-variance")) {
				r.hollowVar = (float) genObject.getDouble("hollow-variance");
			}
		}
		return r;
	}

}
