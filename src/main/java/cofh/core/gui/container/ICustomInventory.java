package cofh.core.gui.container;

import net.minecraft.item.ItemStack;

/**
 * Interface to allow a Container to interact with a secondary inventory.
 *
 * @author King Lemming
 */
public interface ICustomInventory {

	/**
	 * @param inventoryIndex Internal index of the inventory (there may be multiple). Assume 0 as default.
	 * @return An array of ItemStacks stored in the Inventory.
	 */
	ItemStack[] getInventorySlots(int inventoryIndex);

	int getSlotStackLimit(int slotIndex);

	void onSlotUpdate(int slotIndex);

}
