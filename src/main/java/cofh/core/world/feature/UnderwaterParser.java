package cofh.core.world.feature;

import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.world.feature.FeatureBase;
import cofh.lib.world.feature.FeatureBase.GenRestriction;
import cofh.lib.world.feature.FeatureGenUnderwater;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenerator;

public class UnderwaterParser extends UniformParser {

	@Override
	protected List<WeightedRandomBlock> generateDefaultMaterial() {

		return Arrays.asList(new WeightedRandomBlock(Blocks.dirt, -1), new WeightedRandomBlock(Blocks.grass, -1));
	}

	@Override
	protected FeatureBase getFeature(String name, WorldGenerator gen, List<WeightedRandomBlock> matList, int numClusters, int minHeight, int maxHeight, GenRestriction biomeRes, boolean retrogen,
			GenRestriction dimRes) {

		return new FeatureGenUnderwater(name, gen, matList, numClusters, minHeight, biomeRes, retrogen, dimRes);
	}

	@Override
	protected int parseMinHeight(JsonObject genObject) {

		return MathHelper.clampI(genObject.get("chunkChance").getAsInt(), 1, 1000000);
	}

	@Override
	protected int parseMaxHeight(JsonObject genObject) {

		return 0;
	}

	@Override
	protected boolean verifyHeight(int minHeight, int maxHeight) {

		return false;
	}

}
