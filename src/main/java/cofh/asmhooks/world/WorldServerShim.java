package cofh.asmhooks.world;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

/**
 * Do not extend this class directly, extend WorldServerProxy instead. <br>
 * This class is never used at runtime, and is simply a compile-time shim.
 */
public abstract class WorldServerShim extends WorldServer {

	public WorldServerShim(MinecraftServer server, ISaveHandler saveHandler, WorldInfo info, WorldProvider provider, Profiler profiler, boolean isRemote) {

		super(server, saveHandler, info, provider.getDimension(), profiler);
		throw new IllegalAccessError("WorldServerShim cannot be extended. Extend WorldServerProxy instead.");
	}

}
