package cofh.core.world.feature;

import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.feature.FeatureBase;
import cofh.lib.world.feature.FeatureBase.GenRestriction;
import cofh.lib.world.feature.FeatureGenSurface;
import cofh.lib.world.feature.FeatureGenTopBlock;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenerator;

import org.apache.logging.log4j.Logger;

public class SurfaceParser extends UniformParser {

	@Override
	protected List<WeightedRandomBlock> generateDefaultMaterial() {

		return Arrays.asList(new WeightedRandomBlock(Blocks.STONE, -1), new WeightedRandomBlock(Blocks.DIRT, -1), new WeightedRandomBlock(Blocks.GRASS, -1),
				new WeightedRandomBlock(Blocks.SAND, -1), new WeightedRandomBlock(Blocks.GRAVEL, -1), new WeightedRandomBlock(Blocks.SNOW, -1),
				new WeightedRandomBlock(Blocks.AIR, -1), new WeightedRandomBlock(Blocks.WATER, -1));
	}

	@Override
	protected FeatureBase getFeature(String featureName, JsonObject genObject, WorldGenerator gen, List<WeightedRandomBlock> matList, int numClusters,
			GenRestriction biomeRes, boolean retrogen, GenRestriction dimRes, Logger log) {

		if (genObject.has("followTerrain") && genObject.get("followTerrain").getAsBoolean()) {
			return new FeatureGenTopBlock(featureName, gen, matList, numClusters, biomeRes, retrogen, dimRes);
		} else {
			return new FeatureGenSurface(featureName, gen, matList, numClusters, biomeRes, retrogen, dimRes);
		}
	}

}
