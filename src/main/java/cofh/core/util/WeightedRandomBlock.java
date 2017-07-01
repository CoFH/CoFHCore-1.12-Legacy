package cofh.core.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

import java.util.Collection;

/**
 * This class essentially allows for ores to be generated in clusters, with Features randomly choosing one or more blocks from a weighted list.
 *
 * @author King Lemming
 */
public final class WeightedRandomBlock extends WeightedRandom.Item {

	public final Block block;
	public final int metadata;
	public final IBlockState state;

	public WeightedRandomBlock(ItemStack ore) {

		this(ore, 100);
	}

	public WeightedRandomBlock(ItemStack ore, int weight) {

		this(Block.getBlockFromItem(ore.getItem()), ore.getItemDamage(), weight);
	}

	public WeightedRandomBlock(Block ore) {

		this(ore, 0, 100); // some blocks do not have associated items
	}

	public WeightedRandomBlock(Block ore, int metadata) {

		this(ore, metadata, 100);
	}

	public WeightedRandomBlock(Block ore, int metadata, int weight) {

		super(weight);
		this.block = ore;
		this.metadata = metadata;
		this.state = null;
	}

	public WeightedRandomBlock(IBlockState ore, int weight) {

		super(weight);
		this.block = ore.getBlock();
		this.metadata = block.getMetaFromState(ore);
		this.state = ore;
	}

	public static boolean isBlockContained(Block block, int metadata, Collection<WeightedRandomBlock> list) {

		for (WeightedRandomBlock rb : list) {
			if (block.equals(rb.block) && (metadata == -1 || rb.metadata == -1 || rb.metadata == metadata)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isBlockContained(Block block, int metadata, WeightedRandomBlock[] list) {

		for (WeightedRandomBlock rb : list) {
			if (block.equals(rb.block) && (metadata == -1 || rb.metadata == -1 || rb.metadata == metadata)) {
				return true;
			}
		}
		return false;
	}

	public IBlockState getState() {

		return state == null ? block.getStateFromMeta(metadata) : state;
	}

}
