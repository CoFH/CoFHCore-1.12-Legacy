package cofh.util.fluid;

import cofh.util.BlockWrapper;
import cofh.util.ItemWrapper;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.IFluidBlock;

public class BucketHandler {

	public static BucketHandler instance = new BucketHandler();

	public static void initialize() {

		registerBucket(Blocks.water, 0, new ItemStack(Items.water_bucket));
		registerBucket(Blocks.flowing_water, 0, new ItemStack(Items.water_bucket));
		registerBucket(Blocks.lava, 0, new ItemStack(Items.lava_bucket));
		registerBucket(Blocks.flowing_lava, 0, new ItemStack(Items.lava_bucket));
	}

	private static TMap<Integer, ItemWrapper> bucketFill = new THashMap<Integer, ItemWrapper>();
	private static TMap<Integer, BlockWrapper> bucketEmpty = new THashMap<Integer, BlockWrapper>();

	private BucketHandler() {

		if (instance != null) {
			throw new IllegalArgumentException();
		}
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onBucketFill(FillBucketEvent event) {

		ItemStack current = event.current;

		if (event.target.typeOfHit != MovingObjectType.BLOCK) {
			return;
		}
		boolean fill = true;
		int x = event.target.blockX, y = event.target.blockY, z = event.target.blockZ, side = event.target.sideHit;

		l: if (!current.getItem().equals(Items.bucket)) {
			if (FluidContainerRegistry.isBucket(current)) {
				ForgeDirection fside = ForgeDirection.getOrientation(side).getOpposite();
				x += fside.offsetX;
				y += fside.offsetY;
				z += fside.offsetZ;
				fill = false;
				break l;
			}
			return;
		}
		if (event.entityPlayer != null) {
			if ((fill && !event.world.canMineBlock(event.entityPlayer, x, y, z)) || !event.entityPlayer.canPlayerEdit(x, y, z, side, current)) {
				event.setCanceled(true);
				return;
			}
		}
		ItemStack bucket = null;

		if (fill) {
			bucket = fillBucket(event.world, x, y, z);
		} else if (emptyBucket(event.world, x, y, z, current)) {
			bucket = new ItemStack(Items.bucket);
		}
		if (bucket == null) {
			return;
		}
		event.result = bucket;
		event.setResult(Result.ALLOW);
	}

	public static boolean registerBucket(Block block, int bMeta, ItemStack bucket) {

		if (block == null || bMeta < 0 || bucket == null || bucketFill.containsKey(BlockWrapper.getHashCode(block, bMeta))
				|| bucketEmpty.containsKey(ItemWrapper.getHashCode(bucket))) {
			return false;
		}
		bucketFill.put(BlockWrapper.getHashCode(block, bMeta), new ItemWrapper(bucket));
		bucketEmpty.put(ItemWrapper.getHashCode(bucket), new BlockWrapper(block, bMeta));
		return true;
	}

	public static ItemStack fillBucket(World world, int x, int y, int z) {

		Block block = world.getBlock(x, y, z);
		int bMeta = world.getBlockMetadata(x, y, z);

		if (!bucketFill.containsKey(BlockWrapper.getHashCode(block, bMeta))) {
			if (block instanceof IFluidBlock) {
				IFluidBlock flBlock = (IFluidBlock) block;

				if (flBlock.canDrain(world, x, y, z)) {
					ItemStack stack = new ItemStack(Items.bucket);
					stack = FluidContainerRegistry.fillFluidContainer(flBlock.drain(world, x, y, z, false), stack);

					if (stack != null) {
						flBlock.drain(world, x, y, z, true);
						return stack;
					}
				}
			}
			return null;
		}
		world.setBlockToAir(x, y, z);
		ItemWrapper result = bucketFill.get(BlockWrapper.getHashCode(block, bMeta));
		return new ItemStack(result.item, 1, result.metadata);
	}

	public static boolean emptyBucket(World world, int x, int y, int z, ItemStack bucket) {

		if (!bucketEmpty.containsKey(ItemWrapper.getHashCode(bucket))) {
			if (bucket.getItem() instanceof ItemBucket) {
				return ((ItemBucket) bucket.getItem()).tryPlaceContainedLiquid(world, x, y, z);
			}
			return false;
		}
		BlockWrapper result = bucketEmpty.get(ItemWrapper.getHashCode(bucket));
		world.setBlock(x, y, z, result.block, result.metadata, 3);
		return true;
	}

}
