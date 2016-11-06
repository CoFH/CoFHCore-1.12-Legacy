package cofh.core.item.tool;

import cofh.core.util.CoreUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class ItemSickleAdv extends ItemToolAdv {

    public int radius = 3;

    public ItemSickleAdv(Item.ToolMaterial toolMaterial) {

        super(3.0F, -2.4F, toolMaterial);
        addToolClass("sickle");

        effectiveMaterials.add(Material.LEAVES);
        effectiveMaterials.add(Material.PLANTS);
        effectiveMaterials.add(Material.VINE);
        effectiveMaterials.add(Material.WEB);
        effectiveBlocks.add(Blocks.WEB);
        effectiveBlocks.add(Blocks.VINE);
    }

    public ItemSickleAdv setRadius(int radius) {

        this.radius = radius;
        return this;
    }

    @Override
    protected boolean harvestBlock(World world, BlockPos pos, EntityPlayer player) {

        if (world.isAirBlock(pos)) {
            return false;
        }
        EntityPlayerMP playerMP = null;
        if (player instanceof EntityPlayerMP) {
            playerMP = (EntityPlayerMP) player;
        }
        // check if the block can be broken, since extra block breaks shouldn't instantly break stuff like obsidian
        // or precious ores you can't harvest while mining stone
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
//        int meta = world.getBlockMetadata(x, y, z);
        // only effective materials
        if (!(getToolClasses(player.getHeldItemMainhand()).contains(block.getHarvestTool(state)) || canHarvestBlock(state, player.getHeldItemMainhand()))) {
            return false;
        }
        if (!ForgeHooks.canHarvestBlock(block, player, world, pos)) {
            return false;
        }
        // send the blockbreak event
        int xpToDrop = 0;
        if (playerMP != null) {
            xpToDrop = ForgeHooks.onBlockBreakEvent(world, playerMP.interactionManager.getGameType(), playerMP, pos);
            if (xpToDrop == -1) {
                return false;
            }
        }
        if (player.capabilities.isCreativeMode) {
            if (!world.isRemote) {
                block.onBlockHarvested(world, pos, state, player);
            }

            if (block.removedByPlayer(state, world, pos, player, false)) {
                block.onBlockDestroyedByPlayer(world, pos, state);
            }
            // send update to client
            if (!world.isRemote) {
                playerMP.connection.sendPacket(new SPacketBlockChange(world, pos));
            } else {
                Minecraft.getMinecraft().getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, Minecraft.getMinecraft().objectMouseOver.sideHit));
            }
            return true;
        }
        if (!world.isRemote) {
            // serverside we reproduce ItemInWorldManager.tryHarvestBlock
            // ItemInWorldManager.removeBlock
            block.onBlockHarvested(world, pos, state, player);
            if (block.removedByPlayer(state, world, pos, player, true)) {
                block.onBlockDestroyedByPlayer(world, pos, state);
                block.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getHeldItemMainhand());
                if (block.equals(Blocks.VINE)) {
                    CoreUtils.dropItemStackIntoWorldWithVelocity(new ItemStack(Blocks.VINE), world, pos);
                }
                if (xpToDrop > 0) {
                    block.dropXpOnBlockBreak(world, pos, xpToDrop);
                }
            }
            // always send block update to client
            playerMP.connection.sendPacket(new SPacketBlockChange(world, pos));
        } else {
            // PlayerControllerMP pcmp = Minecraft.getMinecraft().playerController;
            // clientside we do a "this block has been clicked on long enough to be broken" call. This should not send any new packets
            // the code above, executed on the server, sends a block-updates that give us the correct state of the block we destroy.
            // following code can be found in PlayerControllerMP.onPlayerDestroyBlock
            if (block.removedByPlayer(state, world, pos, player, true)) {
                block.onBlockDestroyedByPlayer(world, pos, state);
            }
            Minecraft.getMinecraft().getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, Minecraft.getMinecraft().objectMouseOver.sideHit));
        }
        return true;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        World world = player.worldObj;
        IBlockState state = world.getBlockState(pos);

        if (!canHarvestBlock(state, stack)) {
            if (!player.capabilities.isCreativeMode) {
                stack.damageItem(1, player);
            }
            return false;
        }
        boolean used = false;

        world.playEvent(2001, pos, Block.getStateId(state));

        for (int i = pos.getX() - radius; i <= pos.getX() + radius; i++) {
            for (int k = pos.getZ() - radius; k <= pos.getZ() + radius; k++) {
                used |= harvestBlock(world, new BlockPos(i, pos.getY(), k), player);
            }
        }
        if (used && !player.capabilities.isCreativeMode) {
            stack.damageItem(1, player);
        }
        return true;
    }

}
