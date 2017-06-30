package cofh.core.item.tool;

import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.NonNullList;

public class ItemSwordCore extends ItemSword {

	protected String repairIngot = "";

	protected boolean showInCreative = true;

	public ItemSwordCore(ToolMaterial toolMaterial) {

		super(toolMaterial);
	}

	public ItemSwordCore setRepairIngot(String repairIngot) {

		this.repairIngot = repairIngot;
		return this;
	}

	public ItemSwordCore setShowInCreative(boolean showInCreative) {

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
