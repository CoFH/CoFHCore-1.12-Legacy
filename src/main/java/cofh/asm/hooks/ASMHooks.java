package cofh.asm.hooks;

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

}
