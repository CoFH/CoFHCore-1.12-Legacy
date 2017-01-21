package cofh.core.item.tool;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemAxe;

public class ItemAxeBase extends ItemToolBase {

	public ItemAxeBase(String modName) {

		super(modName);
		addToolClass("axe");

		effectiveBlocks.addAll(ItemAxe.EFFECTIVE_ON);

		effectiveMaterials.add(Material.WOOD);
		effectiveMaterials.add(Material.PLANTS);
		effectiveMaterials.add(Material.VINE);
		effectiveMaterials.add(Material.CACTUS);
		effectiveMaterials.add(Material.GOURD);
	}

}
