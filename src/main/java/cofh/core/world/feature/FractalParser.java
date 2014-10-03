package cofh.core.world.feature;

import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.world.feature.FeatureBase;
import cofh.lib.world.feature.FeatureBase.GenRestriction;
import cofh.lib.world.feature.FeatureGenLargeVein;
import com.google.gson.JsonObject;

import java.util.List;

import net.minecraft.world.gen.feature.WorldGenerator;

import org.apache.logging.log4j.Logger;

public class FractalParser extends UniformParser {

	@Override
	protected FeatureBase getFeature(String featureName, JsonObject genObject, WorldGenerator gen, List<WeightedRandomBlock> matList, int numClusters, GenRestriction biomeRes, boolean retrogen,
			GenRestriction dimRes, Logger log) {

		int rarity = MathHelper.clampI(genObject.get("chunkChance").getAsInt(), 1, 1000000);
		int minY = genObject.get("minHeight").getAsInt();
		int h = genObject.get("veinHeight").getAsInt();
		int d = genObject.get("veinDiameter").getAsInt();
		int vD= genObject.get("verticalDensity").getAsInt();
		int hD = genObject.get("horizontalDensity").getAsInt();

		return new FeatureGenLargeVein(featureName, gen, numClusters, minY, rarity, biomeRes, retrogen, dimRes, h, d, vD, hD);
	}
	
	@Override
	protected String getDefaultTemplate() {
		return "large-vein";
	}

}
