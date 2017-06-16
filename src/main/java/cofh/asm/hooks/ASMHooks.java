package cofh.asm.hooks;

import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ASMHooks {

	private static List<IModGenerateHook> preGenWorldListeners = new ArrayList<>();
	private static List<IModGenerateHook> postGenWorldListeners = new ArrayList<>();

	public static void registerPreGenHook(IModGenerateHook hook) {

		preGenWorldListeners.add(hook);
	}

	public static void registerPostGenHook(IModGenerateHook hook) {

		postGenWorldListeners.add(hook);
	}

	public static void preGenerateWorld(World world, int chunkX, int chunkZ) {

		for (IModGenerateHook pre : preGenWorldListeners) {
			pre.onGeneration(world, chunkX, chunkZ);
		}
	}

	public static void postGenerateWorld(World world, int chunkX, int chunkZ) {

		for (IModGenerateHook pre : postGenWorldListeners) {
			pre.onGeneration(world, chunkX, chunkZ);
		}
	}

	public interface IModGenerateHook {

		void onGeneration(World world, int chunkX, int chunkZ);
	}

	public static boolean shouldStopFishing(EntityFishHook fishHook) {

		ItemStack mainHand = fishHook.angler.getHeldItemMainhand();
		ItemStack offHand = fishHook.angler.getHeldItemOffhand();
		boolean flag = mainHand.getItem() instanceof ItemFishingRod;
		boolean flag1 = offHand.getItem() instanceof ItemFishingRod;

		if (!fishHook.angler.isDead && fishHook.angler.isEntityAlive() && (flag || flag1) && fishHook.getDistanceSqToEntity(fishHook.angler) <= 1024.0D) {
			return false;
		} else {
			fishHook.setDead();
			return true;
		}
	}

}
