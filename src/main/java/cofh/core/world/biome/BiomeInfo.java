package cofh.core.world.biome;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.TempCategory;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

import java.util.Collection;
import java.util.Random;

public class BiomeInfo {

	private final Object data;
	private final boolean whitelist;
	private final int type;
	private final int hash;

	public BiomeInfo(String name) {

		data = name;
		hash = name.hashCode();
		whitelist = true;
		type = 0;
	}

	public BiomeInfo(Object d, int t, boolean wl) {

		data = d;
		hash = 0;
		whitelist = wl;
		type = t;
	}

	@SuppressWarnings ("unchecked")
	public boolean isBiomeEqual(Biome biome, Random rand) {

		boolean r = false;
		if (biome != null) {
			switch (type) {
				default:
					break;
				case 0:
					String name = biome.getBiomeName();
					r = name.hashCode() == hash && name.equals(data);
					break;
				case 1:
					r = biome.getTempCategory() == data;
					break;
				case 2:
					r = BiomeDictionary.hasType(biome, (Type) data);
					break;
				case 4:
					r = ((Collection<String>) data).contains(biome.getBiomeName());
					break;
				case 5:
					r = ((Collection<TempCategory>) data).contains(biome.getTempCategory());
					break;
				case 6:
					Type[] d = (Type[]) data;
					int c = 0, e = d.length;
					for (int i = 0; i < e; ++i) {
						if (BiomeDictionary.hasType(biome, d[i])) {
							++c;
						}
					}
					r = c == e;
					break;
				case 7:
					ResourceLocation registry = Biome.REGISTRY.getNameForObject(biome);
					r = registry.hashCode() == hash && registry.equals(data);
					break;
				case 8:
					r = ((Collection<ResourceLocation>) data).contains(Biome.REGISTRY.getNameForObject(biome));
					break;
			}
		}
		return r == whitelist;
	}

}
