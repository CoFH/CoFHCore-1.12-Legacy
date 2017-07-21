package cofh.core.util;

import cofh.core.util.helpers.ItemHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Wrapper for an Item/Metadata combination post 1.7. Quick and dirty, allows for Integer-based Hashes without collisions.
 *
 * @author King Lemming
 */
public final class ItemWrapper {

	public static ItemWrapper fromItemStack(ItemStack stack) {

		return new ItemWrapper(stack);
	}

	public Item item;
	public int metadata;

	public ItemWrapper(Item item, int metadata) {

		this.item = item;
		this.metadata = metadata;
	}

	public ItemWrapper(ItemStack stack) {

		this(stack.getItem(), ItemHelper.getItemDamage(stack));
	}

	public ItemWrapper(ItemWrapper wrapper) {

		this(wrapper.item, wrapper.metadata);
	}

	public ItemWrapper set(ItemStack stack) {

		if (stack != null) {
			this.item = stack.getItem();
			this.metadata = ItemHelper.getItemDamage(stack);
		} else {
			this.item = null;
			this.metadata = 0;
		}
		return this;
	}

	public boolean isEqual(ItemWrapper other) {

		if (other == null) {
			return false;
		}
		if (metadata == other.metadata) {
			if (item == other.item) {
				return true;
			}
			if (item != null && other.item != null) {
				return item.delegate.get() == other.item.delegate.get();
			}
		}
		return false;
	}

	public int getId() {

		return Item.getIdFromItem(item); // '0' is null. '-1' is an unmapped item (missing in this World)
	}

	@Override
	public ItemWrapper clone() {

		return new ItemWrapper(this);
	}

	@Override
	public boolean equals(Object o) {

		return o instanceof ItemWrapper && isEqual((ItemWrapper) o);
	}

	@Override
	public int hashCode() {

		return (metadata & 65535) | getId() << 16;
	}

	@Override
	public String toString() {

		return getClass().getName() + '@' + System.identityHashCode(this) + '{' + "m:" + metadata + ", i:" + (item == null ? null : item.getClass().getName()) + '@' + System.identityHashCode(item) + ", v:" + getId() + '}';
	}

}
