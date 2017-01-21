package cofh.core.world.decoration;

import cofh.api.world.IGeneratorParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.world.WorldGenMinablePlate;
import com.google.gson.JsonObject;
import com.typesafe.config.Config;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class PlateParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String generatorName, Config genObject, Logger log, List<WeightedRandomBlock> resList, int clusterSize, List<WeightedRandomBlock> matList) {

		WorldGenMinablePlate r = new WorldGenMinablePlate(resList, MathHelper.clamp(clusterSize, 0, 32), matList);
		{
			if (genObject.hasPath("sizeVariance")) {
				r.variation = (byte) MathHelper.clamp(genObject.getInt("sizeVariance"), 0, 16);
			}
			if (genObject.hasPath("height")) {
				r.height = (byte) MathHelper.clamp(genObject.getInt("height"), 0, 64);
			}
			if (genObject.hasPath("slim")) {
				r.slim = genObject.getBoolean("slim");
			}
		}
		return r;
	}

}
