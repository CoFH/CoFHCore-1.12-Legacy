package cofh.lib.world.feature;

import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.FluidHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fluids.Fluid;

public class FeatureGenUnderfluid extends FeatureBase {

	final boolean water;
	final WorldGenerator worldGen;
	final int count;
	final List<WeightedRandomBlock> matList;
	final int[] fluidList;

	public FeatureGenUnderfluid(String name, WorldGenerator worldGen, List<WeightedRandomBlock> matList, int count, GenRestriction biomeRes, boolean regen,
			GenRestriction dimRes) {

		super(name, biomeRes, regen, dimRes);
		this.worldGen = worldGen;
		this.count = count;
		this.matList = matList;
		water = true;
		fluidList = null;
	}

	public FeatureGenUnderfluid(String name, WorldGenerator worldGen, List<WeightedRandomBlock> matList, int[] fluidList, int count, GenRestriction biomeRes,
			boolean regen, GenRestriction dimRes) {

		super(name, biomeRes, regen, dimRes);
		this.worldGen = worldGen;
		this.count = count;
		this.matList = matList;
		water = false;
		Arrays.sort(fluidList);
		this.fluidList = fluidList;
	}

	@Override
	public boolean generateFeature(Random random, int chunkX, int chunkZ, World world) {

		int blockX = chunkX * 16;
		int blockZ = chunkZ * 16;

		boolean generated = false;
		for (int i = 0; i < count; i++) {
			int x = blockX + random.nextInt(16);
			int z = blockZ + random.nextInt(16);
			if (!canGenerateInBiome(world, x, z, random)) {
				continue;
			}

			int y = BlockHelper.getSurfaceBlockY(world, x, z);
			l: do {
				Block block = world.getBlock(x, y, z);
				if (water) {
					if (block.getMaterial() == Material.water) {
						continue;
					}
					if (world.getBlock(x, y + 1, z).getMaterial() != Material.water) {
						continue;
					}
				} else {
					Fluid fluid = FluidHelper.lookupFluidForBlock(block);
					if (fluid != null && Arrays.binarySearch(fluidList, fluid.getID()) >= 0) {
						continue;
					}

					fluid = FluidHelper.lookupFluidForBlock(world.getBlock(x, y + 1, z));
					if (fluid == null || Arrays.binarySearch(fluidList, fluid.getID()) < 0) {
						continue;
					}
				}
				for (WeightedRandomBlock mat : matList) {
					if (block.isReplaceableOreGen(world, x, y, z, mat.block)) {
						break l;
					}
				}
			} while (y-- > 1);

			if (y > 0) {
				generated |= worldGen.generate(world, random, x, y, z);
			}
		}
		return generated;
	}

}
