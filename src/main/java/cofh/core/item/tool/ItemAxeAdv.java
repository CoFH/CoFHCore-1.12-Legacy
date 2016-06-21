package cofh.core.item.tool;

import com.google.common.collect.Sets;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public class ItemAxeAdv extends ItemToolAdv {

	protected static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(new Block[] { Blocks.planks, Blocks.bookshelf, Blocks.log, Blocks.log2, Blocks.chest,
			Blocks.pumpkin, Blocks.lit_pumpkin, Blocks.melon_block, Blocks.ladder });

	public ItemAxeAdv(Item.ToolMaterial toolMaterial) {

		super(3.0F, toolMaterial);
		addToolClass("axe");

		effectiveBlocks.addAll(EFFECTIVE_ON);
		effectiveMaterials.add(Material.wood);
		effectiveMaterials.add(Material.plants);
		effectiveMaterials.add(Material.vine);
		effectiveMaterials.add(Material.cactus);
		effectiveMaterials.add(Material.gourd);
	}

}
