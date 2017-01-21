package cofh.core.world.decoration;

import cofh.api.world.IGeneratorParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenBoulder;
import com.google.gson.JsonObject;
import com.typesafe.config.Config;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class BoulderParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String generatorName, Config genObject, Logger log, List<WeightedRandomBlock> resList, int clusterSize, List<WeightedRandomBlock> matList) {

		WorldGenBoulder r = new WorldGenBoulder(resList, clusterSize, matList);
		{
			if (genObject.hasPath("sizeVariance")) {
				r.sizeVariance = genObject.getInt("sizeVariance");
			}
			if (genObject.hasPath("count")) {
				r.clusters = genObject.getInt("count");
			}
			if (genObject.hasPath("countVariance")) {
				r.clusterVariance = genObject.getInt("countVariance");
			}
			if (genObject.hasPath("hollow")) {
				r.hollow = genObject.getBoolean("hollow");
			}
			if (genObject.hasPath("hollowSize")) {
				r.hollowAmt = (float) genObject.getDouble("hollowSize");
			}
			if (genObject.hasPath("hollowVariance")) {
				r.hollowVar = (float)genObject.getDouble("hollowVariance");
			}
		}
		return r;
	}

}
