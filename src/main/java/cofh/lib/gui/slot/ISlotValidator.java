package cofh.lib.gui.slot;

import net.minecraft.item.ItemStack;

/**
 * Interface used in conjunction with {@link SlotValidated}.
 *
 * @author King Lemming
 */
@Deprecated//TODO, J8 Predicate
public interface ISlotValidator {

	/**
	 * Essentially a passthrough so an arbitrary criterion can be checked against.
	 */
	boolean isItemValid(ItemStack stack);

}
