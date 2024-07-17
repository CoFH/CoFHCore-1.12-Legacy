package cofh.lib.util;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.WeightedRandom;

public class WeightedRandomNBTTag extends WeightedRandom.Item {

	public final NBTBase tag;

	public WeightedRandomNBTTag(int weight, NBTBase tag) {

		super(weight);
		this.tag = tag;
	}

}
