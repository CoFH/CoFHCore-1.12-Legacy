package cofh.lib.gui.container;

import net.minecraft.inventory.Slot;

/**
 * Implement this interface on Container objects (the backend of a GUI). These are basically passthrough functions which should call back to the Tile Entity.
 *
 * @author King Lemming
 *
 */
public interface IAugmentableContainer {

	/**
	 * Used by a tab to set lock status so that new Augments cannot be added (there may be many reasons for this).
	 */
	void setAugmentLock(boolean lock);

	/**
	 * Returns the Augment slots.
	 */
	Slot[] getAugmentSlots();

}
