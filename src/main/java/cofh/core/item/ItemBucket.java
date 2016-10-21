package cofh.core.item;

import cofh.core.util.fluid.BucketHandler;
import cofh.lib.render.IFluidOverlayItem;
import cofh.lib.util.RegistryUtils;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBucket extends ItemBase implements IFluidOverlayItem {

    Item container = Items.bucket;

    public ItemBucket() {

        super();
        setMaxStackSize(1);
        setContainerItem(container);
        itemMap.put(-1000, new ItemEntry("OverlayIcon"));
    }

    public ItemBucket(String modName) {

        super(modName);
        setMaxStackSize(1);
        setContainerItem(container);
        itemMap.put(-1000, new ItemEntry("OverlayIcon"));
    }

    public ItemBucket(String modName, Item container) {

        super(modName);
        setMaxStackSize(1);
        this.container = container;
        setContainerItem(container);
        itemMap.put(-1000, new ItemEntry("OverlayIcon"));
    }

    @Override
    public void registerIcons(IIconRegister ir) {

        if (!hasTextures) {
            return;
        }
        {
            ItemEntry item = itemMap.get(-1000);
            item.icon = registerIcon(ir, item);
        }
        for (int i = 0; i < itemList.size(); i++) {
            ItemEntry item = itemMap.get(itemList.get(i));
            item.icon = registerIcon(ir, item);
        }
    }

    @Override
    protected IIcon registerIcon(IIconRegister ir, ItemEntry item) {

        String texture = modName + ":" + getUnlocalizedName().replace("item." + modName + ".", "") + "/" + StringHelper.titleCase(item.name);
        if (RegistryUtils.itemTextureExists(texture)) {
            item.maxDamage = 1;
            return ir.registerIcon(texture);
        }
        item.maxDamage = 2;
        return ir.registerIcon("minecraft:bucket_empty");
    }

    @Override
    public int getRenderPasses(int metadata) {

        return itemMap.get(metadata).maxDamage;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int meta, int pass) {

        if (pass == 1) {
            return itemMap.get(-1000).icon;
        }
        return super.getIconFromDamageForRenderPass(meta, pass);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

        MovingObjectPosition pos = this.getMovingObjectPositionFromPlayer(world, player, false);

        if (pos == null || pos.typeOfHit != MovingObjectType.BLOCK) {
            return stack;
        }
        int x = pos.blockX;
        int y = pos.blockY;
        int z = pos.blockZ;

        switch (pos.sideHit) {
            case 0:
                --y;
                break;
            case 1:
                ++y;
                break;
            case 2:
                --z;
                break;
            case 3:
                ++z;
                break;
            case 4:
                --x;
                break;
            case 5:
                ++x;
                break;
        }
        if (!player.canPlayerEdit(x, y, z, pos.sideHit, stack) || !world.isAirBlock(x, y, z) && world.getBlock(x, y, z).getMaterial().isSolid()) {
            return stack;
        }
        if (BucketHandler.emptyBucket(world, x, y, z, stack)) {
            if (!player.capabilities.isCreativeMode) {
                return new ItemStack(container);
            }
        }
        return stack;
    }

}
