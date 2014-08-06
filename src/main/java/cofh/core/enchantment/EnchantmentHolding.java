package cofh.core.enchantment;

import cofh.api.item.IInventoryContainerItem;
import cofh.lib.util.helpers.StringHelper;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;

public class EnchantmentHolding extends Enchantment {

	public EnchantmentHolding(int id) {

		super(id, 2, EnumEnchantmentType.all);
	}

	@Override
	public int getMinEnchantability(int level) {

		return 1 + (level - 1) * 10;
	}

	@Override
	public int getMaxEnchantability(int level) {

		return getMinEnchantability(level) + 15;
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
	public String getTranslatedName(int level) {

		return StringHelper.localize(getName()) + " " + StringHelper.ROMAN_NUMERAL[level];
	}

	@Override
	public boolean canApply(ItemStack stack) {

		return (stack.getItem() instanceof IInventoryContainerItem);
	}

	@Override
	public boolean isAllowedOnBooks() {

		return false;
	}

}
