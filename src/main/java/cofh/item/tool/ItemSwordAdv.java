package cofh.item.tool;

import cofh.util.ItemHelper;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class ItemSwordAdv extends ItemSword {

	public String repairIngot = "";

	public ItemSwordAdv(Item.ToolMaterial toolMaterial) {

		super(toolMaterial);
	}

	public ItemSwordAdv setRepairIngot(String repairIngot) {

		this.repairIngot = repairIngot;
		return this;
	}

	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

		return ItemHelper.isOreName(stack, repairIngot);
	}

}
