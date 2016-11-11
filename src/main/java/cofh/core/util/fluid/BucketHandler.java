package cofh.core.util.fluid;

import cofh.lib.util.BlockWrapper;
import cofh.lib.util.ItemWrapper;
import cofh.lib.util.helpers.ServerHelper;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map.Entry;

public class BucketHandler {

    public static BucketHandler instance = new BucketHandler();

    public static void initialize() {

    }

    private static BiMap<BlockWrapper, ItemWrapper> buckets = HashBiMap.create();

    private BucketHandler() {

        if (instance != null) {
            throw new IllegalArgumentException();
        }
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPreBucketFill(FillBucketEvent event) {
        // perform global permissions checks
        onBucketFill(event, true);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPostBucketFill(FillBucketEvent event) {
        // handle thusfar unhandled buckets
        onBucketFill(event, false);
    }

    private void onBucketFill(FillBucketEvent event, boolean pre) {
        if (ServerHelper.isClientWorld(event.getWorld()) || event.getResult() != Result.DEFAULT) {
            return;
        }
        ItemStack current = event.getEmptyBucket();
        RayTraceResult target = event.getTarget();
        if (target == null || target.typeOfHit != Type.BLOCK) {
            return;
        }
        boolean fill = true;
        BlockPos offsetPos = target.getBlockPos();

        if (!Items.BUCKET.equals(current.getItem())) {
            if (!FluidContainerRegistry.isBucket(current)) {
                return;
            }
            IBlockState state = event.getWorld().getBlockState(offsetPos);
            if (!state.getBlock().isReplaceable(event.getWorld(), offsetPos) && state.getMaterial().isSolid()) {
                offsetPos = offsetPos.offset(target.sideHit.getOpposite());
            }
            fill = false;
        }

        if (pre) { // doing all of this in one pass will pre-empt other handlers. Split to two priorities.
            if (event.getEntityPlayer() != null) {
                if (!event.getWorld().isBlockModifiable(event.getEntityPlayer(), offsetPos) || (fill && !event.getEntityPlayer().canPlayerEdit(offsetPos, target.sideHit, current))) {
                    event.setCanceled(true);
                }
            }
            return;
        }
        ItemStack bucket = null;

        if (fill) {
            bucket = fillBucket(event.getWorld(), offsetPos);
        } else if (emptyBucket(event.getWorld(), offsetPos, current)) {
            bucket = new ItemStack(Items.BUCKET);
        }
        if (bucket == null) {
            return;
        }
        event.setFilledBucket(bucket);
        event.setResult(Result.ALLOW);
    }

    public static boolean registerBucket(Block block, int bMeta, ItemStack bucket) {

        if (block == null || bMeta < 0 || bucket == null || buckets.containsKey(new BlockWrapper(block, bMeta))) {
            return false;
        }
        buckets.put(new BlockWrapper(block, bMeta), new ItemWrapper(bucket));
        return true;
    }

    public static ItemStack fillBucket(World world, BlockPos pos) {

        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        int bMeta = state.getBlock().getMetaFromState(state);

        if (!buckets.containsKey(new BlockWrapper(block, bMeta))) {
            if (block.equals(Blocks.WATER) || block.equals(Blocks.FLOWING_WATER)) {
                if (bMeta == 0) {
                    world.setBlockToAir(pos);
                    return new ItemStack(Items.WATER_BUCKET);
                }
                return null;
            } else if (block.equals(Blocks.LAVA) || block.equals(Blocks.FLOWING_LAVA)) {
                if (bMeta == 0) {
                    world.setBlockToAir(pos);
                    return new ItemStack(Items.LAVA_BUCKET);
                }
                return null;
            }
            if (block instanceof IFluidBlock) {
                IFluidBlock flBlock = (IFluidBlock) block;

                if (flBlock.canDrain(world, pos)) {
                    ItemStack stack = new ItemStack(Items.BUCKET);
                    stack = FluidContainerRegistry.fillFluidContainer(flBlock.drain(world, pos, false), stack);

                    if (stack != null) {
                        flBlock.drain(world, pos, true);
                        return stack;
                    }
                }
            }
            return null;
        }
        if (!world.setBlockToAir(pos)) {
            return null;
        }
        ItemWrapper result = buckets.get(new BlockWrapper(block, bMeta));
        return new ItemStack(result.item, 1, result.metadata);
    }

    public static boolean emptyBucket(World world, BlockPos pos, ItemStack bucket) {

        boolean r = false;
        IBlockState beforeState = world.getBlockState(pos);
        if (!buckets.inverse().containsKey(new ItemWrapper(bucket))) {
            if (bucket.getItem() instanceof ItemBucket) {
                r = ((ItemBucket) bucket.getItem()).tryPlaceContainedLiquid(null, world, pos);
                world.notifyBlockUpdate(pos, beforeState, world.getBlockState(pos), 3);
            }
            return r;
        }
        BlockWrapper result = buckets.inverse().get(new ItemWrapper(bucket));

        Material material = beforeState.getMaterial();
        boolean solid = !material.isSolid();
        if (world.isAirBlock(pos) || solid) {
            if (!world.isRemote && solid && !material.isLiquid()) {
                world.destroyBlock(pos, true);
            }
            r = world.setBlockState(pos, result.block.getStateFromMeta(result.metadata), 3); // this can fail
            world.notifyBlockUpdate(pos, beforeState, world.getBlockState(pos), 3);
        }
        return r;
    }

    public static void refreshMap() {

        BiMap<BlockWrapper, ItemWrapper> tempMap = HashBiMap.create(buckets.size());

        for (Entry<BlockWrapper, ItemWrapper> entry : buckets.entrySet()) {
            BlockWrapper tempBlock = new BlockWrapper(entry.getKey().block, entry.getKey().metadata);
            ItemWrapper tempItem = new ItemWrapper(entry.getValue().item, entry.getValue().metadata);
            tempMap.put(tempBlock, tempItem);
        }
        buckets.clear();
        buckets = tempMap;
    }

}
