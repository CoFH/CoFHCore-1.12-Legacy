package cofh.core.util.filter;

import cofh.api.core.IFilterable;
import cofh.core.util.helpers.ItemHelper;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Arrays;
import java.util.List;

public class ItemFilter implements INBTSerializable<NBTTagCompound> {

	private ItemStack[] items;
	private TIntHashSet oreIDs;
	private boolean[] flags;

	public ItemFilter(int size) {

		flags = new boolean[] { false, false, false, false };
		items = new ItemStack[size];
		Arrays.fill(items, ItemStack.EMPTY);
		oreIDs = new TIntHashSet();
	}

	public void setFlag(int flag, boolean value) {

		flags[flag] = value;
	}

	public boolean getFlag(int flag) {

		return flags[flag];
	}

	public void setSlot(int index, ItemStack stack) {

		items[index] = stack;
		updateOreIDs();
	}

	public ItemStack getSlot(int index) {

		return items[index];
	}

	public ItemStack[] getItems() {

		return items;
	}

	public int getSize() {

		return items.length;
	}

	public void clear() {

		Arrays.fill(items, ItemStack.EMPTY);
		oreIDs.clear();
	}

	public boolean matches(ItemStack stack) {

		boolean ret = flags[IFilterable.FLAG_WHITELIST];

		if (flags[IFilterable.FLAG_ORE_DICT] && !oreIDs.isEmpty()) {
			List<Integer> ids = ItemHelper.oreProxy.getAllOreIDs(stack);
			for (Integer i : ids) {
				if (oreIDs.contains(i)) {
					return ret;
				}
			}
		}
		for (ItemStack item : items) {
			if (item.getItem() != stack.getItem()) {
				continue;
			}
			if (flags[IFilterable.FLAG_METADATA] && item.getItemDamage() != stack.getItemDamage()) {
				continue;
			}
			if (flags[IFilterable.FLAG_NBT] && !ItemHelper.doNBTsMatch(item.getTagCompound(), stack.getTagCompound())) {
				continue;
			}
			return ret;
		}
		return !ret;
	}

	private void updateOreIDs() {

		oreIDs.clear();
		for (ItemStack item : items) {
			if (!item.isEmpty()) {
				oreIDs.addAll(ItemHelper.oreProxy.getAllOreIDs(item));
			}
		}
	}

	/* INBTSerializable */
	@Override
	public NBTTagCompound serializeNBT() {

		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < items.length; i++) {
			if (!items[i].isEmpty()) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("Slot", i);
				items[i].writeToNBT(tag);
				list.appendTag(tag);
			}
		}
		nbt.setTag("Items", list);
		nbt.setByte("Flags", (byte) getFlagByte());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {

		NBTTagList list = nbt.getTagList("Items", 10);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int slot = tag.getInteger("Slot");

			if (slot >= 0 && slot < items.length) {
				items[slot] = new ItemStack(tag);
			}
		}
		setFlagByte((int) nbt.getByte("Flags"));
		updateOreIDs();
	}

	public void setFlagByte(int value) {

		for (int i = 0; i < flags.length; i++) {
			flags[i] = (value & (1 << i)) != 0;
		}
	}

	public int getFlagByte() {

		int v = 0;
		for (int i = 0; i < flags.length; i++) {
			if (flags[i]) {
				v |= (1 << i);
			}
		}
		return v;
	}

}
