package cofh.core.item.tool;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemAxe;

public class ItemAxeAdv extends ItemToolAdv {

	public ItemAxeAdv(ToolMaterial toolMaterial) {

		this(-3.2F, toolMaterial);
	}

	public ItemAxeAdv(float attackSpeed, ToolMaterial toolMaterial) {

		super(3.0F, attackSpeed, toolMaterial);
		addToolClass("axe");

		effectiveBlocks.addAll(ItemAxe.EFFECTIVE_ON);

		effectiveMaterials.add(Material.WOOD);
		effectiveMaterials.add(Material.PLANTS);
		effectiveMaterials.add(Material.VINE);
		effectiveMaterials.add(Material.CACTUS);
		effectiveMaterials.add(Material.GOURD);

		damageVsEntity = damageVsEntity + 1;
	}

}
