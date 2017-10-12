package cofh.core.inventory;

import net.minecraft.item.Item;
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
	public int hashCode() {

		return oreID != -1 ? oreID : tag != null ? 17 + tag.hashCode() * 31 + (metadata & 65535) | getId() << 16 : (metadata & 65535) | getId() << 16;
	}

}
