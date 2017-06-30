package cofh.lib.world.feature;

import cofh.lib.world.IFeatureGenerator;
import cofh.lib.world.biome.BiomeInfo;
import cofh.lib.world.biome.BiomeInfoSet;
import gnu.trove.set.hash.THashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Random;
import java.util.Set;

public abstract class FeatureBase implements IFeatureGenerator {

	public enum GenRestriction {
		NONE, BLACKLIST, WHITELIST;

		public static GenRestriction get(String restriction) {

			if (restriction.equalsIgnoreCase("blacklist")) {
				return BLACKLIST;
			}
			if (restriction.equalsIgnoreCase("whitelist")) {
				return WHITELIST;
			}
			return NONE;
		}
	}

	public final String name;

	public final GenRestriction biomeRestriction;
	public final GenRestriction dimensionRestriction;

	public final boolean regen;

	public boolean withVillage = true;

	protected int rarity;

	protected final BiomeInfoSet biomes = new BiomeInfoSet(1);
	protected final Set<Integer> dimensions = new THashSet<>();

	/**
	 * Shortcut to add a Feature with no biome or dimension restriction.
	 */
	public FeatureBase(String name, boolean regen) {

		this(name, GenRestriction.NONE, regen, GenRestriction.NONE);
	}

	/**
	 * Shortcut to add a Feature with a dimension restriction but no biome restriction.
	 */
	public FeatureBase(String name, boolean regen, GenRestriction dimRes) {

		this(name, GenRestriction.NONE, regen, dimRes);
	}

	/**
	 * Shortcut to add a Feature with a biome restriction but no dimension restriction.
	 */
	public FeatureBase(String name, GenRestriction biomeRes, boolean regen) {

		this(name, biomeRes, regen, GenRestriction.NONE);
	}

	public FeatureBase(String name, GenRestriction biomeRes, boolean regen, GenRestriction dimRes) {

		this.name = name;
		this.biomeRestriction = biomeRes;
		this.dimensionRestriction = dimRes;
		this.regen = regen;
	}

	public void setRarity(int rarity) {

		this.rarity = rarity;
	}

	public FeatureBase addBiome(BiomeInfo biome) {

		biomes.add(biome);
		return this;
	}

	public FeatureBase addBiomes(BiomeInfoSet biomes) {

		this.biomes.addAll(biomes);
		return this;
	}

	public FeatureBase addDimension(int dimID) {

		dimensions.add(dimID);
		return this;
	}

	/* IFeatureGenerator */
	@Override
	public final String getFeatureName() {

		return name;
	}

	@Override
	public boolean generateFeature(Random random, int chunkX, int chunkZ, World world, boolean hasVillage, boolean newGen) {

		if (!newGen && !regen) {
			return false;
		}
		if (hasVillage && !withVillage) {
			return false;
		}
		if (dimensionRestriction != GenRestriction.NONE && dimensionRestriction == GenRestriction.BLACKLIST == dimensions.contains(world.provider.getDimension())) {
			return false;
		}
		if (rarity > 1 && random.nextInt(rarity) != 0) {
			return false;
		}
		return generateFeature(random, chunkX * 16 + 8, chunkZ * 16 + 8, world);
	}

	protected abstract boolean generateFeature(Random random, int blockX, int blockZ, World world);

	protected boolean canGenerateInBiome(World world, int x, int z, Random rand) {

		if (biomeRestriction != GenRestriction.NONE) {
			Biome biome = world.getBiome(new BlockPos(x, 0, z));
			return !(biomeRestriction == GenRestriction.BLACKLIST == biomes.contains(biome, rand));
		}
		return true;
	}

}
