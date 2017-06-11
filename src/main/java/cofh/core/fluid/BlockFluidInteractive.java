package cofh.core.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fluids.Fluid;

import java.util.HashMap;

public class BlockFluidInteractive extends BlockFluidCore {

	private final HashMap<IBlockState, IBlockState> collisionMap = new HashMap<>();
	private final HashMap<Block, IBlockState> anyState = new HashMap<>();

	public BlockFluidInteractive(Fluid fluid, Material material, String name) {

		super(fluid, material, name);
	}

	public BlockFluidInteractive(Fluid fluid, Material material, String modName, String name) {

		super(fluid, material, modName, name);
	}

	public boolean addInteraction(Block preBlock, Block postBlock) {

		if (preBlock == null || postBlock == null) {
			return false;
		}
		return addInteraction(preBlock.getDefaultState(), postBlock.getDefaultState(), true);
	}

	public boolean addInteraction(IBlockState pre, IBlockState post) {

		return addInteraction(pre, post, false);
	}

	public boolean addInteraction(IBlockState pre, IBlockState post, boolean anyState) {

		if (pre == null || post == null) {
			return false;
		}
		if (anyState) {
			this.anyState.put(pre.getBlock(), post);
		} else {
			collisionMap.put(pre, post);
		}
		return true;
	}

	public boolean addInteraction(IBlockState pre, Block postBlock) {

		return addInteraction(pre, postBlock.getDefaultState(), false);
	}

	public boolean hasInteraction(IBlockState state) {

		return collisionMap.containsKey(state) || anyState.containsKey(state.getBlock());
	}

	public IBlockState getInteraction(IBlockState state) {

		if (collisionMap.containsKey(state)) {
			return collisionMap.get(state);
		}
		return anyState.get(state.getBlock());
	}

}
