package cofh.core.item.tool;

import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemShearsCore extends ItemShears {

	protected String repairIngot = "";
	protected ToolMaterial toolMaterial;

	protected boolean showInCreative = true;

	public ItemShearsCore(ToolMaterial toolMaterial) {

		super();
		this.toolMaterial = toolMaterial;
		this.setMaxDamage(toolMaterial.getMaxUses() - 12);
	}

	public ItemShearsCore setRepairIngot(String repairIngot) {

		this.repairIngot = repairIngot;
		return this;
	}

	public ItemShearsCore setShowInCreative(boolean showInCreative) {

		this.showInCreative = showInCreative;
		return this;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (isInCreativeTab(tab) && showInCreative) {
			items.add(new ItemStack(this, 1, 0));
		}
	}

	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

		return ItemHelper.isOreNameEqual(stack, repairIngot);
	}

}
