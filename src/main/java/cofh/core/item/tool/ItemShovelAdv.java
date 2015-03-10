package cofh.core.item.tool;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemSpade;

public class ItemShovelAdv extends ItemToolAdv {

	public ItemShovelAdv(ToolMaterial toolMaterial) {

		super(1.0F, toolMaterial);
		addToolClass("shovel");

		effectiveBlocks.addAll(ItemSpade.field_150916_c);
		effectiveMaterials.add(Material.ground);
		effectiveMaterials.add(Material.grass);
		effectiveMaterials.add(Material.sand);
		effectiveMaterials.add(Material.snow);
		effectiveMaterials.add(Material.craftedSnow);
		effectiveMaterials.add(Material.clay);
	}

}
