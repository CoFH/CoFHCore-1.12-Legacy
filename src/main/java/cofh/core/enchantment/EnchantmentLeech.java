package cofh.core.enchantment;

import cofh.core.item.IEnchantableItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class EnchantmentLeech extends Enchantment {

	public static boolean enable = true;

	public EnchantmentLeech(String id) {

		super(Rarity.UNCOMMON, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND });
		setRegistryName(id);
	}

	@Override
	public int getMinEnchantability(int level) {

		return 5 + (level - 1) * 10;
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

		return "enchant.cofh.leech";
	}

	@Override
	public boolean canApply(ItemStack stack) {

		return enable && (stack.getItem() instanceof ItemSword || stack.getItem() instanceof IEnchantableItem && ((IEnchantableItem) stack.getItem()).canEnchant(stack, this));
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
