package cofh.core.item.tool;

import cofh.lib.util.helpers.ItemHelper;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;

public class ItemShearsAdv extends ItemShears {

	public String repairIngot = "";
	protected Item.ToolMaterial toolMaterial;
	protected boolean showInCreative = true;

	public ItemShearsAdv(Item.ToolMaterial toolMaterial) {

		this.toolMaterial = toolMaterial;
		this.setMaxDamage(toolMaterial.getMaxUses());
	}

	public ItemShearsAdv setRepairIngot(String repairIngot) {

		this.repairIngot = repairIngot;
		return this;
	}

	public ItemShearsAdv setShowInCreative(boolean showInCreative) {

		this.showInCreative = showInCreative;
		return this;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {

		if (showInCreative) {
			list.add(new ItemStack(item, 1, 0));
		}
	}

	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

		return ItemHelper.isOreNameEqual(stack, repairIngot);
	}

}
