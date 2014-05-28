package cofh.fluid;

import cofh.api.tileentity.ISidedBlockTexture;
import cofh.block.TileCoFHBase;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;

public class TileFont extends TileCoFHBase implements ISidedBlockTexture {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileFont.class, "cofh.Font");
	}

	Block baseBlock = Blocks.bedrock;
	IIcon icon = Blocks.bedrock.getBlockTextureFromSide(0);

	Fluid fluid;
	BlockFluidBase fluidBlock;
	int amount = -1;

	public TileFont() {

	}

	public boolean setFluid(Fluid fluid) {

		if (fluid.getBlock() != null) {
			this.fluid = fluid;
			this.fluidBlock = (BlockFluidBase) fluid.getBlock();
			return true;
		}
		return false;
	}

	public boolean setAmount(int amount) {

		if (amount > 0) {
			this.amount = amount;
			return true;
		}
		return false;
	}

	public boolean setBaseBlock(Block block) {

		if (block != null) {
			this.baseBlock = block;
			return true;
		}
		return false;
	}

	public boolean setIcon(IIcon icon) {

		if (icon != null) {
			this.icon = icon;
			return true;
		}
		return false;
	}

	@Override
	public String getName() {

		return "tile.cofh.font.name";
	}

	@Override
	public int getType() {

		return 0;
	}

	@Override
	public void onNeighborBlockChange() {

		if (fluidBlock.canDisplace(worldObj, xCoord, yCoord + 1, zCoord)) {
			worldObj.setBlock(xCoord, yCoord, zCoord, fluidBlock, 0, 2);

			if (amount > 0) {
				amount--;
			}
			if (amount == 0) {
				worldObj.setBlock(xCoord, yCoord, zCoord, baseBlock, 0, 2);
				invalidate();
			}
		}
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

	}

	/* ISidedBlockTexture */
	@Override
	public IIcon getBlockTexture(int side, int pass) {

		return icon;
	}

}
