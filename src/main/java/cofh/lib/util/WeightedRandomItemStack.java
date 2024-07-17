package cofh.lib.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

public class WeightedRandomItemStack extends WeightedRandom.Item {

	private final ItemStack stack;

	public WeightedRandomItemStack(ItemStack stack) {

		this(stack, 100);
	}

	public WeightedRandomItemStack(ItemStack stack, int weight) {

		super(weight);
		this.stack = stack;
	}

	public ItemStack getStack() {

		if (stack == null) {
			return null;
		}
		return stack.copy();
	}

}
