package cofh.lib.world.biome;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;

import java.util.HashMap;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BiomeDictionaryArbiter {

	private static HashMap<BiomeGenBase, Type[]> types = new HashMap<BiomeGenBase, Type[]>();
	private static HashMap<Type, BiomeGenBase[]> biomes = new HashMap<Type, BiomeGenBase[]>();
	private static boolean loaded = Loader.instance().isInState(LoaderState.AVAILABLE);

	public static Type[] getTypesForBiome(BiomeGenBase biome) {

		if (loaded) {
			Type[] r = types.get(biome);
			if (r == null) {
				types.put(biome, r = BiomeDictionary.getTypesForBiome(biome));
			}
			return r;
		}
		loaded = Loader.instance().isInState(LoaderState.AVAILABLE);
		return BiomeDictionary.getTypesForBiome(biome);
	}

	public static BiomeGenBase[] getTypesForBiome(Type type) {

		if (loaded) {
			BiomeGenBase[] r = biomes.get(type);
			if (r == null) {
				biomes.put(type, r = BiomeDictionary.getBiomesForType(type));
			}
			return r;
		}
		loaded = Loader.instance().isInState(LoaderState.AVAILABLE);
		return BiomeDictionary.getBiomesForType(type);
	}

	private BiomeDictionaryArbiter() {

		throw new IllegalArgumentException();
	}
}
