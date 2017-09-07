package cofh.core.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentSmelting extends Enchantment {

	public static boolean enable = true;

	public EnchantmentSmelting(String id) {

		super(Rarity.RARE, EnumEnchantmentType.DIGGER, EntityEquipmentSlot.values());
		setRegistryName(id);
	}

	@Override
	public int getMinEnchantability(int level) {

		return 15;
	}

	@Override
	public int getMaxEnchantability(int level) {

		return getMinEnchantability(level) + 50;
	}

	@Override
	public int getMaxLevel() {

		return 1;
	}

	@Override
	public String getName() {

		return "enchant.cofh.smelting";
	}

	//	@Override
	//	public boolean canApply(ItemStack stack) {
	//
	//		return enable && stack.getItem() instanceof IEnchantableItem && ((IEnchantableItem) stack.getItem()).canEnchant(stack, this);
	//	}
	//
	//	@Override
	//	public boolean canApplyAtEnchantingTable(ItemStack stack) {
	//
	//		return canApply(stack);
	//	}

	@Override
	public boolean isAllowedOnBooks() {

		return true;
	}

}
