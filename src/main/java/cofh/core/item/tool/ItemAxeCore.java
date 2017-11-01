package cofh.core.item.tool;

import cofh.core.init.CoreEnchantments;
import cofh.core.item.IEnchantableItem;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;

public class ItemAxeCore extends ItemToolCore implements IEnchantableItem {

	public ItemAxeCore(ToolMaterial toolMaterial) {

		super(3.0F, -3.2F, toolMaterial);
		addToolClass("axe");

		effectiveBlocks.addAll(ItemAxe.EFFECTIVE_ON);

		effectiveMaterials.add(Material.WOOD);
		effectiveMaterials.add(Material.PLANTS);
		effectiveMaterials.add(Material.VINE);
		effectiveMaterials.add(Material.CACTUS);
		effectiveMaterials.add(Material.GOURD);

		if (harvestLevel > 0) {
			attackDamage = 8.0F;
			attackSpeed = -3.3F + (0.1F * harvestLevel);
		} else {
			attackDamage = 6.0F;
			attackSpeed = -3.2F + (0.1F * (int) (efficiency / 5));
		}
	}

	/* IEnchantableItem */
	@Override
	public boolean canEnchant(ItemStack stack, Enchantment enchantment) {

		return enchantment == CoreEnchantments.leech || enchantment == CoreEnchantments.vorpal;
	}

}
