package cofh.lib.inventory;

import cofh.lib.util.ComparableItem;
import cofh.lib.util.helpers.ItemHelper;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * This class allows for OreDictionary-compatible ItemStack comparisons and Integer-based Hashes without collisions.
 *
 * The intended purpose of this is for things such as Recipe Handlers or HashMaps of ItemStacks.
 *
 * @author King Lemming
 *
 */
public class ComparableItemStack extends ComparableItem {

	public static ComparableItemStack fromItemStack(ItemStack stack) {

		return new ComparableItemStack(stack);
	}

	public int stackSize = -1;
	public int oreID = -1;

	protected static ItemStack getOre(String oreName) {

		if (ItemHelper.oreNameExists(oreName)) {
			return ItemHelper.oreProxy.getOre(oreName);
		}
		return null;
	}

	public ComparableItemStack(String oreName) {

		this(getOre(oreName));
	}

	public ComparableItemStack(ItemStack stack) {

		super(stack);
		if (stack != null) {
			stackSize = stack.stackSize;
			oreID = ItemHelper.oreProxy.getOreID(stack);
		}
	}

	public ComparableItemStack(Item item, int damage, int stackSize) {

		super(item, damage);
		this.stackSize = stackSize;
		this.oreID = ItemHelper.oreProxy.getOreID(this.toItemStack());
	}

	public ComparableItemStack(ComparableItemStack stack) {

		super(stack.item, stack.metadata);
		this.stackSize = stack.stackSize;
		this.oreID = stack.oreID;
	}

	@Override
	public ComparableItemStack set(ItemStack stack) {

		if (stack != null) {
			item = stack.getItem();
			metadata = ItemHelper.getItemDamage(stack);
			stackSize = stack.stackSize;
			oreID = ItemHelper.oreProxy.getOreID(stack);
		} else {
			item = null;
			metadata = -1;
			stackSize = -1;
			oreID = -1;
		}
		return this;
	}

	public ComparableItemStack set(ComparableItemStack stack) {

		if (stack != null) {
			item = stack.item;
			metadata = stack.metadata;
			stackSize = stack.stackSize;
			oreID = stack.oreID;
		} else {
			item = null;
			metadata = -1;
			stackSize = -1;
			oreID = -1;
		}
		return this;
	}

	public boolean isItemEqual(ComparableItemStack other) {

		return other != null && (oreID != -1 && oreID == other.oreID || isEqual(other));
	}

	public boolean isStackEqual(ComparableItemStack other) {

		return isItemEqual(other) && stackSize == other.stackSize;
	}

	public boolean isStackValid() {

		return item != null;
	}

	public ItemStack toItemStack() {

		return item != null ? new ItemStack(item, stackSize, metadata) : null;
	}

	@Override
	public ComparableItemStack clone() {

		return new ComparableItemStack(this);
	}

	@Override
	public int hashCode() {

		return oreID != -1 ? oreID : super.hashCode();
	}

	@Override
	public boolean equals(Object o) {

		if (!(o instanceof ComparableItemStack)) {
			return false;
		}
		return isItemEqual((ComparableItemStack) o);
	}

}
