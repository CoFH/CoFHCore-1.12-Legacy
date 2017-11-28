package cofh.core.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

// TODO: Investigate use of this for Caches.
public class ItemStorageCore {

	protected ItemStack item;
	protected int amount;
	protected int capacity;
	protected boolean locked;

	public ItemStorageCore readFromNBT(NBTTagCompound nbt) {

		return this;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		return nbt;
	}

	public ItemStorageCore setLock(ItemStack stack) {

		return this;
	}

	public void setLocked(boolean lock) {

		if (lock) {
			setLocked();
		} else {
			clearLocked();
		}
	}

	public void setLocked() {

		if (locked || this.item.isEmpty()) {
			return;
		}
		locked = true;
	}

	public void clearLocked() {

		locked = false;
		if (this.amount <= 0) {
			this.item = ItemStack.EMPTY;
		}
	}

	public void setItem(ItemStack stack) {

		if (stack.isEmpty()) {
			this.item = ItemStack.EMPTY;
			this.amount = 0;
		} else {
			this.item = stack;
			this.item.setCount(1);
			this.amount = stack.getCount();
		}
	}

	public void setCapacity(int capacity) {

		this.capacity = capacity;
	}

	public boolean isLocked() {

		return locked;
	}

	public int getSpace() {

		if (item.isEmpty()) {
			return capacity;
		}
		return amount >= capacity ? 0 : capacity - amount;
	}

}
