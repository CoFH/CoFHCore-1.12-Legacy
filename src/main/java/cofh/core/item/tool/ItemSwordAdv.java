package cofh.core.item.tool;

import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import java.util.List;

public class ItemSwordAdv extends ItemSword {

	public String repairIngot = "";
	protected boolean showInCreative = true;

	public ItemSwordAdv(Item.ToolMaterial toolMaterial) {

		super(toolMaterial);
	}

	public ItemSwordAdv setRepairIngot(String repairIngot) {

		this.repairIngot = repairIngot;
		return this;
	}

	public ItemSwordAdv setShowInCreative(boolean showInCreative) {

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
