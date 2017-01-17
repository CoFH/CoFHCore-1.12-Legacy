package cofh.core.enchantment;

import cofh.core.item.tool.ItemBowCore;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentMultishot extends Enchantment {

	public EnchantmentMultishot(String id) {

		super(Rarity.UNCOMMON, EnumEnchantmentType.BOW, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND });
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

		return "enchant.cofh.multishot";
	}

	@Override
	public boolean canApply(ItemStack stack) {

		return (stack.getItem() instanceof ItemBowCore);
	}

	@Override
	public boolean isAllowedOnBooks() {

		return false;
	}

}
