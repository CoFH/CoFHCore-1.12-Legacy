package cofh.core.fluid;

import cofh.api.tileentity.ISidedTexture;
import cofh.core.block.TileCoFHBase;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;

public class TileFont extends TileCoFHBase implements ISidedTexture {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileFont.class, "cofh.Font");
	}

	Block baseBlock = Blocks.bedrock;
	IIcon icon = Blocks.bedrock.getBlockTextureFromSide(0);

	Fluid fluid;
	BlockFluidBase fluidBlock;
	int amount = -1;
	int productionDelay = 100;
	long timeTracker;

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

	public void update() {

		long worldTime = worldObj.getTotalWorldTime();
		if (timeTracker >= worldTime) {
			return;
		}
		timeTracker = worldTime + productionDelay;

		int x = xCoord, y = yCoord, z = zCoord, meta = worldObj.getBlockMetadata(x, y, z);
		ForgeDirection dir = ForgeDirection.getOrientation(meta ^ 1);

		if (fluidBlock.canDisplace(worldObj, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ)) {
			worldObj.setBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, fluidBlock, 0, 3);
			worldObj.notifyBlockChange(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, getBlockType());

			if (amount > 0) {
				amount--;
			}
			if (amount == 0) {
				// if (worldObj.rand) TODO: depleted font state
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta | 8, 2);
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

	/* ISidedTexture */
	@Override
	public IIcon getTexture(int side, int pass) {

		// FIXME: overlay an icon on side we eject fluids?
		return icon;
	}

}
