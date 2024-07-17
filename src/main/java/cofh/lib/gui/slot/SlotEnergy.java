package cofh.lib.gui.slot;

import cofh.lib.util.helpers.EnergyHelper;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Slot which only accepts Energy (Redstone Flux) Containers as valid.
 *
 * @author King Lemming
 *
 */
public class SlotEnergy extends Slot {

	public SlotEnergy(IInventory inventory, int index, int x, int y) {

		super(inventory, index, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return EnergyHelper.isEnergyContainerItem(stack);
	}

}
