package cofh.core.item.tool;

import com.google.common.collect.Sets;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public class ItemAxeAdv extends ItemToolAdv {

	protected static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(new Block[] { Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG, Blocks.LOG2, Blocks.CHEST,
			Blocks.PUMPKIN, Blocks.LIT_PUMPKIN, Blocks.MELON_BLOCK, Blocks.LADDER });

	public ItemAxeAdv(Item.ToolMaterial toolMaterial) {

		//TODO attack speed (currently set to wooden axe speed)
		super(3.0F, -3.2F, toolMaterial);
		addToolClass("axe");

		effectiveBlocks.addAll(EFFECTIVE_ON);
		effectiveMaterials.add(Material.WOOD);
		effectiveMaterials.add(Material.PLANTS);
		effectiveMaterials.add(Material.VINE);
		effectiveMaterials.add(Material.CACTUS);
		effectiveMaterials.add(Material.GOURD);
	}

}
