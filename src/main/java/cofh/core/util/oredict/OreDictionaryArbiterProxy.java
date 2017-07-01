package cofh.core.util.oredict;

import cofh.core.util.OreDictionaryProxy;
import cofh.core.util.helpers.ItemHelper;
import net.minecraft.item.ItemStack;

/**
 * If CoFHCore is present, an instance of this class is initialized by the OreDictionaryArbiter and the functionality in ItemHelper is much improved.
 * <p>
 * Translation: Don't touch.
 *
 * @author King Lemming
 */
public class OreDictionaryArbiterProxy extends OreDictionaryProxy {

	@Override
	public final ItemStack getOre(String oreName) {

		if (OreDictionaryArbiter.getOres(oreName) == null) {
			return ItemStack.EMPTY;
		}
		return ItemHelper.cloneStack(OreDictionaryArbiter.getOres(oreName).get(0), 1);
	}

	@Override
	public final int getOreID(ItemStack stack) {

		return OreDictionaryArbiter.getOreID(stack);
	}

	@Override
	public final int getOreID(String oreName) {

		return OreDictionaryArbiter.getOreID(oreName);
	}

	@Override
	public final String getOreName(ItemStack stack) {

		return OreDictionaryArbiter.getOreName(OreDictionaryArbiter.getOreID(stack));
	}

	@Override
	public final String getOreName(int oreID) {

		return OreDictionaryArbiter.getOreName(oreID);
	}

	@Override
	public final boolean isOreIDEqual(ItemStack stack, int oreID) {

		return OreDictionaryArbiter.getOreID(stack) == oreID;
	}

	@Override
	public final boolean isOreNameEqual(ItemStack stack, String oreName) {

		return OreDictionaryArbiter.getOreName(OreDictionaryArbiter.getOreID(stack)).equals(oreName);
	}

	@Override
	public final boolean oreNameExists(String oreName) {

		return OreDictionaryArbiter.getOres(oreName) != null;
	}

}
