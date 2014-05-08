package cofh.fluid;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.Fluid;
import cofh.util.BlockWrapper;

public class BlockFluidInteractive extends BlockFluidCoFHBase {

	protected final TMap<Integer, BlockWrapper> collisionMap = new THashMap<Integer, BlockWrapper>();

	public BlockFluidInteractive(Fluid fluid, Material material, String name) {

		super(fluid, material, name);
	}

	public boolean addInteraction(Block preBlock, Block postBlock) {

		if (preBlock == null || postBlock == null) {
			return false;
		}
		return addInteraction(preBlock, -1, postBlock, 0);
	}

	public boolean addInteraction(Block preBlock, int preMeta, Block postBlock, int postMeta) {

		if (preBlock == null || postBlock == null || postMeta < 0) {
			return false;
		}
		if (preMeta < 0) {
			collisionMap.put(BlockWrapper.getHashCode(preBlock, preMeta), new BlockWrapper(postBlock, postMeta));
		} else {
			collisionMap.put(BlockWrapper.getHashCode(preBlock, preMeta), new BlockWrapper(postBlock, postMeta));
		}
		return true;
	}

	public boolean hasInteraction(Block preBlock, int preMeta) {

		return collisionMap.containsKey(BlockWrapper.getHashCode(preBlock, preMeta)) || collisionMap.containsKey(BlockWrapper.getHashCode(preBlock, -1));
	}

	public BlockWrapper getInteraction(Block preBlock, int preMeta) {

		if (collisionMap.containsKey(BlockWrapper.getHashCode(preBlock, preMeta))) {
			return collisionMap.get(BlockWrapper.getHashCode(preBlock, preMeta));
		}
		return collisionMap.get(BlockWrapper.getHashCode(preBlock, -1));
	}

}
