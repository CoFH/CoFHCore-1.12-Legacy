package cofh.lib.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Extension of {@link ComparableItemStack} except NBT sensitive.
 *
 * It is expected that this will have limited use, so this is a child class for overhead performance reasons.
 *
 * @author King Lemming
 *
 */
public class ComparableItemStackNBT extends ComparableItemStack {

	public NBTTagCompound tag;

	public ComparableItemStackNBT(ItemStack stack) {

		super(stack);

		if (stack != null && stack.stackTagCompound != null) {
			tag = (NBTTagCompound) stack.stackTagCompound.copy();
		}
	}

	@Override
	public boolean isStackEqual(ComparableItemStack other) {

		return super.isStackEqual(other) && isStackTagEqual((ComparableItemStackNBT) other);
	}

	private boolean isStackTagEqual(ComparableItemStackNBT other) {

		return tag == null ? other.tag == null : other.tag == null ? false : tag.equals(other.tag);
	}

	@Override
	public ItemStack toItemStack() {

		ItemStack ret = super.toItemStack();
		if (ret != null) {
			ret.stackTagCompound = (NBTTagCompound) tag.copy();
		}
		return ret;
	}

}
