package cofh.lib.world;

import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.numbers.ConstantProvider;
import cofh.lib.util.numbers.INumberProvider;
import cofh.lib.util.numbers.SkellamRandomProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.List;
import java.util.Random;

import static cofh.lib.world.WorldGenMinableCluster.canGenerateInBlock;
import static cofh.lib.world.WorldGenMinableCluster.selectBlock;

public class WorldGenDecoration extends WorldGenerator {

	private final List<WeightedRandomBlock> cluster;
	private final WeightedRandomBlock[] genBlock;
	private final WeightedRandomBlock[] onBlock;
	private final INumberProvider clusterSize;
	private boolean seeSky = true;
	private boolean checkStay = true;
	private INumberProvider stackHeight;
	private INumberProvider xVar;
	private INumberProvider yVar;
	private INumberProvider zVar;

	public WorldGenDecoration(List<WeightedRandomBlock> blocks, int count, List<WeightedRandomBlock> material, List<WeightedRandomBlock> on) {

		this(blocks, new ConstantProvider(count), material, on);
	}

	public WorldGenDecoration(List<WeightedRandomBlock> blocks, INumberProvider count, List<WeightedRandomBlock> material, List<WeightedRandomBlock> on) {

		cluster = blocks;
		clusterSize = count;
		genBlock = material == null ? null : material.toArray(new WeightedRandomBlock[material.size()]);
		onBlock = on == null ? null : on.toArray(new WeightedRandomBlock[on.size()]);
		this.setStackHeight(1);
		this.setXVar(new SkellamRandomProvider(8));
		this.setYVar(new SkellamRandomProvider(4));
		this.setZVar(new SkellamRandomProvider(8));
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {

		int xStart = pos.getX();
		int yStart = pos.getY();
		int zStart = pos.getZ();

		final int clusterSize = this.clusterSize.intValue(world, rand, pos);

		boolean r = false;
		for (int l = clusterSize; l-- > 0; ) {
			int x = xStart + xVar.intValue(world, rand, pos);
			int y = yStart + yVar.intValue(world, rand, pos);
			int z = zStart + zVar.intValue(world, rand, pos);

			if (!world.isBlockLoaded(new BlockPos(x, y, z))) {
				++l;
				continue;
			}

			if ((!seeSky || world.canSeeSky(new BlockPos(x, y, z))) && canGenerateInBlock(world, x, y - 1, z, onBlock) && canGenerateInBlock(world, x, y, z, genBlock)) {

				WeightedRandomBlock block = selectBlock(world, cluster);
				int stack = stackHeight.intValue(world, rand, pos);
				do {
					// TODO: checkStay logic
					if (!checkStay /*|| block.block.canBlockStay(world, x, y, z) Moved to BlockBush...*/) {
						r |= world.setBlockState(new BlockPos(x, y, z), block.getState(), 2);
					} else {
						break;
					}
					++y;
					if (!canGenerateInBlock(world, x, y, z, genBlock)) {
						break;
					}
				} while (--stack > 0);
			}
		}
		return r;
	}

	public WorldGenDecoration setSeeSky(boolean seeSky) {

		this.seeSky = seeSky;
		return this;
	}

	public WorldGenDecoration setCheckStay(boolean checkStay) {

		this.checkStay = checkStay;
		return this;
	}

	public WorldGenDecoration setStackHeight(int stackHeight) {

		return this.setStackHeight(new ConstantProvider(stackHeight));
	}

	public WorldGenDecoration setStackHeight(INumberProvider stackHeight) {

		this.stackHeight = stackHeight;
		return this;
	}

	public WorldGenDecoration setXVar(int xVar) {

		return this.setXVar(new ConstantProvider(xVar));
	}

	public WorldGenDecoration setXVar(INumberProvider xVar) {

		this.xVar = xVar;
		return this;
	}

	public WorldGenDecoration setYVar(int yVar) {

		return this.setYVar(new ConstantProvider(yVar));
	}

	public WorldGenDecoration setYVar(INumberProvider yVar) {

		this.yVar = yVar;
		return this;
	}

	public WorldGenDecoration setZVar(int zVar) {

		return this.setZVar(new ConstantProvider(zVar));
	}

	public WorldGenDecoration setZVar(INumberProvider zVar) {

		this.zVar = zVar;
		return this;
	}

}
