package cofh.core.item.tool;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemPickaxe;

public class ItemPickaxeAdv extends ItemToolAdv {

	public ItemPickaxeAdv(ToolMaterial toolMaterial) {

		super(2.0F, toolMaterial);
		addToolClass("pickaxe");

		effectiveBlocks.addAll(ItemPickaxe.field_150915_c);
		effectiveMaterials.add(Material.iron);
		effectiveMaterials.add(Material.anvil);
		effectiveMaterials.add(Material.rock);
	}

}
