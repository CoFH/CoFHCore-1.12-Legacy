package cofh.core.item.tool;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemAxe;

public class ItemAxeCore extends ItemToolCore {

	public ItemAxeCore(ToolMaterial toolMaterial) {

		this(-3.2F, toolMaterial);
	}

	public ItemAxeCore(float attackSpeed, ToolMaterial toolMaterial) {

		super(3.0F, attackSpeed, toolMaterial);
		addToolClass("axe");

		effectiveBlocks.addAll(ItemAxe.EFFECTIVE_ON);

		effectiveMaterials.add(Material.WOOD);
		effectiveMaterials.add(Material.PLANTS);
		effectiveMaterials.add(Material.VINE);
		effectiveMaterials.add(Material.CACTUS);
		effectiveMaterials.add(Material.GOURD);

		attackDamage = attackDamage + 1;
	}

}
