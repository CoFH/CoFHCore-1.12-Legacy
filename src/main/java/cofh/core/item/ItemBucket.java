package cofh.core.item;

import cofh.core.util.fluid.BucketHandler;
import cofh.lib.render.IFluidOverlayItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemBucket extends ItemBase implements IFluidOverlayItem {

    Item container = Items.BUCKET;

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

//    @Override
//    public void registerIcons(IIconRegister ir) {
//
//        if (!hasTextures) {
//            return;
//        }
//        {
//            ItemEntry item = itemMap.get(-1000);
//            item.icon = registerIcon(ir, item);
//        }
//        for (int i = 0; i < itemList.size(); i++) {
//            ItemEntry item = itemMap.get(itemList.get(i));
//            item.icon = registerIcon(ir, item);
//        }
//    }
//
//    @Override
//    protected IIcon registerIcon(IIconRegister ir, ItemEntry item) {
//
//        String texture = modName + ":" + getUnlocalizedName().replace("item." + modName + ".", "") + "/" + StringHelper.titleCase(item.name);
//        if (RegistryUtils.itemTextureExists(texture)) {
//            item.maxDamage = 1;
//            return ir.registerIcon(texture);
//        }
//        item.maxDamage = 2;
//        return ir.registerIcon("minecraft:bucket_empty");
//    }

//    @Override
//    public int getRenderPasses(int metadata) {
//
//        return itemMap.get(metadata).maxDamage;
//    }

//    @Override
//    @SideOnly(Side.CLIENT)
//    public IIcon getIconFromDamageForRenderPass(int meta, int pass) {
//
//        if (pass == 1) {
//            return itemMap.get(-1000).icon;
//        }
//        return super.getIconFromDamageForRenderPass(meta, pass);
//    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        RayTraceResult traceResult = this.rayTrace(world, player, false);

        if (traceResult.typeOfHit != RayTraceResult.Type.BLOCK) {
            return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
        }

        BlockPos offset = traceResult.getBlockPos().offset(traceResult.sideHit);

        if (!player.canPlayerEdit(offset, traceResult.sideHit, stack) || !world.isAirBlock(offset) && world.getBlockState(offset).getMaterial().isSolid()) {
            return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
        }
        if (BucketHandler.emptyBucket(world, offset, stack)) {
            if (!player.capabilities.isCreativeMode) {
                return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, new ItemStack(container));
            }
        }
        return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
    }

}
