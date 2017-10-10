package cofh.core.enchantment;

import cofh.core.item.IEnchantableItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class EnchantmentVorpal extends Enchantment {

	public static boolean enable = true;
	public static final int CRIT_CHANCE = 5;
	public static final int CRIT_DAMAGE = 10;
	public static final int HEAD_CHANCE = 20;

	public EnchantmentVorpal(String id) {

		super(Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND });
		setRegistryName(id);
	}

	@Override
	public int getMinEnchantability(int level) {

		return 15 + (level - 1) * 9;
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

		return "enchant.cofh.vorpal";
	}

	@Override
	public boolean canApply(ItemStack stack) {

		return enable && (stack.getItem() instanceof ItemSword || stack.getItem() instanceof IEnchantableItem && ((IEnchantableItem) stack.getItem()).canEnchant(stack, this));
	}

	@Override
	public boolean isAllowedOnBooks() {

		return false;
	}

}
