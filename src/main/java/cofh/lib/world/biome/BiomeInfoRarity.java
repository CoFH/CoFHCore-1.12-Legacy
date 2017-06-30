package cofh.lib.world.biome;

import net.minecraft.world.biome.Biome;

import java.util.Random;

public class BiomeInfoRarity extends BiomeInfo {

	private final int rarity;

	public BiomeInfoRarity(String name, int r) {

		super(name);
		rarity = r;
	}

	public BiomeInfoRarity(Object d, int t, boolean wl, int r) {

		super(d, t, wl);
		rarity = r;
	}

	@Override
	public boolean isBiomeEqual(Biome biome, Random rand) {

		boolean r = super.isBiomeEqual(biome, rand);
		if (rand != null) {
			return r && (rarity <= 1 || rand.nextInt(rarity) == 0);
		}
		return r;
	}
}
