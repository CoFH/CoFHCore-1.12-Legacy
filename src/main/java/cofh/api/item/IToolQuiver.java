package cofh.api.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Implement this interface on subclasses of Item to have that item work as a custom ammo source (quiver, special arrow) for CoFH mods.
 */
public interface IToolQuiver {

	EntityArrow createEntityArrow(World world, ItemStack item, EntityLivingBase shooter);

	/**
	 * Determine if the Quiver will allow a bow to override its arrow. The quiver will still deplete even if it defers.
	 *
	 * @param item ItemStack representing the quiver.
	 * @return If the bow is allowed to generate a custom ArrowEntity using the ammo provided by this quiver.
	 */
	default boolean allowCustomArrowOverride(ItemStack item) {

		return true;
	}

	boolean isEmpty(ItemStack item, EntityLivingBase shooter);

	void onArrowFired(ItemStack item, EntityLivingBase shooter);

}
