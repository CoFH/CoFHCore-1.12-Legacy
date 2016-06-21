package cofh.core.item.tool;

import com.google.common.collect.Sets;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

public class ItemShovelAdv extends ItemToolAdv {

	protected static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(new Block[] { Blocks.clay, Blocks.dirt, Blocks.farmland, Blocks.grass, Blocks.gravel,
			Blocks.mycelium, Blocks.sand, Blocks.snow, Blocks.snow_layer, Blocks.soul_sand });

	public ItemShovelAdv(ToolMaterial toolMaterial) {

		super(1.0F, toolMaterial);
		addToolClass("shovel");

		effectiveBlocks.addAll(EFFECTIVE_ON);
		effectiveMaterials.add(Material.ground);
		effectiveMaterials.add(Material.grass);
		effectiveMaterials.add(Material.sand);
		effectiveMaterials.add(Material.snow);
		effectiveMaterials.add(Material.craftedSnow);
		effectiveMaterials.add(Material.clay);
	}

}
