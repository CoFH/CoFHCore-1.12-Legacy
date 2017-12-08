package cofh.core.inventory;

import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.oredict.OreDictionaryArbiter;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

/**
 * This is basically a default "safe" implementation of a ComparableItemStackNBT - the OreID will only be used for the 5 "basic" conventions.
 *
 * @author King Lemming
 */
public class ComparableItemStackSafeNBT extends ComparableItemStackNBT {

	public static final String BLOCK = "block";
	public static final String ORE = "ore";
	public static final String DUST = "dust";
	public static final String INGOT = "ingot";
	public static final String NUGGET = "nugget";

	public boolean safeOreType(String oreName) {

		return oreName.startsWith(BLOCK) || oreName.startsWith(ORE) || oreName.startsWith(DUST) || oreName.startsWith(INGOT) || oreName.startsWith(NUGGET);
	}

	public int getOreID(ItemStack stack) {

		ArrayList<Integer> ids = OreDictionaryArbiter.getAllOreIDs(stack);
		if (!ids.isEmpty()) {
			for (Integer id : ids) {
				if (id != -1 && safeOreType(ItemHelper.oreProxy.getOreName(id))) {
					return id;
				}
			}
		}
		return -1;
	}

	public int getOreID(String oreName) {

		if (!safeOreType(oreName)) {
			return -1;
		}
		return ItemHelper.oreProxy.getOreID(oreName);
	}

	public ComparableItemStackSafeNBT(ItemStack stack) {

		super(stack);
		oreID = getOreID(stack);
	}

}
