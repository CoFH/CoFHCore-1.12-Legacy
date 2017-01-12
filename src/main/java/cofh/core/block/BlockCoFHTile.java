package cofh.core.block;

import cofh.api.block.IBlockInfo;
import cofh.api.block.IDismantleable;
import cofh.api.core.IInitializer;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.tileentity.*;
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

public abstract class BlockCoFHTile extends BlockCoFHBase implements ITileEntityProvider, IBlockInfo, IDismantleable, IInitializer {

	public static final ArrayList<ItemStack> NO_DROP = new ArrayList<ItemStack>();

	public BlockCoFHTile(Material material) {

		super(material);

		setSoundType(SoundType.STONE);
	}

	public BlockCoFHTile(Material material, String modName) {

		super(material, modName);

		setSoundType(SoundType.STONE);
	}

	/* ITileEntityProvider */
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {

		return createNewTileEntity(world, state.getBlock().getMetaFromState(state));
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {

		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileCoFHBase) {
			return ((TileCoFHBase) tile).getExtendedState(state, world, pos);
		}
		return state;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {

		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileCoFHBase) {
			((TileCoFHBase) tile).blockBroken();
		}
		if (tile instanceof IInventoryRetainer && ((IInventoryRetainer) tile).retainInventory()) {
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

	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {

	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {

		if (!player.capabilities.isCreativeMode) {
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {

		TileEntity tile = world.getTileEntity(pos);

		if (ServerHelper.isServerWorld(world) && tile instanceof ISecurable) {
			if (SecurityHelper.isSecure(stack)) {
				GameProfile stackOwner = SecurityHelper.getOwner(stack);

				if (((ISecurable) tile).setOwner(stackOwner)) {
					; // cool, set the owner
				} else if (placer instanceof ICommandSender) {
					((ISecurable) tile).setOwnerName(placer.getName());
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
			int quadrant = MathHelper.floor(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

			if (reconfig.allowYAxisFacing()) {
				quadrant = placer.rotationPitch > 60 ? 4 : placer.rotationPitch < -60 ? 5 : quadrant;
			}
			switch (quadrant) {
				// TODO: Change away from ordinals.
				case 0:
					reconfig.setFacing(EnumFacing.NORTH.ordinal());
					break;
				case 1:
					reconfig.setFacing(EnumFacing.EAST.ordinal());
					break;
				case 2:
					reconfig.setFacing(EnumFacing.SOUTH.ordinal());
					break;
				case 3:
					reconfig.setFacing(EnumFacing.WEST.ordinal());
					break;
				case 4:
					reconfig.setFacing(EnumFacing.UP.ordinal());
					break;
				case 5:
					reconfig.setFacing(EnumFacing.DOWN.ordinal());
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
		return ForgeHooks.blockStrength(world.getBlockState(pos), player, world, pos);
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {

		TileEntity tile = world.getTileEntity(pos);
		return tile instanceof TileCoFHBase && tile.hasWorldObj() ? ((TileCoFHBase) tile).getComparatorInputOverride() : 0;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {

		TileEntity tile = world.getTileEntity(pos);
		return tile instanceof TileCoFHBase && tile.hasWorldObj() ? ((TileCoFHBase) tile).getLightValue() : 0;
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
	public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {

		return dropDelegate(getItemStackTag(world, pos), world, pos, fortune);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {

		Item item = Item.getItemFromBlock(this);

		if (item == null) {
			return null;
		}
		int bMeta = state.getBlock().getMetaFromState(state);
		ItemStack retStack = new ItemStack(item, 1, bMeta);
		retStack.setTagCompound(getItemStackTag(world, pos));

		return retStack;
	}

	/* HELPERS */
	public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

		return null;
	}

	public abstract ArrayList<ItemStack> dropDelegate(NBTTagCompound nbt, IBlockAccess world, BlockPos pos, int fortune);

	public abstract ArrayList<ItemStack> dismantleDelegate(NBTTagCompound nbt, World world, BlockPos pos, EntityPlayer player, boolean returnDrops, boolean simulate);

	/* IBlockInfo */
	@Override
	public void getBlockInfo(List<ITextComponent> info, IBlockAccess world, BlockPos pos, EnumFacing side, EntityPlayer player, boolean debug) {

		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof ITileInfo) {
			((ITileInfo) tile).getTileInfo(info, world, pos, side, player, debug);
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
	public ArrayList<ItemStack> dismantleBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player, boolean returnDrops) {

		return dismantleDelegate(getItemStackTag(world, pos), world, pos, player, returnDrops, false);
	}

	@Override
	public boolean canDismantle(World world, BlockPos pos, IBlockState state, EntityPlayer player) {

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

		return false;
	}

	@Override
	public boolean initialize() {

		return false;
	}

	@Override
	public boolean postInit() {

		return false;
	}

}
