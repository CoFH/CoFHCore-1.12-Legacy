package cofh.core.world.decoration;

import cofh.lib.world.IGeneratorParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenMinableLargeVein;
import com.typesafe.config.Config;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class LargeVeinParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String name, Config genObject, Logger log, List<WeightedRandomBlock> resList, List<WeightedRandomBlock> matList) {

		int clusterSize = genObject.getInt("cluster-size");
		if (clusterSize <= 0) {
			log.warn("Invalid cluster size for generator '%s'", name);
			return null;
		}

		boolean sparse = true;
		{
			sparse = genObject.hasPath("sparse") ? genObject.getBoolean("sparse") : sparse;
		}
		return new WorldGenMinableLargeVein(resList, clusterSize, matList, sparse);
	}

}
