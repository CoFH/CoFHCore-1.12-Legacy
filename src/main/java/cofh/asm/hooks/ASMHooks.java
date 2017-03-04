package cofh.asm.hooks;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ASMHooks {

	public static void preGenerateWorld(World world, int chunkX, int chunkZ) {

		MinecraftForge.EVENT_BUS.post(new ModPopulateChunkEvent.Pre(world, chunkX, chunkZ));
	}

	public static void postGenerateWorld(World world, int chunkX, int chunkZ) {

		MinecraftForge.EVENT_BUS.post(new ModPopulateChunkEvent.Post(world, chunkX, chunkZ));
	}

}
