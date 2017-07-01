package cofh.core.inventory;

import cofh.core.util.ComparableItem;
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
		return ItemStack.EMPTY;
	}

	public ComparableItemStack(String oreName) {

		this(getOre(oreName));
	}

	public ComparableItemStack(ItemStack stack) {

		super(stack);
		if (!stack.isEmpty()) {
			stackSize = stack.getCount();
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

		if (!stack.isEmpty()) {
			item = stack.getItem();
			metadata = ItemHelper.getItemDamage(stack);
			stackSize = stack.getCount();
			oreID = ItemHelper.oreProxy.getOreID(stack);
		} else {
			item = Items.AIR;
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
			item = Items.AIR;
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

		return item != Items.AIR;
	}

	public ItemStack toItemStack() {

		return item != Items.AIR ? new ItemStack(item, stackSize, metadata) : ItemStack.EMPTY;
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

		return o instanceof ComparableItemStack && isItemEqual((ComparableItemStack) o);
	}

}
