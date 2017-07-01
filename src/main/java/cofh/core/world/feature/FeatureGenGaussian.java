package cofh.core.world.feature;

import cofh.core.util.numbers.ConstantProvider;
import cofh.core.util.numbers.INumberProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class FeatureGenGaussian extends FeatureBase {

	final WorldGenerator worldGen;
	final INumberProvider count;
	final INumberProvider rolls;
	final INumberProvider meanY;
	final INumberProvider maxVar;

	public FeatureGenGaussian(String name, WorldGenerator worldGen, int count, int smoothness, int meanY, int maxVar, GenRestriction biomeRes, boolean regen, GenRestriction dimRes) {

		this(name, worldGen, new ConstantProvider(count), new ConstantProvider(smoothness), new ConstantProvider(meanY), new ConstantProvider(maxVar), biomeRes, regen, dimRes);
	}

	public FeatureGenGaussian(String name, WorldGenerator worldGen, INumberProvider count, INumberProvider smoothness, INumberProvider meanY, INumberProvider maxVar, GenRestriction biomeRes, boolean regen, GenRestriction dimRes) {

		super(name, biomeRes, regen, dimRes);
		this.worldGen = worldGen;
		this.count = count;
		this.rolls = smoothness;
		this.meanY = meanY;
		this.maxVar = maxVar;
	}

	@Override
	public boolean generateFeature(Random random, int blockX, int blockZ, World world) {

		BlockPos pos = new BlockPos(blockX, 64, blockZ);

		final int count = this.count.intValue(world, random, pos);
		final int meanY = this.meanY.intValue(world, random, pos);

		boolean generated = false;
		for (int i = 0; i < count; i++) {
			int x = blockX + random.nextInt(16);
			int y = meanY;
			final int maxVar = this.maxVar.intValue(world, random, pos);
			if (maxVar > 1) {
				final int rolls = this.rolls.intValue(world, random, pos);
				for (int v = 0; v < rolls; ++v) {
					y += random.nextInt(maxVar);
				}
				y = Math.round(y - (maxVar * (rolls * .5f)));
			}
			int z = blockZ + random.nextInt(16);
			if (!canGenerateInBiome(world, x, z, random)) {
				continue;
			}

			generated |= worldGen.generate(world, random, new BlockPos(x, y, z));
		}
		return generated;
	}

}
