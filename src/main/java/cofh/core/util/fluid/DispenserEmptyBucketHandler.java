package cofh.core.util.fluid;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public final class DispenserEmptyBucketHandler extends BehaviorDefaultDispenseItem {

	private final BehaviorDefaultDispenseItem defaultDispenserItemBehavior = new BehaviorDefaultDispenseItem();

	@Override
	public ItemStack dispenseStack(IBlockSource blockSource, ItemStack stackBucket) {

		EnumFacing facing = BlockDispenser.func_149937_b(blockSource.getBlockMetadata());
		World world = blockSource.getWorld();

		int x = blockSource.getXInt() + facing.getFrontOffsetX();
		int y = blockSource.getYInt() + facing.getFrontOffsetY();
		int z = blockSource.getZInt() + facing.getFrontOffsetZ();

		ItemStack filledBucket = BucketHandler.fillBucket(world, x, y, z);
		if (filledBucket == null) {
			return defaultDispenserItemBehavior.dispense(blockSource, stackBucket);
		}
		if (--stackBucket.stackSize == 0) {
			stackBucket = filledBucket.copy();
		} else if (((TileEntityDispenser) blockSource.getBlockTileEntity()).func_146019_a(filledBucket) < 0) {
			return defaultDispenserItemBehavior.dispense(blockSource, stackBucket);
		}
		return stackBucket;
	}

}
