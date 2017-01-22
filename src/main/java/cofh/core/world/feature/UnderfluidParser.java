package cofh.core.world.feature;

import cofh.core.world.FeatureParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.numbers.INumberProvider;
import cofh.lib.world.feature.FeatureBase;
import cofh.lib.world.feature.FeatureBase.GenRestriction;
import cofh.lib.world.feature.FeatureGenUnderfluid;
import com.google.gson.JsonObject;
import com.typesafe.config.Config;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.DungeonHooks.DungeonMob;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnderfluidParser extends UniformParser {

	private boolean isUnderwater;

	public UnderfluidParser(boolean water) {

		isUnderwater = water;
	}

	@Override
	protected List<WeightedRandomBlock> generateDefaultMaterial() {

		return Arrays.asList(new WeightedRandomBlock(Blocks.DIRT, -1), new WeightedRandomBlock(Blocks.GRASS, -1));
	}

	@Override
	protected FeatureBase getFeature(String featureName, Config genObject, WorldGenerator gen, INumberProvider numClusters, GenRestriction biomeRes, boolean retrogen, GenRestriction dimRes, Logger log) {

		boolean water = true;
		int[] fluidList = null;
		l:
		if (genObject.hasPath("genFluid")) {
			ArrayList<DungeonMob> list = new ArrayList<DungeonMob>();
			if (!FeatureParser.parseWeightedStringList(genObject.root().get("genFluid"), list)) {
				break l;
			}
			water = false;
			TIntHashSet ints = new TIntHashSet();
			for (DungeonMob str : list) {
				// ints.add(FluidRegistry.getFluidID(str.type));
				// NOPE. this NPEs.
				Fluid fluid = FluidRegistry.getFluid(str.type);
				if (fluid != null) {
					ints.add(FluidRegistry.getFluidID(fluid));
				}
			}
			fluidList = ints.toArray();
		}

		// TODO: WorldGeneratorAdv that allows access to its material list
		List<WeightedRandomBlock> matList = defaultMaterial;
		if (genObject.hasPath("material")) {
			matList = new ArrayList<WeightedRandomBlock>();
			if (!FeatureParser.parseResList(genObject.root().get("material"), matList, false)) {
				log.warn("Invalid material list! Using default list.");
				matList = defaultMaterial;
			}
		}
		if (water) {
			return new FeatureGenUnderfluid(featureName, gen, matList, numClusters, biomeRes, retrogen, dimRes);
		} else {
			return new FeatureGenUnderfluid(featureName, gen, matList, fluidList, numClusters, biomeRes, retrogen, dimRes);
		}
	}

	@Override
	protected String getDefaultGenerator() {

		return isUnderwater ? "plate" : super.getDefaultGenerator();
	}

}
