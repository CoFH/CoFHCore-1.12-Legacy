package cofh.core.world.feature;

import cofh.core.util.numbers.ConstantProvider;
import cofh.core.util.numbers.INumberProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class FeatureGenLargeVein extends FeatureBase {

	final WorldGenerator worldGen;
	final INumberProvider count;
	final INumberProvider minY;
	private INumberProvider veinHeight, veinDiameter;
	private INumberProvider verticalDensity;
	private INumberProvider horizontalDensity;

	public FeatureGenLargeVein(String name, WorldGenerator worldGen, int count, int minY, GenRestriction biomeRes, boolean regen, GenRestriction dimRes, int height, int diameter, int vDensity, int hDensity) {

		this(name, worldGen, new ConstantProvider(count), new ConstantProvider(minY), biomeRes, regen, dimRes, new ConstantProvider(height), new ConstantProvider(diameter), new ConstantProvider(vDensity), new ConstantProvider(hDensity));
	}

	public FeatureGenLargeVein(String name, WorldGenerator worldGen, INumberProvider count, INumberProvider minY, GenRestriction biomeRes, boolean regen, GenRestriction dimRes, INumberProvider height, INumberProvider diameter, INumberProvider vDensity, INumberProvider hDensity) {

		super(name, biomeRes, regen, dimRes);
		this.worldGen = worldGen;
		this.count = count;
		this.minY = minY;
		this.veinHeight = height;
		this.veinDiameter = diameter;
		this.verticalDensity = vDensity;
		this.horizontalDensity = hDensity;
	}

	public int getDensity(Random rand, int oreDistance, float oreDensity) {

		oreDensity = oreDensity * 0.01f * (oreDistance >> 1);
		int i = (int) oreDensity;
		if (i == 0) {
			++i;
		}
		int rnd = oreDistance / i;
		int r = 0;
		for (; i > 0; --i) {
			r += rand.nextInt(rnd);
		}
		return r;
	}

	@Override
	public boolean generateFeature(Random random, int blockX, int blockZ, World world) {

		BlockPos pos = new BlockPos(blockX, 64, blockZ);

		final int count = this.count.intValue(world, random, pos);
		final int blockY = minY.intValue(world, random, pos);
		final int veinDiameter = this.veinDiameter.intValue(world, random, pos);
		final int horizontalDensity = this.horizontalDensity.intValue(world, random, pos);
		final int veinHeight = this.veinHeight.intValue(world, random, pos);
		final int verticalDensity = this.verticalDensity.intValue(world, random, pos);

		Random dRand = new Random(world.getSeed());
		long l = (dRand.nextLong() / 2L) * 2L + 1L;
		long l1 = (dRand.nextLong() / 2L) * 2L + 1L;
		dRand.setSeed((blockX >> 4) * l + (blockZ >> 4) * l1 ^ world.getSeed());

		boolean generated = false;
		for (int i = count; i-- > 0; ) {

			int x = blockX + getDensity(dRand, veinDiameter, horizontalDensity);
			int y = blockY + getDensity(dRand, veinHeight, verticalDensity);
			int z = blockZ + getDensity(dRand, veinDiameter, horizontalDensity);
			if (!canGenerateInBiome(world, x, z, random)) {
				continue;
			}

			generated |= worldGen.generate(world, random, new BlockPos(x, y, z));
		}
		return generated;
	}

}
