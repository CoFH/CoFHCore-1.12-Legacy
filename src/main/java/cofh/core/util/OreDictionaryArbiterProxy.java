package cofh.core.util;

import cofh.lib.util.OreDictionaryProxy;
import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * If CoFHCore is present, an instance of this class is initialized by the OreDictionaryArbiter and the functionality in ItemHelper is much improved.
 *
 * Translation: Don't touch.
 *
 * @author King Lemming
 *
 */
public class OreDictionaryArbiterProxy extends OreDictionaryProxy {

	@Override
	public final ItemStack getOre(String oreName) {

		if (OreDictionaryArbiter.getOres(oreName) == null) {
			return null;
		}
		return ItemHelper.cloneStack(OreDictionaryArbiter.getOres(oreName).get(0), 1);
	}

	@Override
	public final List<Integer> getOreIDs(ItemStack stack) {

		return OreDictionaryArbiter.getOreIDs(stack);
	}

	@Override
	public final int getOreID(String oreName) {

		return OreDictionaryArbiter.getOreID(oreName);
	}

	@Override
	public final List<String> getOreNames(ItemStack stack) {

		return OreDictionaryArbiter.getOreNames(OreDictionaryArbiter.getOreIDs(stack));
	}

	@Override
	public final String getOreName(int oreID) {

		return OreDictionaryArbiter.getOreName(oreID);
	}

	@Override
	public final boolean isOreIDEqual(ItemStack stack, int oreID) {

		return OreDictionaryArbiter.getOreIDs(stack).contains(oreID);
	}

	@Override
	public final boolean isOreNameEqual(ItemStack stack, String oreName) {

		return OreDictionaryArbiter.getOreNames(OreDictionaryArbiter.getOreIDs(stack)).contains(oreName);
	}

	@Override
	public final boolean oreNameExists(String oreName) {

		return OreDictionaryArbiter.getOres(oreName) != null;
	}

}

