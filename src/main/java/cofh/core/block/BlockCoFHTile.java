package cofh.core.block;

import cofh.api.block.IDismantleable;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.api.tileentity.IRedstoneControl;
import cofh.api.tileentity.ISecurable;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.RedstoneControlHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.ServerHelper;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;

public abstract class BlockCoFHTile extends BlockCoFHBase implements IDismantleable {
	public BlockCoFHTile(Material material, String modName) {
		super(material, modName);
	}

	public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

		return null;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	public abstract TileEntity createTileEntity(World world, IBlockState state);

	public abstract ArrayList<ItemStack> dropDelegate(NBTTagCompound nbt, IBlockAccess world, BlockPos pos, int fortune);

	public abstract ArrayList<ItemStack> dismantleDelegate(NBTTagCompound nbt, World world, BlockPos pos, EntityPlayer player, boolean returnDrops, boolean simulate);

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
				case 0:
					reconfig.setFacing(EnumFacing.NORTH);
					break;
				case 1:
					reconfig.setFacing(EnumFacing.EAST);
					break;
				case 2:
					reconfig.setFacing(EnumFacing.SOUTH);
					break;
				case 3:
					reconfig.setFacing(EnumFacing.WEST);
					break;
				case 4:
					reconfig.setFacing(EnumFacing.UP);
					break;
				case 5:
					reconfig.setFacing(EnumFacing.DOWN);
					break;
			}
		}
		if (tile instanceof TileCoFHBase) {
			((TileCoFHBase) tile).onNeighborBlockChange();
			((TileCoFHBase) tile).blockPlaced();
		}
	}
}
