package cofh.lib.world.feature;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class FeatureGenLargeVein extends FeatureBase {

	final WorldGenerator worldGen;
	final int count;
	final int minY;
	private int veinHeight, veinDiameter;
	private int verticalDensity;
	private int horizontalDensity;

	public FeatureGenLargeVein(String name, WorldGenerator worldGen, int count, int minY, GenRestriction biomeRes, boolean regen, GenRestriction dimRes,
			int height, int diameter, int vDensity, int hDensity) {

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

		oreDensity = (oreDensity * 0.01f * (oreDistance >> 1)) + 1f;
		int i = (int) oreDensity;
		int rnd = oreDistance / i;
		int r = 0;
		for (; i > 0; --i) {
			r += rand.nextInt(rnd);
		}
		return r;
	}

	@Override
	public boolean generateFeature(Random random, int chunkX, int chunkZ, World world) {

		int blockX = chunkX * 16;
		int blockY = minY;
		int blockZ = chunkZ * 16;

		Random dRand = new Random(world.getSeed());
		long l = (dRand.nextLong() / 2L) * 2L + 1L;
		long l1 = (dRand.nextLong() / 2L) * 2L + 1L;
		dRand.setSeed(chunkX * l + chunkZ * l1 ^ world.getSeed());

		boolean generated = false;
		for (int i = count; i-- > 0;) {
			int x = blockX + getDensity(dRand, veinDiameter, horizontalDensity);
			int y = blockY + getDensity(dRand, veinHeight, verticalDensity);
			int z = blockZ + getDensity(dRand, veinDiameter, horizontalDensity);
			if (!canGenerateInBiome(world, x, z, random)) {
				continue;
			}

			generated |= worldGen.generate(world, random, x, y, z);
		}
		return generated;
	}

}
