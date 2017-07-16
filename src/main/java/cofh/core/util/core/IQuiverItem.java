package cofh.core.util.core;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IQuiverItem {

	EntityArrow createArrow(World world, ItemStack stack, EntityLivingBase shooter);

	boolean isEmpty(ItemStack stack, EntityLivingBase shooter);

	void onArrowFired(ItemStack stack, EntityLivingBase shooter);

}
