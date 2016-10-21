package cofh.core.item.tool;

import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemHoeAdv extends ItemHoe {

    public String repairIngot = "";
    protected boolean showInCreative = true;

    public ItemHoeAdv(Item.ToolMaterial toolMaterial) {

        super(toolMaterial);
    }

    public ItemHoeAdv setRepairIngot(String repairIngot) {

        this.repairIngot = repairIngot;
        return this;
    }

    public ItemHoeAdv setShowInCreative(boolean showInCreative) {

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
