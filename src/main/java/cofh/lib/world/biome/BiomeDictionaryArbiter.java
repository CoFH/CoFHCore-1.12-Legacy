package cofh.lib.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

import java.util.HashMap;
import java.util.Set;

public class BiomeDictionaryArbiter {

	private static HashMap<Biome, Set<Type>> types = new HashMap<>();
	private static HashMap<Type, Set<Biome>> biomes = new HashMap<>();
	private static boolean loaded = Loader.instance().isInState(LoaderState.AVAILABLE);

	public static Set<Type> getTypesForBiome(Biome biome) {

		if (loaded) {
			return types.computeIfAbsent(biome, BiomeDictionary::getTypes);
		}
		loaded = Loader.instance().isInState(LoaderState.AVAILABLE);
		return BiomeDictionary.getTypes(biome);
	}

	public static Set<Biome> getBiomesForType(Type type) {

		if (loaded) {
			return biomes.computeIfAbsent(type, BiomeDictionary::getBiomes);
		}
		loaded = Loader.instance().isInState(LoaderState.AVAILABLE);
		return BiomeDictionary.getBiomes(type);
	}

	private BiomeDictionaryArbiter() {

		throw new IllegalArgumentException();
	}
}
