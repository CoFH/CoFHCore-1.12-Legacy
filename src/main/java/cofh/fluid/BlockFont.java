package cofh.fluid;

import cofh.block.BlockCoFHBase;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class BlockFont extends BlockCoFHBase {

	Block baseBlock;
	Fluid fluid;
	int amountScale = 500; // Fluid blocks contained within per metadata - 0 is unlimited

	public BlockFont(Material material, Block baseBlock, Fluid fluid) {

		super(material);

		this.baseBlock = baseBlock;

		if (fluid.getBlock() == null) {
			this.fluid = FluidRegistry.WATER;
		} else {
			this.fluid = fluid;
		}
	}

	public BlockFont setAmountScale(int amountScale) {

		this.amountScale = amountScale > 0 ? amountScale : this.amountScale;
		return this;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		return new TileFont(fluid, metadata * amountScale);
	}

	/* IDismantleable */
	@Override
	public ItemStack dismantleBlock(EntityPlayer player, NBTTagCompound nbt, World world, int x, int y, int z, boolean returnBlock, boolean simulate) {

		return null;
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
