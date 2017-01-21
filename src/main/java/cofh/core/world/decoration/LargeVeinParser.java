package cofh.core.world.decoration;

import cofh.api.world.IGeneratorParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenMinableLargeVein;
import com.google.gson.JsonObject;
import com.typesafe.config.Config;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class LargeVeinParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String generatorName, Config genObject, Logger log, List<WeightedRandomBlock> resList, int clusterSize, List<WeightedRandomBlock> matList) {

		boolean sparse = true;
		{
			sparse = genObject.hasPath("sparse") ? genObject.getBoolean("sparse") : sparse;
		}
		return new WorldGenMinableLargeVein(resList, clusterSize, matList, sparse);
	}

}
