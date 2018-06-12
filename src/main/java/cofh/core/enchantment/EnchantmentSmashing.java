package cofh.core.enchantment;

import cofh.core.item.IEnchantableItem;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.StringHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Map;

public class EnchantmentSmashing extends Enchantment {

	public static boolean enable = true;

	public EnchantmentSmashing(String id) {

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

		return "enchantment.cofhcore.smashing";
	}

	@Override
	public boolean canApply(ItemStack stack) {

		Item item = stack.getItem();
		return enable && (item.getToolClasses(stack).contains("pickaxe") || item instanceof IEnchantableItem && ((IEnchantableItem) item).canEnchant(stack, this));
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
	public static final int ORE_MULTIPLIER = 2;
	public static final int ORE_MULTIPLIER_SPECIAL = 3;
	public static Map<String, SmashConversion> smashList = new Object2ObjectOpenHashMap<>();

	public static class SmashConversion {

		final String ore;
		final int count;

		SmashConversion(String ore, int count) {

			this.ore = ore;
			this.count = count;
		}

		ItemStack toItemStack() {

			return ItemHelper.getOre(ore, count);
		}
	}

	public static ItemStack getItemStack(ItemStack stack) {

		SmashConversion result = smashList.get(ItemHelper.getOreName(stack));
		if (result == null) {
			return ItemStack.EMPTY;
		}
		ItemStack ret = result.toItemStack();
		// TODO this may make a stack that's >maxStackSize
		return ret.isEmpty() ? ItemStack.EMPTY : ItemHelper.cloneStack(ret, ret.getCount() * stack.getCount());
	}

	public static void initialize() {

		/* GENERAL SCAN */
		{
			String oreType;
			for (String oreName : OreDictionary.getOreNames()) {
				if (oreName.startsWith("ore") || oreName.startsWith("gem")) {
					oreType = oreName.substring(3, oreName.length());
					addConversions(oreType);
				} else if (oreName.startsWith("dust")) {
					oreType = oreName.substring(4, oreName.length());
					addConversions(oreType);
				}
			}
		}
	}

	private static void addConversions(String oreType) {

		if (oreType == null || oreType.isEmpty()) {
			return;
		}
		String suffix = StringHelper.titleCase(oreType);

		String oreName = "ore" + suffix;
		String gemName = "gem" + suffix;
		String dustName = "dust" + suffix;

		String oreNetherName = "oreNether" + suffix;
		String oreEndName = "oreEnd" + suffix;

		if (ItemHelper.oreNameExists(gemName)) {
			addConversion(oreName, gemName, ORE_MULTIPLIER);
			addConversion(oreNetherName, gemName, ORE_MULTIPLIER_SPECIAL);
			addConversion(oreEndName, gemName, ORE_MULTIPLIER_SPECIAL);
		} else if (ItemHelper.oreNameExists(dustName)) {
			addConversion(oreName, dustName, ORE_MULTIPLIER);
			addConversion(oreNetherName, dustName, ORE_MULTIPLIER_SPECIAL);
			addConversion(oreEndName, dustName, ORE_MULTIPLIER_SPECIAL);
		}
	}

	private static boolean addConversion(String oreName, String resultName, int count) {

		if (oreName.isEmpty() || resultName.isEmpty() || count <= 0 || smashList.containsKey(oreName)) {
			return false;
		}
		smashList.put(oreName, new SmashConversion(resultName, count));
		return true;
	}

}
