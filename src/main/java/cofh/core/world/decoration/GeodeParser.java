package cofh.core.world.decoration;

import cofh.api.world.IGeneratorParser;
import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenGeode;
import com.google.gson.JsonObject;
import com.typesafe.config.Config;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class GeodeParser implements IGeneratorParser {

	@Override
	public WorldGenerator parseGenerator(String generatorName, Config genObject, Logger log, List<WeightedRandomBlock> resList, int clusterSize, List<WeightedRandomBlock> matList) {

		ArrayList<WeightedRandomBlock> list = new ArrayList<WeightedRandomBlock>();
		if (!genObject.hasPath("crust")) {
			log.info("Entry does not specify crust for 'geode' generator. Using stone.");
			list.add(new WeightedRandomBlock(Blocks.STONE));
		} else {
			if (!FeatureParser.parseResList(genObject.root().get("crust"), list, true)) {
				log.warn("Entry specifies invalid crust for 'geode' generator! Using obsidian!");
				list.clear();
				list.add(new WeightedRandomBlock(Blocks.OBSIDIAN));
			}
		}
		WorldGenGeode r = new WorldGenGeode(resList, matList, list);
		{
			if (genObject.hasPath("hollow")) {
				r.setHollow(genObject.getBoolean("hollow"));
			}
			if (genObject.hasPath("filler")) {
				list = new ArrayList<WeightedRandomBlock>();
				if (!FeatureParser.parseResList(genObject.getValue("filler"), list, true)) {
					log.warn("Entry specifies invalid filler for 'geode' generator! Not filling!");
				} else {
					r.setFillBlock(list);
				}
			}
		}
		return r;
	}

}
