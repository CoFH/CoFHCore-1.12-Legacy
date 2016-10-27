package cofh.core.block;

import cofh.api.block.IBlockDebug;
import cofh.api.block.IBlockInfo;
import cofh.api.block.IDismantleable;
import cofh.api.core.IInitializer;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.inventory.IInventoryRetainer;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.IRedstoneControl;
import cofh.api.tileentity.ISecurable;
import cofh.api.tileentity.ITileInfo;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.*;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class BlockCoFHBase extends Block implements ITileEntityProvider, IBlockDebug, IBlockInfo, IDismantleable, IInitializer {

    public static int renderPass = 0;
    public static final ArrayList<ItemStack> NO_DROP = new ArrayList<ItemStack>();

    public BlockCoFHBase(Material material) {

        super(material);
        setSoundType(SoundType.STONE);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {

        return createNewTileEntity(world, state.getBlock().getMetaFromState(state));
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileCoFHBase) {
            TileCoFHBase theTile = (TileCoFHBase) tile;
            theTile.blockBroken();
        }
        if (tile instanceof IInventoryRetainer) {
            // do nothing
        } else if (tile instanceof IInventory) {
            IInventory inv = (IInventory) tile;
            for (int i = 0; i < inv.getSizeInventory(); i++) {
                CoreUtils.dropItemStackIntoWorldWithVelocity(inv.getStackInSlot(i), world, pos);
            }
        }
        if (tile != null) {
            world.removeTileEntity(pos);
        }
    }

    //@Override
    //public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
    //	if (world.getBlock(x, y, z) != this) { // BUGFIX: mojang randomly calls this method for blocks not in the world!
    //		return getStatelessBoundingBox(world, x, y, z); // see: net.minecraft.item.ItemBlock.func_150936_a (1.7.10 srg)
    //	}
    //
    //	return getBoundingBox(world, x, y, z);
    //}

    //protected AxisAlignedBB getBoundingBox(World world, int x, int y, int z) {
    //	//this.setBlockBoundsBasedOnState(world, x, y, z); // BUGFIX: neither vanilla nor forge call this correctly
    //	return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    //}

    //protected AxisAlignedBB getStatelessBoundingBox(World world, int x, int y, int z) {
    //
    //	return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    //}

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {

    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (!player.capabilities.isCreativeMode) {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 7);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);

        if (ServerHelper.isServerWorld(world) && tile instanceof ISecurable) {
            if (SecurityHelper.isSecure(stack)) {
                GameProfile stackOwner = SecurityHelper.getOwner(stack);

                if (((ISecurable) tile).setOwner(stackOwner)) {
                    ; // cool, set the owner
                } else if (living instanceof ICommandSender) {
                    ((ISecurable) tile).setOwnerName(living.getName());
                }
                ((ISecurable) tile).setAccess(SecurityHelper.getAccess(stack));
            }
        }
        if (tile instanceof IRedstoneControl) {
            if (RedstoneControlHelper.hasRSControl(stack)) {
                ((IRedstoneControl) tile).setControl(RedstoneControlHelper.getControl(stack));
            }
        }
        if (tile instanceof IReconfigurableFacing) {
            IReconfigurableFacing reconfig = (IReconfigurableFacing) tile;
            int quadrant = MathHelper.floor(living.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

            if (reconfig.allowYAxisFacing()) {
                quadrant = living.rotationPitch > 60 ? 4 : living.rotationPitch < -60 ? 5 : quadrant;
            }
            switch (quadrant) {
                case 0:
                    reconfig.setFacing(2);
                    break;
                case 1:
                    reconfig.setFacing(5);
                    break;
                case 2:
                    reconfig.setFacing(3);
                    break;
                case 3:
                    reconfig.setFacing(4);
                    break;
                case 4:
                    reconfig.setFacing(1);
                    break;
                case 5:
                    reconfig.setFacing(0);
                    break;
            }
        }
        if (tile instanceof TileCoFHBase) {
            ((TileCoFHBase) tile).onNeighborBlockChange();
            ((TileCoFHBase) tile).blockPlaced();
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileCoFHBase) {
            ((TileCoFHBase) tile).onNeighborBlockChange();
        }
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileCoFHBase) {
            ((TileCoFHBase) tile).onNeighborTileChange(neighbor);
        }
    }

    @Override
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof ISecurable && !((ISecurable) tile).canPlayerAccess(player)) {
            return -1;
        }
        return ForgeHooks.blockStrength(state, player, world, pos);
    }

    @Override
    public int damageDropped(IBlockState state) {

        return getMetaFromState(state);
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileCoFHBase ? ((TileCoFHBase) tile).getComparatorInput() : 0;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileCoFHBase && tile.hasWorldObj()) {
            return ((TileCoFHBase) tile).getLightValue();
        }
        return 0;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, SpawnPlacementType type) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {

        return false;
    }

    @Override
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
        TileEntity tile = world.getTileEntity(pos);
        return tile != null ? tile.receiveClientEvent(id, param) : false;
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        TileEntity tile = world.getTileEntity(pos);

        return tile instanceof IReconfigurableFacing ? ((IReconfigurableFacing) tile).rotateBlock() : false;
    }

    //@Override
    //@SideOnly(Side.CLIENT)
    //public void registerBlockIcons(IIconRegister ir) {
    //}

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return dismantleBlock(null, getItemStackTag(world, pos), world, pos, false, true);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        Item item = Item.getItemFromBlock(this);

        if (item == null) {
            return null;
        }
        int bMeta = state.getBlock().getMetaFromState(state);
        ItemStack pickBlock = new ItemStack(item, 1, bMeta);
        pickBlock.setTagCompound(getItemStackTag(world, pos));

        return pickBlock;
    }

    public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

        return null;
    }

    //TODO, Fix this simulate bullshit.
    public abstract ArrayList<ItemStack> dismantleBlock(EntityPlayer player, NBTTagCompound nbt, IBlockAccess world, BlockPos pos, boolean returnDrops, boolean simulate);

    /* IBlockDebug */
    @Override
    public void debugBlock(IBlockAccess world, BlockPos pos, EnumFacing side, EntityPlayer player) {

    }

    /* IBlockInfo */
    @Override
    public void getBlockInfo(IBlockAccess world, BlockPos pos, EnumFacing side, EntityPlayer player, List<ITextComponent> info, boolean debug) {

        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof ITileInfo) {
            ((ITileInfo) tile).getTileInfo(info, side, player, debug);
        } else {
            if (tile instanceof IEnergyReceiver) {
                IEnergyReceiver eReceiver = (IEnergyReceiver) tile;
                if (eReceiver.getMaxEnergyStored(side) <= 0) {
                    return;
                }
                info.add(new TextComponentString(StringHelper.localize("info.cofh.energy") + ": " + eReceiver.getEnergyStored(side) + "/" + eReceiver.getMaxEnergyStored(side) + " RF."));
            }
        }
    }

    /* IDismantleable */
    @Override
    public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, BlockPos pos, boolean returnDrops) {

        return dismantleBlock(player, getItemStackTag(world, pos), world, pos, returnDrops, false);
    }

    @Override
    public boolean canDismantle(EntityPlayer player, World world, BlockPos pos) {

        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof ISecurable) {
            return ((ISecurable) tile).canPlayerAccess(player);
        } else if (tile instanceof TileCoFHBase) {
            return ((TileCoFHBase) tile).canPlayerDismantle(player);
        }
        return true;
    }

    /* IInitializer */
    @Override
    public boolean preInit() {

        return true;
    }

}
