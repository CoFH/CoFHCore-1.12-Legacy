package cofh.util.fluid;

import cofh.util.BlockWrapper;
import cofh.util.ItemWrapper;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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

	}

	// private static BlockWrapper queryBlock = new BlockWrapper(Blocks.stone, 0);
	// private static ItemWrapper queryItem = new ItemWrapper(Items.diamond, 0);

	private static BiMap<BlockWrapper, ItemWrapper> buckets = HashBiMap.create();

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
				ForgeDirection fside = ForgeDirection.getOrientation(side);
				Block block = event.world.getBlock(x, y, z);
				x += fside.offsetX;
				y += fside.offsetY;
				z += fside.offsetZ;
				if (!block.isReplaceable(event.world, x, y, z) && block.getMaterial().isSolid()) {
					x -= fside.offsetX;
					y -= fside.offsetY;
					z -= fside.offsetZ;
				}
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

		if (block == null || bMeta < 0 || bucket == null || buckets.containsKey(new BlockWrapper(block, bMeta))) {
			return false;
		}
		buckets.put(new BlockWrapper(block, bMeta), new ItemWrapper(bucket));
		return true;
	}

	public static ItemStack fillBucket(World world, int x, int y, int z) {

		Block block = world.getBlock(x, y, z);
		int bMeta = world.getBlockMetadata(x, y, z);

		if (!buckets.containsKey(new BlockWrapper(block, bMeta))) {
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
		if (!world.setBlockToAir(x, y, z)) {
			return null;
		}
		ItemWrapper result = buckets.get(new BlockWrapper(block, bMeta));
		return new ItemStack(result.item, 1, result.metadata);
	}

	public static boolean emptyBucket(World world, int x, int y, int z, ItemStack bucket) {

		boolean r = false;
		if (!buckets.inverse().containsKey(new ItemWrapper(bucket))) {
			if (bucket.getItem() instanceof ItemBucket) {
				r = ((ItemBucket) bucket.getItem()).tryPlaceContainedLiquid(world, x, y, z);
				world.markBlockForUpdate(x, y, z);
			}
			return r;
		}
		BlockWrapper result = buckets.inverse().get(new ItemWrapper(bucket));

		Material material = world.getBlock(x, y, z).getMaterial();
		boolean solid = !material.isSolid();
		if (world.isAirBlock(x, y, z) || solid) {
			r = world.setBlock(x, y, z, result.block, result.metadata, 3); // this can fail

			if (r && !world.isRemote && solid && !material.isLiquid()) {
				world.func_147480_a(x, y, z, true);
			}

			world.markBlockForUpdate(x, y, z);
		}
		return r;
	}

}
