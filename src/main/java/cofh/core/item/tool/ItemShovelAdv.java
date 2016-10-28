package cofh.core.item.tool;

import com.google.common.collect.Sets;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

public class ItemShovelAdv extends ItemToolAdv {

	protected static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(new Block[] { Blocks.CLAY, Blocks.DIRT, Blocks.FARMLAND, Blocks.GRASS, Blocks.GRAVEL,
			Blocks.MYCELIUM, Blocks.SAND, Blocks.SNOW, Blocks.SNOW_LAYER, Blocks.SOUL_SAND });

	public ItemShovelAdv(ToolMaterial toolMaterial) {

		//TODO attack speed (set to vanilla shovel attack speed currently)
		super(1.0F, -3.0F, toolMaterial);
		addToolClass("shovel");

		effectiveBlocks.addAll(EFFECTIVE_ON);
		effectiveMaterials.add(Material.GROUND);
		effectiveMaterials.add(Material.GRASS);
		effectiveMaterials.add(Material.SAND);
		effectiveMaterials.add(Material.SNOW);
		effectiveMaterials.add(Material.CRAFTED_SNOW);
		effectiveMaterials.add(Material.CLAY);
	}

}
