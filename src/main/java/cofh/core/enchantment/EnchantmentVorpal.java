package cofh.core.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class EnchantmentVorpal extends Enchantment {

	public EnchantmentVorpal(String id) {

		super(Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND });
		setRegistryName(id);
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

		return "enchant.cofh.vorpal";
	}

	@Override
	public boolean canApply(ItemStack stack) {

		return (stack.getItem() instanceof ItemSword);
	}

	@Override
	public boolean isAllowedOnBooks() {

		return false;
	}

}
