package cofh.api.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Implement this interface on subclasses of Item to have that item work as a bow for CoFH mods.
 */
public interface IToolBow {

	/**
	 * Callback for when a bow is fired. Used for example, if energy should be drained.
	 *
	 * @param player Player holding the bow.
	 * @param item   ItemStack representing the bow.
	 */
	default void onBowFired(EntityPlayer player, ItemStack item) {

	}

	default boolean hasCustomArrow(ItemStack item) {

		return false;
	}

	default EntityArrow createEntityArrow(World world, ItemStack item, EntityLivingBase shooter) {

		return null;
	}

	/**
	 * This number is ADDED to 1.0F - it is damage above the standard.
	 *
	 * @param item ItemStack representing the bow.
	 * @return Additional damage over 100% - 0.1F would be 10% more.
	 */
	default float getArrowDamageMultiplier(ItemStack item) {

		return 0.0F;
	}

	/**
	 * This number is ADDED to 1.0F - it is speed above the standard.
	 *
	 * @param item ItemStack representing the bow.
	 * @return Additional speed over 100% - 0.1F would be 10% more.
	 */
	default float getArrowSpeedMultiplier(ItemStack item) {

		return 0.0F;
	}

}
