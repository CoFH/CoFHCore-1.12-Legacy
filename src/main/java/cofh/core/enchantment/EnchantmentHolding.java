package cofh.core.enchantment;

import cofh.api.item.IInventoryContainerItem;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentHolding extends Enchantment {

	public EnchantmentHolding() {

		super(Rarity.RARE, EnumEnchantmentType.ALL, new EntityEquipmentSlot[]{}); //TODO figure out if there needs to be equipment slot defined here
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
	public boolean canApply(ItemStack stack) {

		return (stack.getItem() instanceof IInventoryContainerItem);
	}

	@Override
	public boolean isAllowedOnBooks() {

		return false;
	}

}
