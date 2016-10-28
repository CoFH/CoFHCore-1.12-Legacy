package cofh.core.item.tool;

import com.google.common.collect.Sets;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

public class ItemPickaxeAdv extends ItemToolAdv {

	protected static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(new Block[] { Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE,
			Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.DOUBLE_STONE_SLAB, Blocks.GOLDEN_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE,
			Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.LIT_REDSTONE_ORE, Blocks.MOSSY_COBBLESTONE,
			Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.STONE_SLAB });

	public ItemPickaxeAdv(ToolMaterial toolMaterial) {

		//TODO attack speed (set to vanilla pickaxe speed currently)
		super(2.0F, -2.8F, toolMaterial);
		addToolClass("pickaxe");

		effectiveBlocks.addAll(EFFECTIVE_ON);
		effectiveMaterials.add(Material.IRON);
		effectiveMaterials.add(Material.ANVIL);
		effectiveMaterials.add(Material.ROCK);
		effectiveMaterials.add(Material.ICE);
		effectiveMaterials.add(Material.PACKED_ICE);
		effectiveMaterials.add(Material.GLASS);
		effectiveMaterials.add(Material.REDSTONE_LIGHT);
	}

}
