package cofh.api.core;

import cofh.api.item.IAugmentItem;
import net.minecraft.item.ItemStack;

/**
 * Implemented on objects which support Augments - these are modular and removable items which provide boosts or alterations to functionality.
 *
 * Effects of this are determined by the object itself and should be checked vs the Augment Type denoted in {@link IAugmentItem}.
 *
 * @author King Lemming
 */
public interface IAugmentable {

	/**
	 * Attempt to install a specific augment in the (Tile) Entity.
	 *
	 * Returns TRUE if augment was installed properly.
	 */
	boolean installAugment(ItemStack augment);

	/**
	 * Returns TRUE if a given augment is valid for the (Tile) Entity.
	 */
	boolean isValidAugment(ItemStack augment);

	/**
	 * Returns an array of the Augment slots for this (Tile) Entity.
	 */
	ItemStack[] getAugmentSlots();

	/**
	 * Updates the status of Augments in the (Tile) Entity. Should be called to reset and re-baseline.
	 */
	void updateAugmentStatus();

}
