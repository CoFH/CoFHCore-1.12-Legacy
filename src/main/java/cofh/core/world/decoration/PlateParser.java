package cofh.core.world.decoration;

import cofh.lib.world.IGeneratorParser;
import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.world.WorldGenMinablePlate;
import com.typesafe.config.Config;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class PlateParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String name, Config genObject, Logger log, List<WeightedRandomBlock> resList, List<WeightedRandomBlock> matList) {

		int clusterSize = genObject.getInt("radius");
		if (clusterSize <= 0) {
			log.warn("Invalid radius for generator '%s'", name);
			return null;
		}

		WorldGenMinablePlate r = new WorldGenMinablePlate(resList, MathHelper.clamp(clusterSize, 0, 32), matList);
		{
			if (genObject.hasPath("height")) {
				r.setHeight(FeatureParser.parseNumberValue(genObject.root().get("height"), 0, 64));
			}
			if (genObject.hasPath("slim")) {
				r.setSlim(genObject.getBoolean("slim"));
			}
		}
		return r;
	}

}
