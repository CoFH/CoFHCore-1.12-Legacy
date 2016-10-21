package cofh.core.item.tool;

import cofh.core.entity.EntityCoFHFishHook;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

public class ItemFishingRodAdv extends ItemFishingRod {

    protected IIcon normalIcons[] = new IIcon[2];

    public String repairIngot = "";
    protected ToolMaterial toolMaterial;
    protected boolean showInCreative = true;
    protected int luckModifier = 0;
    protected int speedModifier = 0;

    public ItemFishingRodAdv(ToolMaterial toolMaterial) {

        this.toolMaterial = toolMaterial;
        this.setMaxDamage(toolMaterial.getMaxUses());
    }

    public ItemFishingRodAdv setRepairIngot(String repairIngot) {

        this.repairIngot = repairIngot;
        return this;
    }

    public ItemFishingRodAdv setShowInCreative(boolean showInCreative) {

        this.showInCreative = showInCreative;
        return this;
    }

    public ItemFishingRodAdv setLuckModifier(int luckMod) {

        luckModifier = luckMod;
        return this;
    }

    public ItemFishingRodAdv setSpeedModifier(int speedMod) {

        speedModifier = speedMod;
        return this;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {

        if (showInCreative) {
            list.add(new ItemStack(item, 1, 0));
        }
    }

    @Override
    public int getItemEnchantability() {

        return toolMaterial.getEnchantability();
    }

    @Override
    public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

        return ItemHelper.isOreNameEqual(stack, repairIngot);
    }

    // TODO: This will need a custom render or something
    @Override
    public boolean isFull3D() {

        return true;
    }

    @Override
    public boolean isItemTool(ItemStack stack) {

        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

        if (player.fishEntity != null) {
            int i = player.fishEntity.func_146034_e();
            stack.damageItem(i, player);
            player.swingItem();
        } else {
            world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

            if (!world.isRemote) {
                world.spawnEntityInWorld(new EntityCoFHFishHook(world, player, luckModifier, speedModifier));
            }
            player.swingItem();
        }
        return stack;
    }

    @Override
    public IIcon getIconIndex(ItemStack stack) {

        return getIcon(stack, 0);
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {

        EntityPlayer player = CoreUtils.getClientPlayer();

        if (player.inventory.getCurrentItem() == stack && player.fishEntity != null) {
            return this.normalIcons[1];
        }
        return this.normalIcons[0];
    }

    @Override
    public void registerIcons(IIconRegister ir) {

        this.normalIcons[0] = ir.registerIcon(this.getIconString() + "_Uncast");
        this.normalIcons[1] = ir.registerIcon(this.getIconString() + "_Cast");
    }

}
