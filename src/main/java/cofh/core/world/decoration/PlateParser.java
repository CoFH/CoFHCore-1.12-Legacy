package cofh.core.world.decoration;

import cofh.api.world.IGeneratorParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.world.WorldGenMinablePlate;
import com.google.gson.JsonObject;

import java.util.List;

import net.minecraft.world.gen.feature.WorldGenerator;

import org.apache.logging.log4j.Logger;


public class PlateParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String generatorName, JsonObject genObject, Logger log,
			List<WeightedRandomBlock> resList, int clusterSize, List<WeightedRandomBlock> matList) {

		WorldGenMinablePlate r = new WorldGenMinablePlate(resList, MathHelper.clampI(clusterSize, 0, 32), matList);
		{
			if (genObject.has("sizeVariance"))
				r.variation = (byte) MathHelper.clampI(genObject.get("sizeVariance").getAsInt(), 0, 16);
			if (genObject.has("height"))
				r.height = (byte) MathHelper.clampI(genObject.get("height").getAsInt(), 0, 64);
			if (genObject.has("slim"))
				r.slim = genObject.get("slim").getAsBoolean();
		}
		return r;
	}

}
