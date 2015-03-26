package cofh.core.world.decoration;

import cofh.api.world.IGeneratorParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenMinableLargeVein;
import com.google.gson.JsonObject;

import java.util.List;

import net.minecraft.world.gen.feature.WorldGenerator;

import org.apache.logging.log4j.Logger;

public class LargeVeinParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String generatorName, JsonObject genObject, Logger log, List<WeightedRandomBlock> resList, int clusterSize,
			List<WeightedRandomBlock> matList) {

		boolean sparse = true;
		{
			sparse = genObject.has("sparse") ? genObject.get("sparse").getAsBoolean() : sparse;
		}
		return new WorldGenMinableLargeVein(resList, clusterSize, matList, sparse);
	}

}
