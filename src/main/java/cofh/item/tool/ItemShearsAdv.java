package cofh.item.tool;

import cofh.util.ItemHelper;

import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;

public class ItemShearsAdv extends ItemShears {

	public String repairIngot = "";
	public Item.ToolMaterial toolMaterial;

	public ItemShearsAdv(Item.ToolMaterial toolMaterial) {

		this.toolMaterial = toolMaterial;
		this.setMaxDamage(toolMaterial.getMaxUses());
	}

	public ItemShearsAdv setRepairIngot(String repairIngot) {

		this.repairIngot = repairIngot;
		return this;
	}

	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

		return ItemHelper.isOreName(stack, repairIngot);
	}

}
