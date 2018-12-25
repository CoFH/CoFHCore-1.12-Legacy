package cofh.core.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Extension of {@link ComparableItemStack} except NBT sensitive.
 *
 * It is expected that this will have limited use, so this is a child class for overhead performance reasons.
 *
 * @author King Lemming
 */
public class ComparableItemStackNBT extends ComparableItemStack {

	public NBTTagCompound tag;

	public ComparableItemStackNBT(ItemStack stack) {

		super(stack);

		if (!stack.isEmpty() && stack.getTagCompound() != null) {
			tag = stack.getTagCompound().copy();
		}
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

	@Override
	public boolean isStackEqual(ComparableItemStack other) {

		return super.isStackEqual(other) && isStackTagEqual((ComparableItemStackNBT) other);
	}

	private boolean isStackTagEqual(ComparableItemStackNBT other) {

		return tag == null ? other.tag == null : other.tag != null && tag.equals(other.tag);
	}

	@Override
	public ItemStack toItemStack() {

		ItemStack ret = super.toItemStack();

		if (!ret.isEmpty() && tag != null) {
			ret.setTagCompound(tag.copy());
		}
		return ret;
	}

	@Override
	public boolean equals(Object o) {

		return o instanceof ComparableItemStackNBT && isItemEqual((ComparableItemStack) o) && isStackTagEqual((ComparableItemStackNBT) o);
	}

	@Override
	public int hashCode() {

		return oreID != -1 ? oreName.hashCode() : tag != null ? 17 + tag.toString().hashCode() * 31 + (metadata & 65535) + (getId() << 16) : (metadata & 65535) | getId() << 16;
	}

}
