package cofh.core.world.feature;

import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.world.feature.FeatureBase;
import cofh.lib.world.feature.FeatureBase.GenRestriction;
import cofh.lib.world.feature.FeatureOreGenSurface;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.feature.WorldGenerator;

public class SurfaceParser extends UniformParser {

	@Override
	protected List<WeightedRandomBlock> generateDefaultMaterial() {

		return Arrays.asList(new WeightedRandomBlock(new ItemStack(Blocks.stone, 1, -1)),
				new WeightedRandomBlock(new ItemStack(Blocks.dirt, 1, -1)),
				new WeightedRandomBlock(new ItemStack(Blocks.grass, 1, -1)),
				new WeightedRandomBlock(new ItemStack(Blocks.sand, 1, -1)),
				new WeightedRandomBlock(new ItemStack(Blocks.gravel, 1, -1)),
				new WeightedRandomBlock(new ItemStack(Blocks.snow, 1, -1)),
				new WeightedRandomBlock(new ItemStack(Blocks.air, 1, -1)),
				new WeightedRandomBlock(new ItemStack(Blocks.water, 1, -1)));
	}

	@Override
	protected FeatureBase getFeature(String name, WorldGenerator gen, int numClusters, int minHeight, int maxHeight, GenRestriction biomeRes, boolean retrogen, GenRestriction dimRes) {

		return new FeatureOreGenSurface(name, gen, numClusters, minHeight, biomeRes, retrogen, dimRes);
	}

	@Override
	protected int parseMinHeight(JsonObject genObject) {

		return MathHelper.clampI(genObject.get("chunkChance").getAsInt(), 1, 1000000);
	}

	@Override
	protected int parseMaxHeight(JsonObject genObject) {

		return 0;
	}

}
