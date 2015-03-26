package cofh.core.fluid;

import cofh.core.block.BlockCoFHBase;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public class BlockFont extends BlockCoFHBase {

	public BlockFont(Material material, Block baseBlock, Fluid fluid) {

		super(material);
		setTickRandomly(true);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		if (metadata >= 8) {
			return null;
		}
		return new TileFont();
	}

	/* IDismantleable */
	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, NBTTagCompound nbt, World world, int x, int y, int z, boolean returnDrops, boolean simulate) {

		return null;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileFont) {
			((TileFont) te).update();
		}
	}

	@Override
	public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z) {

		return false;
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		return true;
	}

	@Override
	public boolean postInit() {

		return true;
	}

}
