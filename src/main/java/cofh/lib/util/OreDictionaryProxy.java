package cofh.lib.util;

import cofh.lib.util.helpers.ItemHelper;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Don't instantiate this or call these methods in any way. Use the methods in {@link ItemHelper}.
 *
 * @author King Lemming
 *
 */
@SuppressWarnings("deprecation")
public class OreDictionaryProxy {

	public ItemStack getOre(String oreName) {

		if (!oreNameExists(oreName)) {
			return null;
		}
		return ItemHelper.cloneStack(OreDictionary.getOres(oreName).get(0), 1);
	}

	public int getOreID(ItemStack stack) {

		return OreDictionary.getOreID(stack);
	}

	public int getOreID(String oreName) {

		return OreDictionary.getOreID(oreName);
	}

	public String getOreName(ItemStack stack) {

		return OreDictionary.getOreName(OreDictionary.getOreID(stack));
	}

	public String getOreName(int oreID) {

		return OreDictionary.getOreName(oreID);
	}

	public boolean isOreIDEqual(ItemStack stack, int oreID) {

		return OreDictionary.getOreID(stack) == oreID;
	}

	public boolean isOreNameEqual(ItemStack stack, String oreName) {

		return OreDictionary.getOreName(OreDictionary.getOreID(stack)).equals(oreName);
	}

	public boolean oreNameExists(String oreName) {

		return OreDictionary.doesOreNameExist(oreName);
	}

}
