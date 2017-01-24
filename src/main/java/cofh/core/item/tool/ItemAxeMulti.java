package cofh.core.item.tool;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemAxe;

public class ItemAxeMulti extends ItemToolMulti {

	public ItemAxeMulti(String modName) {

		super(modName, 3.0F, -3.2F);
		addToolClass("axe");

		effectiveBlocks.addAll(ItemAxe.EFFECTIVE_ON);

		effectiveMaterials.add(Material.WOOD);
		effectiveMaterials.add(Material.PLANTS);
		effectiveMaterials.add(Material.VINE);
		effectiveMaterials.add(Material.CACTUS);
		effectiveMaterials.add(Material.GOURD);
	}

}
