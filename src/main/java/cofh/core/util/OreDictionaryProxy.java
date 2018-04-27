package cofh.core.util;

import cofh.core.util.helpers.ItemHelper;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

/**
 * Don't instantiate this or call these methods in any way. Use the methods in {@link ItemHelper}.
 *
 * @author King Lemming
 */
@SuppressWarnings ("deprecation")
public class OreDictionaryProxy {

	public ItemStack getOre(String oreName, int amount) {

		if (!oreNameExists(oreName)) {
			return ItemStack.EMPTY;
		}
		return ItemHelper.cloneStack(OreDictionary.getOres(oreName, false).get(0), amount);
	}

	public int getOreID(ItemStack stack) {

		return getOreID(getOreName(stack));
	}

	public int getOreID(String oreName) {

		if (Strings.isNullOrEmpty(oreName)) {
			return -1;
		}
		return OreDictionary.getOreID(oreName);
	}

	public List<Integer> getAllOreIDs(ItemStack stack) {

		return Ints.asList(OreDictionary.getOreIDs(stack));
	}

	public String getOreName(ItemStack stack) {

		int[] ids = OreDictionary.getOreIDs(stack);
		if (ids != null && ids.length >= 1) {
			return OreDictionary.getOreName(ids[0]);
		}
		return "";
	}

	public String getOreName(int oreID) {

		return OreDictionary.getOreName(oreID);
	}

	public boolean isOreIDEqual(ItemStack stack, int oreID) {

		return getOreID(stack) == oreID;
	}

	public boolean isOreNameEqual(ItemStack stack, String oreName) {

		return OreDictionary.getOreName(getOreID(stack)).equals(oreName);
	}

	public boolean oreNameExists(String oreName) {

		return OreDictionary.doesOreNameExist(oreName) && OreDictionary.getOres(oreName, false).size() > 0;
	}

}
