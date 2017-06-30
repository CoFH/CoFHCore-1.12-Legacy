package cofh.lib.util;

import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Wrapper for an Item/Metadata combination post 1.7. Quick and dirty, allows for Integer-based Hashes without collisions.
 *
 * @author King Lemming
 */
public class ComparableItem {

	public Item item;
	public int metadata;

	public static ComparableItem fromItemStack(ItemStack stack) {

		return new ComparableItem(stack);
	}

	protected ComparableItem() {

		item = null;
		metadata = 0;
	}

	public ComparableItem(Item item, int metadata) {

		this.item = item;
		this.metadata = metadata;
	}

	public ComparableItem(ItemStack stack) {

		if (stack != null) {
			this.item = stack.getItem();
			this.metadata = ItemHelper.getItemDamage(stack);
		} else {
			this.item = null;
			this.metadata = 0;
		}
	}

	public ComparableItem(ComparableItem stack) {

		this.item = stack.item;
		this.metadata = stack.metadata;
	}

	public ComparableItem set(ItemStack stack) {

		if (stack != null) {
			this.item = stack.getItem();
			this.metadata = ItemHelper.getItemDamage(stack);
		} else {
			this.item = null;
			this.metadata = 0;
		}
		return this;
	}

	// '0' is null. '-1' is an unmapped item (missing in this World)
	protected final int getId() {

		return Item.getIdFromItem(item);
	}

	public boolean isEqual(ComparableItem other) {

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

	@Override
	public ComparableItem clone() {

		return new ComparableItem(this);
	}

	@Override
	public boolean equals(Object o) {

		if (!(o instanceof ComparableItem)) {
			return false;
		}
		return isEqual((ComparableItem) o);
	}

	@Override
	public int hashCode() {

		// TODO: this hash conflicts a lot
		return (metadata & 65535) | getId() << 16;
	}

	@Override
	public String toString() {

		String b = getClass().getName() + '@' + System.identityHashCode(this) + '{' + "m:" + metadata + ", i:" + (item == null ? null : item.getClass().getName()) + '@' + System.identityHashCode(item) + ", v:" + getId() + '}';
		return b;
	}

}
