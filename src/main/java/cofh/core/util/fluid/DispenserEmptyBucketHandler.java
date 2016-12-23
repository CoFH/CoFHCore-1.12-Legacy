package cofh.core.util.fluid;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class DispenserEmptyBucketHandler extends BehaviorDefaultDispenseItem {

    private final BehaviorDefaultDispenseItem defaultDispenserItemBehavior = new BehaviorDefaultDispenseItem();

    @Override
    public ItemStack dispenseStack(IBlockSource blockSource, ItemStack stackBucket) {

        EnumFacing facing = blockSource.getBlockState().getValue(BlockDispenser.FACING);
        World world = blockSource.getWorld();

        BlockPos pos = blockSource.getBlockPos().offset(facing);

        ItemStack filledBucket = BucketHandler.fillBucket(world, pos);
        if (filledBucket == null) {
            return defaultDispenserItemBehavior.dispense(blockSource, stackBucket);
        }
        if (--stackBucket.stackSize == 0) {
            stackBucket = filledBucket.copy();
        } else if (((TileEntityDispenser) blockSource.getBlockTileEntity()).addItemStack(filledBucket) < 0) {
            return defaultDispenserItemBehavior.dispense(blockSource, stackBucket);
        }
        return stackBucket;
    }

}
