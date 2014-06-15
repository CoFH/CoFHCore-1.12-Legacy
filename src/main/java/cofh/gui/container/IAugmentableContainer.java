package cofh.gui.container;

import net.minecraft.inventory.Slot;

/**
 * Implement this interface on Container objects (the backend of a GUI). These are basically passthrough functions which should call back to the Tile Entity.
 * 
 * @author King Lemming
 * 
 */
public interface IAugmentableContainer {

	/**
	 * Install the Augmentations. Basically a callback to the Tile Entity that the container has.
	 */
	void augmentTile();

	/**
	 * Returns the Augment slots.
	 */
	Slot[] getAugmentSlots();

	/**
	 * Returns the status of each upgrade in the slots - should return false for a given Upgrade if a requirement is not met. Used on the tab display.
	 */
	boolean[] getAugmentStatus();

}
