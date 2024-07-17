package cofh.lib.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Wrapper for an Item/Metadata combination post 1.7. Quick and dirty, allows for Integer-based Hashes without collisions.
 *
 * @author King Lemming
 *
 */
public final class ItemWrapper extends ComparableItem {

	public static ItemWrapper fromItemStack(ItemStack stack) {

		return new ItemWrapper(stack);
	}

	public ItemWrapper(Item item, int metadata) {

		super(item, metadata);
	}

	public ItemWrapper(ItemStack stack) {

		super(stack);
	}

	public ItemWrapper(ItemWrapper stack) {

		super(stack);
	}

	@Override
	public ItemWrapper clone() {

		return new ItemWrapper(this);
	}

	@Override
	public boolean equals(Object o) {

		if (!(o instanceof ItemWrapper)) {
			return false;
		}
		return isEqual((ItemWrapper) o);
	}

}
