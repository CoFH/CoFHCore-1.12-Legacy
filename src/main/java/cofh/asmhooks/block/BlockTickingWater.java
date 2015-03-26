package cofh.asmhooks.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlockTickingWater extends BlockDynamicLiquid {

	public BlockTickingWater(Material mat) {

		super(mat);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {

		super.onBlockAdded(world, x, y, z);

		if (this.blockMaterial != Material.water) {
			return;
		}

		if (world.provider.isHellWorld) {
			world.setBlockToAir(x, y, z);
			world.playAuxSFX(1004, x, y, z, 0);
			world.playAuxSFX(2000, x, y, z, 4);
		}
	}

	@Override
	public boolean isAssociatedBlock(Block block) {

		return super.isAssociatedBlock(block) || block == Blocks.water;
	}

}
