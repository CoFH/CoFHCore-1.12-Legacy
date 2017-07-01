package cofh.core.entity;

import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.NBTHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DropHandler {

	public static DropHandler instance = new DropHandler();

	public static boolean playerPvPOnly = true;
	public static boolean mobPvEOnly = true;

	public static boolean playersEnabled = true;
	public static boolean creeperEnabled = true;
	public static boolean skeletonEnabled = true;
	public static boolean witherSkeletonEnabled = false;
	public static boolean zombieEnabled = true;

	public static int playerChance = 5;
	public static int creeperChance = 5;
	public static int skeletonChance = 5;
	public static int witherSkeletonChance = 5;
	public static int zombieChance = 5;

	@SubscribeEvent
	public void onLivingDrops(LivingDropsEvent event) {

		if (!event.getEntityLiving().world.getGameRules().getBoolean("doMobLoot")) {
			return;
		}

		int randPerc = MathHelper.RANDOM.nextInt(100);
		ItemStack itemSkull = ItemStack.EMPTY;

		if (event.getEntity() instanceof EntityPlayerMP) {
			if (event.isRecentlyHit() || !playerPvPOnly) {
				if (playersEnabled && randPerc < playerChance) {
					EntityPlayer thePlayer = (EntityPlayerMP) event.getEntity();
					itemSkull = new ItemStack(Items.SKULL, 1, 3);
					NBTHelper.setString(itemSkull, "SkullOwner", thePlayer.getName());
				}
			}
		} else if (event.isRecentlyHit() || !mobPvEOnly) {
			if (event.getEntity() instanceof AbstractSkeleton) {
				AbstractSkeleton skeleton = (AbstractSkeleton) event.getEntity();

				if (skeleton instanceof EntitySkeleton && skeletonEnabled && randPerc < skeletonChance) {
					itemSkull = new ItemStack(Items.SKULL, 1, 0);
				} else if (skeleton instanceof EntityWitherSkeleton && witherSkeletonEnabled && randPerc < witherSkeletonChance) {
					itemSkull = new ItemStack(Items.SKULL, 1, 1);
				}
			} else if (event.getEntity() instanceof EntityZombie && zombieEnabled && randPerc < zombieChance) {
				itemSkull = new ItemStack(Items.SKULL, 1, 2);
			} else if (event.getEntity() instanceof EntityCreeper && creeperEnabled && randPerc < creeperChance) {
				itemSkull = new ItemStack(Items.SKULL, 1, 4);
			}
		}
		if (itemSkull.isEmpty()) {
			return;
		}
		EntityLivingBase living = event.getEntityLiving();
		EntityItem theDrop = new EntityItem(living.world, living.posX, living.posY, living.posZ, itemSkull);
		theDrop.setPickupDelay(10);
		event.getDrops().add(theDrop);
	}

}
