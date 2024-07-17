package cofh.lib.util;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

/**
 * This class essentially allows for ores to be generated in clusters, with Features randomly choosing one or more blocks from a weighted list.
 *
 * @author King Lemming
 *
 */
public final class WeightedRandomBlock extends WeightedRandom.Item {

	public final Block block;
	public final int metadata;

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

}
