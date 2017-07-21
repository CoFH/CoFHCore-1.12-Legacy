package cofh.core.inventory;

import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.oredict.OreDictionaryArbiter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

/**
 * This is basically a default "safe" implementation of a ComparableItemStack - the OreID will only be used for the 5 "basic" conventions.
 *
 * @author King Lemming
 */
public class ComparableItemStackSafe extends ComparableItemStack {

	static final String BLOCK = "block";
	static final String ORE = "ore";
	static final String DUST = "dust";
	static final String INGOT = "ingot";
	static final String NUGGET = "nugget";

	public static boolean safeOreType(String oreName) {

		return oreName.startsWith(BLOCK) || oreName.startsWith(ORE) || oreName.startsWith(DUST) || oreName.startsWith(INGOT) || oreName.startsWith(NUGGET);
	}

	public static int getOreID(ItemStack stack) {

		ArrayList<Integer> ids = OreDictionaryArbiter.getAllOreIDs(stack);

		if (ids != null) {
			for (Integer id : ids) {
				if (id != -1 && safeOreType(ItemHelper.oreProxy.getOreName(id))) {
					return id;
				}
			}
		}
		return -1;
	}

	public static int getOreID(String oreName) {

		if (!safeOreType(oreName)) {
			return -1;
		}
		return ItemHelper.oreProxy.getOreID(oreName);
	}

	public ComparableItemStackSafe(ItemStack stack) {

		super(stack);
		oreID = getOreID(stack);
	}

	public ComparableItemStackSafe(Item item, int damage, int stackSize) {

		super(item, damage, stackSize);
		this.oreID = getOreID(this.toItemStack());
	}

	@Override
	public ComparableItemStackSafe set(ItemStack stack) {

		super.set(stack);
		oreID = getOreID(stack);

		return this;
	}

}
