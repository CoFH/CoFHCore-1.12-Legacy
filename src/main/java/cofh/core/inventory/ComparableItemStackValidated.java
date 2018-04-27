package cofh.core.inventory;

import cofh.core.util.helpers.ItemHelper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * This is an implementation of a ComparableItemStack - where the oreName/Id is constrained to an allowed subset, specified by the validator.
 *
 * @author King Lemming
 */
public class ComparableItemStackValidated extends ComparableItemStack {

	private final OreValidator validator;

	public ComparableItemStackValidated(ItemStack stack) {

		super(stack);
		this.validator = DEFAULT_VALIDATOR;
		this.oreID = getOreID(stack);
		this.oreName = ItemHelper.oreProxy.getOreName(oreID);
	}

	public ComparableItemStackValidated(ItemStack stack, @Nonnull OreValidator validator) {

		super(stack);
		this.validator = validator;
		this.oreID = getOreID(stack);
		this.oreName = ItemHelper.oreProxy.getOreName(oreID);
	}

	public int getOreID(ItemStack stack) {

		List<Integer> ids = ItemHelper.oreProxy.getAllOreIDs(stack);
		if (!ids.isEmpty()) {
			for (Integer id : ids) {
				if (id != -1 && validator.validate(ItemHelper.oreProxy.getOreName(id))) {
					return id;
				}
			}
		}
		return -1;
	}

	public int getOreID(String oreName) {

		if (!validator.validate(oreName)) {
			return -1;
		}
		return ItemHelper.oreProxy.getOreID(oreName);
	}

}
