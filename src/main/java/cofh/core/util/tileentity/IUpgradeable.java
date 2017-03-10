package cofh.core.util.tileentity;

import cofh.api.item.IUpgradeItem;
import net.minecraft.item.ItemStack;

/**
 * Implemented on objects which support Upgrades - items which incrementally increase the capabilities of an object, by increasing an internal "level."
 *
 * Effects of this are determined by the object itself and should be checked vs the Upgrade Type denoted in {@link IUpgradeItem}.
 *
 * @author King Lemming
 */
public interface IUpgradeable {

	/**
	 * Returns if an object can be upgraded by a given upgrade item.
	 */
	boolean canUpgrade(ItemStack upgrade);

	/**
	 * Attempt to install a specific upgrade in the (Tile) Entity.
	 *
	 * Returns TRUE if upgrade was installed properly.
	 */
	boolean installUpgrade(ItemStack upgrade);

	/**
	 * Returns the level of an upgradeable object.
	 */
	int getLevel();

}
