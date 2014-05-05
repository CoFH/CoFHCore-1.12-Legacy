package cofh.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import cofh.util.ItemHelper;

public class ItemHoeAdv extends ItemHoe {

	public String repairIngot = "";

	public ItemHoeAdv(Item.ToolMaterial toolMaterial) {

		super(toolMaterial);
	}

	public ItemHoeAdv setRepairIngot(String repairIngot) {

		this.repairIngot = repairIngot;
		return this;
	}

	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

		return ItemHelper.isOreName(stack, repairIngot);
	}

}
