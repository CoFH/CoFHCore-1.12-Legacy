package cofh.pcc.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraftforge.common.util.ForgeDirection;

public class InventoryManager {

	public static IInventoryManager create(IInventory inventory, ForgeDirection targetSide) {

		if (inventory instanceof ISidedInventory) {
			return new InventoryManagerSided((ISidedInventory) inventory, targetSide);
		} else if (inventory != null) {
			return new InventoryManagerStandard(inventory, targetSide);
		} else {
			return null;
		}
	}
}
