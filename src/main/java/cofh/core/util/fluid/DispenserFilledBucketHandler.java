package cofh.core.util.fluid;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class DispenserFilledBucketHandler extends BehaviorDefaultDispenseItem {

	private final BehaviorDefaultDispenseItem defaultDispenserItemBehavior = new BehaviorDefaultDispenseItem();

	/**
	 * Dispense the specified stack, play the dispense sound and spawn particles.
	 */
	@Override
	public ItemStack dispenseStack(IBlockSource blockSource, ItemStack stackBucket) {

		EnumFacing facing = blockSource.getBlockState().getValue(BlockDispenser.FACING);
		World world = blockSource.getWorld();

		BlockPos pos = blockSource.getBlockPos().offset(facing);

		if (!world.isAirBlock(pos) && world.getBlockState(pos).getMaterial().isSolid()) {
			return stackBucket;
		}
		if (BucketHandler.emptyBucket(blockSource.getWorld(), pos, stackBucket)) {
			return new ItemStack(Items.BUCKET);
		}
		return defaultDispenserItemBehavior.dispense(blockSource, stackBucket);
	}

}
