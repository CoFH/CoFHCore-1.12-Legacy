package cofh.core.fluid;

import codechicken.lib.texture.TextureUtils;
import cofh.api.tileentity.ISidedTexture;
import cofh.core.block.TileCoFHBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;

public class TileFont extends TileCoFHBase implements ISidedTexture {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileFont.class, "cofh.Font");
	}

	Block baseBlock = Blocks.BEDROCK;
	TextureAtlasSprite icon = TextureUtils.getBlockTexture("bedrock");

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

	public boolean setIcon(TextureAtlasSprite icon) {

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

		//int x = xCoord, y = yCoord, z = zCoord, meta = worldObj.getBlockMetadata(x, y, z);
        IBlockState state = worldObj.getBlockState(pos);
        int meta = state.getBlock().getMetaFromState(state);
		EnumFacing dir = EnumFacing.VALUES[meta ^ 1];
        BlockPos offsetPos = pos.offset(dir);

		if (fluidBlock.canDisplace(worldObj, offsetPos)) {
			worldObj.setBlockState(offsetPos, fluidBlock.getDefaultState(), 3);
			worldObj.updateComparatorOutputLevel(offsetPos, getBlockType());

			if (amount > 0) {
				amount--;
			}
			if (amount == 0) {
				// if (worldObj.rand) TODO: depleted font state
                IBlockState state2 = worldObj.getBlockState(pos);
				worldObj.setBlockState(pos, state2.getBlock().getStateFromMeta(meta | 8), 2);
			}
		}
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		return super.writeToNBT(nbt);
	}

	/* ISidedTexture */
	@Override
	public TextureAtlasSprite getTexture(int side, int pass) {

		// FIXME: overlay an icon on side we eject fluids?
		return icon;
	}

}
