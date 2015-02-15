package cofh.core.enchantment;

import cofh.lib.util.helpers.StringHelper;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;

public class EnchantmentMultishot extends Enchantment {

	public EnchantmentMultishot(int id) {

		super(id, 5, EnumEnchantmentType.bow);
	}

	@Override
	public int getMinEnchantability(int level) {

		return 5 + (level - 1) * 10;
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

		return "enchant.cofh.multishot";
	}

	@Override
	public String getTranslatedName(int level) {

		return StringHelper.localize(getName()) + " " + StringHelper.ROMAN_NUMERAL[level];
	}

	@Override
	public boolean canApply(ItemStack stack) {

		return (stack.getItem() instanceof ItemBow);
	}

	@Override
	public boolean isAllowedOnBooks() {

		return false;
	}

}
