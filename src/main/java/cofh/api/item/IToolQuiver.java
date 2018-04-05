package cofh.api.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Implement this interface on subclasses of Item to have that item work as a quiver for CoFH mods.
 */
public interface IToolQuiver {

	EntityArrow createEntityArrow(World world, ItemStack item, EntityLivingBase shooter);

	boolean isEmpty(ItemStack item, EntityLivingBase shooter);

	void onArrowFired(ItemStack item, EntityLivingBase shooter);

}
