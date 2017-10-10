package cofh.core.enchantment;

import cofh.core.item.IEnchantableItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentHolding extends Enchantment {

	public static boolean enable = true;

	public EnchantmentHolding(String id) {

		super(Rarity.UNCOMMON, EnumEnchantmentType.ALL, EntityEquipmentSlot.values());
		setRegistryName(id);
	}

	@Override
	public int getMinEnchantability(int level) {

		return 1 + (level - 1) * 10;
	}

	@Override
	public int getMaxEnchantability(int level) {

		return getMinEnchantability(level) + 50;
	}

	@Override
	public int getMaxLevel() {

		return 4;
	}

	@Override
	public String getName() {

		return "enchant.cofh.holding";
	}

	@Override
	public boolean canApply(ItemStack stack) {

		return enable && stack.getItem() instanceof IEnchantableItem && ((IEnchantableItem) stack.getItem()).canEnchant(stack, this);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {

		return canApply(stack);
	}

	@Override
	public boolean isAllowedOnBooks() {

		return true;
	}

}
