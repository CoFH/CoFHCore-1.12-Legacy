package cofh.core.world.decoration;

import cofh.api.world.IGeneratorParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenBoulder;
import com.google.gson.JsonObject;

import java.util.List;

import net.minecraft.world.gen.feature.WorldGenerator;

import org.apache.logging.log4j.Logger;

public class BoulderParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String generatorName, JsonObject genObject, Logger log,
			List<WeightedRandomBlock> resList, int clusterSize, List<WeightedRandomBlock> matList) {

		WorldGenBoulder r = new WorldGenBoulder(resList, clusterSize, matList);
		{
			if (genObject.has("sizeVariance"))
				r.sizeVariance = genObject.get("sizeVariance").getAsInt();
			if (genObject.has("count"))
				r.clusters = genObject.get("count").getAsInt();
			if (genObject.has("countVariance"))
				r.clusterVariance = genObject.get("countVariance").getAsInt();
		}
		return r;
	}

}
