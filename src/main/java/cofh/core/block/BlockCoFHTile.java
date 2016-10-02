package cofh.core.block;

import cofh.api.block.IDismantleable;
import cofh.api.tileentity.ISecurable;
import cofh.core.util.CoreUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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
}
