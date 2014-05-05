package cofh.util.fluid;

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
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class BucketHandler {

	public static BucketHandler instance = new BucketHandler();

	public static void initialize() {

	}

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
		int x = event.target.blockX, y = event.target.blockY, z = event.target.blockZ;
		int side = event.target.sideHit;
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

	public static ItemStack fillBucket(World world, int x, int y, int z) {

		Block block = world.getBlock(x, y, z);
		if (block.equals(Blocks.water) || block.equals(Blocks.flowing_water)) {
			if (world.getBlockMetadata(x, y, z) == 0) {
				world.setBlockToAir(x, y, z);
				return new ItemStack(Items.water_bucket);
			}
			return null;
		} else if (block.equals(Blocks.lava) || block.equals(Blocks.flowing_lava)) {
			if (world.getBlockMetadata(x, y, z) == 0) {
				world.setBlockToAir(x, y, z);
				return new ItemStack(Items.lava_bucket);
			}
			return null;
		}
		if (block instanceof IFluidBlock) {
			IFluidBlock b = (IFluidBlock) block;
			if (b.canDrain(world, x, y, z)) {
				ItemStack stack = new ItemStack(Items.bucket);
				stack = FluidContainerRegistry.fillFluidContainer(b.drain(world, x, y, z, false), stack);
				if (stack != null) {
					b.drain(world, x, y, z, true);
					return stack;
				}
			}
		}
		return null;
	}

	public static boolean emptyBucket(World world, int x, int y, int z, ItemStack bucket) {

		return ((ItemBucket) bucket.getItem()).tryPlaceContainedLiquid(world, x, y, z);
	}

}
