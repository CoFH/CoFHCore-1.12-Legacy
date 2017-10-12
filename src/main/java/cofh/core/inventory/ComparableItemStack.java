package cofh.core.inventory;

import cofh.core.util.helpers.ItemHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * This class allows for OreDictionary-compatible ItemStack comparisons and Integer-based Hashes without collisions.
 *
 * The intended purpose of this is for things such as Recipe Handlers or HashMaps of ItemStacks.
 *
 * @author King Lemming
 */
public class ComparableItemStack {

	public static ComparableItemStack fromItemStack(ItemStack stack) {

		return new ComparableItemStack(stack);
	}

	public Item item = Items.AIR;
	public int metadata = -1;
	public int stackSize = -1;
	public int oreID = -1;

	protected static ItemStack getOre(String oreName) {

		if (ItemHelper.oreNameExists(oreName)) {
			return ItemHelper.oreProxy.getOre(oreName);
		}
		return ItemStack.EMPTY;
	}

	public ComparableItemStack(String oreName) {

		this(getOre(oreName));
	}

	public ComparableItemStack(ItemStack stack) {

		this.item = stack.getItem();
		this.metadata = ItemHelper.getItemDamage(stack);

		if (!stack.isEmpty()) {
			stackSize = stack.getCount();
			oreID = ItemHelper.oreProxy.getOreID(stack);
		}
	}

	public ComparableItemStack(Item item, int metadata, int stackSize) {

		this.item = item;
		this.metadata = metadata;
		this.stackSize = stackSize;
		this.oreID = ItemHelper.oreProxy.getOreID(this.toItemStack());
	}

	public ComparableItemStack(ComparableItemStack stack) {

		this.item = stack.item;
		this.metadata = stack.metadata;
		this.stackSize = stack.stackSize;
		this.oreID = stack.oreID;
	}

	public boolean isEqual(ComparableItemStack other) {

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

	public boolean isItemEqual(ComparableItemStack other) {

		return other != null && (oreID != -1 && oreID == other.oreID || isEqual(other));
	}

	public boolean isStackEqual(ComparableItemStack other) {

		return isItemEqual(other) && stackSize == other.stackSize;
	}

	public int getId() {

		return Item.getIdFromItem(item); // '0' is null. '-1' is an unmapped item (missing in this World)
	}

	public ItemStack toItemStack() {

		return item != Items.AIR ? new ItemStack(item, stackSize, metadata) : ItemStack.EMPTY;
	}

	@Override
	public ComparableItemStack clone() {

		return new ComparableItemStack(this);
	}

	@Override
	public boolean equals(Object o) {

		return o instanceof ComparableItemStack && isItemEqual((ComparableItemStack) o);
	}

	@Override
	public int hashCode() {

		return oreID != -1 ? oreID : (metadata & 65535) | getId() << 16;
	}

	@Override
	public String toString() {

		return getClass().getName() + '@' + System.identityHashCode(this) + '{' + "m:" + metadata + ", i:" + (item == null ? null : item.getClass().getName()) + '@' + System.identityHashCode(item) + ", v:" + getId() + '}';
	}

}
