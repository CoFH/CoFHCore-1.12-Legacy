package cofh.core.enchantment;

import cofh.core.item.IEnchantableItem;
import cofh.core.util.helpers.ItemHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;

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

		return "enchantment.cofhcore.smelting";
	}

	@Override
	public boolean canApply(ItemStack stack) {

		Item item = stack.getItem();
		return enable && (item instanceof ItemTool || item instanceof IEnchantableItem && ((IEnchantableItem) item).canEnchant(stack, this));
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

		return enable;
	}

	/* CONVERSION */
	public static ItemStack getItemStack(ItemStack stack) {

		ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
		// TODO this may make a stack that's >maxStackSize
		return result.isEmpty() ? ItemStack.EMPTY : ItemHelper.cloneStack(result, result.getCount() * stack.getCount());
	}

}
