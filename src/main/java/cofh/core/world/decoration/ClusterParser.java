package cofh.core.world.decoration;

import cofh.api.world.IGeneratorParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenMinableCluster;
import cofh.lib.world.WorldGenSparseMinableCluster;
import com.google.gson.JsonObject;

import java.util.List;

import net.minecraft.world.gen.feature.WorldGenerator;

import org.apache.logging.log4j.Logger;

public class ClusterParser implements IGeneratorParser {

	private final boolean sparse;

	public ClusterParser(boolean sparse) {

		this.sparse = sparse;
	}

	@Override
	public WorldGenerator parseGenerator(String generatorName, JsonObject genObject, Logger log,
			List<WeightedRandomBlock> resList, int clusterSize, List<WeightedRandomBlock> matList) {

		if (sparse) {
			return new WorldGenSparseMinableCluster(resList, clusterSize, matList);
		}

		return new WorldGenMinableCluster(resList, clusterSize, matList);
	}

}
