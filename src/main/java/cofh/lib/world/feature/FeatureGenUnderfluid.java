package cofh.lib.world.feature;

import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.numbers.ConstantProvider;
import cofh.lib.util.numbers.INumberProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fluids.Fluid;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FeatureGenUnderfluid extends FeatureBase {

	final boolean water;
	final WorldGenerator worldGen;
	final INumberProvider count;
	final List<WeightedRandomBlock> matList;
	final String[] fluidList;

	public FeatureGenUnderfluid(String name, WorldGenerator worldGen, List<WeightedRandomBlock> matList, int count, GenRestriction biomeRes, boolean regen, GenRestriction dimRes) {

		this(name, worldGen, matList, new ConstantProvider(count), biomeRes, regen, dimRes);
	}

	public FeatureGenUnderfluid(String name, WorldGenerator worldGen, List<WeightedRandomBlock> matList, String[] fluidList, int count, GenRestriction biomeRes, boolean regen, GenRestriction dimRes) {

		this(name, worldGen, matList, fluidList, new ConstantProvider(count), biomeRes, regen, dimRes);
	}

	public FeatureGenUnderfluid(String name, WorldGenerator worldGen, List<WeightedRandomBlock> matList, INumberProvider count, GenRestriction biomeRes, boolean regen, GenRestriction dimRes) {

		super(name, biomeRes, regen, dimRes);
		this.worldGen = worldGen;
		this.count = count;
		this.matList = matList;
		water = true;
		fluidList = null;
	}

	public FeatureGenUnderfluid(String name, WorldGenerator worldGen, List<WeightedRandomBlock> matList, String[] fluidList, INumberProvider count, GenRestriction biomeRes, boolean regen, GenRestriction dimRes) {

		super(name, biomeRes, regen, dimRes);
		this.worldGen = worldGen;
		this.count = count;
		this.matList = matList;
		water = false;
		this.fluidList = fluidList;
	}

	@Override
	public boolean generateFeature(Random random, int blockX, int blockZ, World world) {

		BlockPos pos = new BlockPos(blockX, 64, blockZ);

		final int count = this.count.intValue(world, random, pos);

		boolean generated = false;
		for (int i = 0; i < count; i++) {
			int x = blockX + random.nextInt(16);
			int z = blockZ + random.nextInt(16);
			if (!canGenerateInBiome(world, x, z, random)) {
				continue;
			}

			int y = BlockHelper.getSurfaceBlockY(world, x, z);
			l:
			do {
				IBlockState state = world.getBlockState(new BlockPos(x, y, z));
				if (water) {
					if (state.getMaterial() == Material.WATER) {
						continue;
					}
					if (world.getBlockState(new BlockPos(x, y + 1, z)).getMaterial() != Material.WATER) {
						continue;
					}
				} else {
					Fluid fluid = FluidHelper.lookupFluidForBlock(state.getBlock());
					if (fluid != null && Arrays.binarySearch(fluidList, fluid.getName()) >= 0) {
						continue;
					}

					fluid = FluidHelper.lookupFluidForBlock(world.getBlockState(new BlockPos(x, y + 1, z)).getBlock());
					if (fluid == null || Arrays.binarySearch(fluidList, fluid.getName()) < 0) {
						continue;
					}
				}
				for (WeightedRandomBlock mat : matList) {
					if (state.getBlock().isReplaceableOreGen(state, world, new BlockPos(x, y, z), BlockMatcher.forBlock(mat.block))) {
						break l;
					}
				}
			} while (y-- > 1);

			if (y > 0) {
				generated |= worldGen.generate(world, random, new BlockPos(x, y, z));
			}
		}
		return generated;
	}

}
