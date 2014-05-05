package cofh.item;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;

public class ItemAxeAdv extends ItemToolAdv {

	public ItemAxeAdv(Item.ToolMaterial toolMaterial) {

		super(3.0F, toolMaterial);
		addToolClass("axe");

		effectiveBlocks.addAll(ItemAxe.field_150917_c);
		effectiveMaterials.add(Material.wood);
		effectiveMaterials.add(Material.plants);
		effectiveMaterials.add(Material.vine);
	}

}
