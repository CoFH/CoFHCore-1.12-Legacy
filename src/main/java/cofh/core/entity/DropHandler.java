package cofh.core.entity;

import cofh.lib.util.helpers.MathHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

public class DropHandler {

	public static DropHandler instance = new DropHandler();

	@SubscribeEvent
	public void onLivingDrops(LivingDropsEvent event) {

		if (event.specialDropValue < 5) {
			return;
		}
		int randPerc = MathHelper.RANDOM.nextInt(100);
		ItemStack itemSkull = null;

		if (event.entity instanceof EntityPlayerMP) {
			if (event.recentlyHit || !playerPvPOnly) {
				if (playersEnabled && randPerc < playerChance) {
					EntityPlayer thePlayer = (EntityPlayerMP) event.entity;
					itemSkull = new ItemStack(Items.skull, 1, 3);
					itemSkull.stackTagCompound = new NBTTagCompound();
					itemSkull.stackTagCompound.setString("SkullOwner", thePlayer.getCommandSenderName());
				}
			}
		} else if (event.recentlyHit || !mobPvEOnly) {
			if (event.entity instanceof EntitySkeleton) {
				EntitySkeleton theEntity = (EntitySkeleton) event.entity;

				if (theEntity.getSkeletonType() == 0 && skeletonEnabled && randPerc < skeletonChance) {
					itemSkull = new ItemStack(Items.skull, 1, 0);
				} else if (theEntity.getSkeletonType() == 1 && witherSkeletonEnabled && randPerc < witherSkeletonChance) {
					itemSkull = new ItemStack(Items.skull, 1, 1);
				}
			} else if (event.entity instanceof EntityZombie && zombieEnabled && randPerc < zombieChance) {
				itemSkull = new ItemStack(Items.skull, 1, 2);
			} else if (event.entity instanceof EntityCreeper && creeperEnabled && randPerc < creeperChance) {
				itemSkull = new ItemStack(Items.skull, 1, 4);
			}
		}
		if (itemSkull == null) {
			return;
		}
		EntityItem theDrop = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, itemSkull);
		theDrop.delayBeforeCanPickup = 10;
		event.drops.add(theDrop);
	}

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

}
