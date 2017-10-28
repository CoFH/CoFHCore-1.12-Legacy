package cofh.core.item.tool;

import cofh.core.init.CoreEnchantments;
import cofh.core.item.IEnchantableItem;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;

public class ItemAxeCore extends ItemToolCore implements IEnchantableItem {

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

	/* IEnchantableItem */
	@Override
	public boolean canEnchant(ItemStack stack, Enchantment enchantment) {

		return enchantment == CoreEnchantments.leech || enchantment == CoreEnchantments.vorpal;
	}

}
