package cofh.core.item.tool;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemSpade;

public class ItemShovelMulti extends ItemToolMulti {

	public ItemShovelMulti(String modName) {

		super(modName, 1.0F, -3.0F);
		addToolClass("shovel");

		effectiveBlocks.addAll(ItemSpade.EFFECTIVE_ON);

		effectiveMaterials.add(Material.GROUND);
		effectiveMaterials.add(Material.GRASS);
		effectiveMaterials.add(Material.SAND);
		effectiveMaterials.add(Material.SNOW);
		effectiveMaterials.add(Material.CRAFTED_SNOW);
		effectiveMaterials.add(Material.CLAY);
	}

}
