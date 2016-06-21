package cofh.core.enchantment;

import cofh.core.item.tool.ItemBowAdv;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentMultishot extends Enchantment {

	public EnchantmentMultishot(int id) {

		super(id, new ResourceLocation("cofh:multishot"), 5, EnumEnchantmentType.BOW);
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

		return (stack.getItem() instanceof ItemBowAdv);
	}

	@Override
	public boolean isAllowedOnBooks() {

		return false;
	}

}
