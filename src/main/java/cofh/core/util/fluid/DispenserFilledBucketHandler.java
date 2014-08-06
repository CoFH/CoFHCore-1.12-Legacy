package cofh.core.util.fluid;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public final class DispenserFilledBucketHandler extends BehaviorDefaultDispenseItem {

	private final BehaviorDefaultDispenseItem defaultDispenserItemBehavior = new BehaviorDefaultDispenseItem();

	/**
	 * Dispense the specified stack, play the dispense sound and spawn particles.
	 */
	@Override
	public ItemStack dispenseStack(IBlockSource blockSource, ItemStack stackBucket) {

		EnumFacing facing = BlockDispenser.func_149937_b(blockSource.getBlockMetadata());
		World world = blockSource.getWorld();

		int x = blockSource.getXInt() + facing.getFrontOffsetX();
		int y = blockSource.getYInt() + facing.getFrontOffsetY();
		int z = blockSource.getZInt() + facing.getFrontOffsetZ();

		if (!world.isAirBlock(x, y, z) && world.getBlock(x, y, z).getMaterial().isSolid()) {
			return stackBucket;
		}
		if (BucketHandler.emptyBucket(blockSource.getWorld(), x, y, z, stackBucket)) {
			return new ItemStack(Items.bucket);
		}
		return defaultDispenserItemBehavior.dispense(blockSource, stackBucket);
	}

}
