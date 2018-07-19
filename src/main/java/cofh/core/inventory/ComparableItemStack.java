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

	public static final OreValidator DEFAULT_VALIDATOR = new OreValidator();

	public static final String BLOCK = "block";
	public static final String ORE = "ore";
	public static final String DUST = "dust";
	public static final String INGOT = "ingot";
	public static final String NUGGET = "nugget";
	public static final String GEM = "gem";
	public static final String PLATE = "plate";

	static {
		DEFAULT_VALIDATOR.addPrefix(BLOCK);
		DEFAULT_VALIDATOR.addPrefix(ORE);
		DEFAULT_VALIDATOR.addPrefix(DUST);
		DEFAULT_VALIDATOR.addPrefix(INGOT);
		DEFAULT_VALIDATOR.addPrefix(NUGGET);
		DEFAULT_VALIDATOR.addPrefix(GEM);
		DEFAULT_VALIDATOR.addPrefix(PLATE);
	}

	public Item item = Items.AIR;
	public int metadata = -1;
	public int stackSize = -1;

	public int oreID = -1;
	public String oreName = "Unknown";

	public ComparableItemStack(String oreName) {

		this(ItemHelper.getOre(oreName));
	}

	public ComparableItemStack(ItemStack stack) {

		this.item = stack.getItem();
		this.metadata = ItemHelper.getItemDamage(stack);

		if (!stack.isEmpty()) {
			stackSize = stack.getCount();
			oreID = ItemHelper.oreProxy.getOreID(stack);
			oreName = ItemHelper.oreProxy.getOreName(oreID);
		}
	}

	public ComparableItemStack(Item item, int metadata, int stackSize) {

		this.item = item;
		this.metadata = metadata;
		this.stackSize = stackSize;
		this.oreID = ItemHelper.oreProxy.getOreID(this.toItemStack());
		this.oreName = ItemHelper.oreProxy.getOreName(oreID);
	}

	public ComparableItemStack(ComparableItemStack stack) {

		this.item = stack.item;
		this.metadata = stack.metadata;
		this.stackSize = stack.stackSize;
		this.oreID = stack.oreID;
		this.oreName = stack.oreName;
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

		return oreID != -1 ? oreName.hashCode() : (metadata & 65535) | getId() << 16;
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder(768);

		builder.append(getClass().getName()).append('@');
		builder.append(System.identityHashCode(this)).append('{');
		builder.append("ID:").append(getId()).append(", ");
		builder.append("DMG:").append(metadata).append(", ");
		builder.append("ORE:").append(oreID).append('|').append(oreName).append(", ");
		builder.append("ITM:");
		if (item == null) {
			builder.append("null");
		} else {
			builder.append(item.getClass().getName()).append('@');
			builder.append(System.identityHashCode(item)).append(' ');
			builder.append('[').append(item.getRegistryName()).append(']');
		}
		builder.append('}');

		return builder.toString();
	}

}
