package cofh.core.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentSoulbound extends Enchantment {

	public static boolean enable = true;

	public static final int REUSE_CHANCE = 10;

	public EnchantmentSoulbound(String id) {

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

		return 3;
	}

	@Override
	public String getName() {

		return "enchantment.cofhcore.soulbound";
	}

	@Override
	public boolean canApply(ItemStack stack) {

		return enable && super.canApply(stack);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {

		return enable && super.canApplyAtEnchantingTable(stack);
	}

	@Override
	public boolean canApplyTogether(Enchantment ench) {

		return super.canApplyTogether(ench) && ench != Enchantments.VANISHING_CURSE;
	}

	@Override
	public boolean isAllowedOnBooks() {

		return enable;
	}

}
