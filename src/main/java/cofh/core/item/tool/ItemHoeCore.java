package cofh.core.item.tool;

import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class ItemHoeCore extends ItemHoe {

	public String repairIngot = "";

	protected boolean showInCreative = true;

	public ItemHoeCore(ToolMaterial toolMaterial) {

		super(toolMaterial);
	}

	public ItemHoeCore setRepairIngot(String repairIngot) {

		this.repairIngot = repairIngot;
		return this;
	}

	public ItemHoeCore setShowInCreative(boolean showInCreative) {

		this.showInCreative = showInCreative;
		return this;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void getSubItems(@Nonnull Item item, CreativeTabs tab, NonNullList<ItemStack> list) {

		if (showInCreative) {
			list.add(new ItemStack(item, 1, 0));
		}
	}

	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

		return ItemHelper.isOreNameEqual(stack, repairIngot);
	}

}
