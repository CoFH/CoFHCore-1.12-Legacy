package cofh.core.world.feature;

import cofh.core.util.WeightedRandomBlock;
import cofh.core.util.helpers.BlockHelper;
import cofh.core.util.numbers.ConstantProvider;
import cofh.core.util.numbers.INumberProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.List;
import java.util.Random;

import static cofh.core.world.WorldGenMinableCluster.canGenerateInBlock;

public class FeatureGenTopBlock extends FeatureBase {

	final WorldGenerator worldGen;
	final INumberProvider count;
	final WeightedRandomBlock[] matList;

	public FeatureGenTopBlock(String name, WorldGenerator worldGen, List<WeightedRandomBlock> matList, int count, GenRestriction biomeRes, boolean regen, GenRestriction dimRes) {

		this(name, worldGen, matList, new ConstantProvider(count), biomeRes, regen, dimRes);
	}

	public FeatureGenTopBlock(String name, WorldGenerator worldGen, List<WeightedRandomBlock> matList, INumberProvider count, GenRestriction biomeRes, boolean regen, GenRestriction dimRes) {

		super(name, biomeRes, regen, dimRes);
		this.worldGen = worldGen;
		this.count = count;
		this.matList = matList.toArray(new WeightedRandomBlock[matList.size()]);
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

			int y = BlockHelper.getTopBlockY(world, x, z);
			l:
			{
				IBlockState state = world.getBlockState(new BlockPos(x, y, z));
				if (!state.getBlock().isAir(state, world, new BlockPos(x, y, z)) && canGenerateInBlock(world, x, y, z, matList)) {
					break l;
				}
				continue;
			}

			generated |= worldGen.generate(world, random, new BlockPos(x, y + 1, z));
		}
		return generated;
	}

}
