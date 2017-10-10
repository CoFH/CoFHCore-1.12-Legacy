package cofh.core.enchantment;

import cofh.core.item.IEnchantableItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public class EnchantmentInsight extends Enchantment {

	public static boolean enable = true;

	public EnchantmentInsight(String id) {

		super(Rarity.UNCOMMON, EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND });
		setRegistryName(id);
	}

	@Override
	public int getMinEnchantability(int level) {

		return 10 + (level - 1) * 9;
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

		return "enchant.cofh.insight";
	}

	@Override
	public boolean canApply(ItemStack stack) {

		return enable && (stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemTool || stack.getItem() instanceof IEnchantableItem && ((IEnchantableItem) stack.getItem()).canEnchant(stack, this));
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {

		return canApply(stack);
	}

	@Override
	public boolean canApplyTogether(Enchantment ench) {

		return super.canApplyTogether(ench) && ench != Enchantments.SILK_TOUCH;
	}

	@Override
	public boolean isAllowedOnBooks() {

		return true;
	}

}
