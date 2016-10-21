package cofh.asmhooks.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockTickingWater extends BlockDynamicLiquid {

	public BlockTickingWater(Material mat) {

		super(mat);
	}

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		super.onBlockAdded(world, pos, state);

		if (this.blockMaterial != Material.WATER) {
			return;
		}

		if (world.provider.doesWaterVaporize()) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
			world.playEvent(1004, pos, 0);
			world.playEvent(2000, pos, 4);
		}
	}

	@Override
	public boolean isAssociatedBlock(Block block) {

		return super.isAssociatedBlock(block) || block == Blocks.WATER;
	}

}
