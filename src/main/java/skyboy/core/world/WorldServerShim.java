package skyboy.core.world;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;

/**
 * Do not extend this class directly, extend WorldServerProxy instead. <br>
 * This class is never used at runtime, and is simply a compile-time shim.
 */
public abstract class WorldServerShim extends WorldServer {

	public WorldServerShim(MinecraftServer minecraftServer, ISaveHandler saveHandler,
			String par2String, WorldProvider provider, WorldSettings par4WorldSettings,
			Profiler theProfiler) {
		super(minecraftServer, saveHandler, par2String, provider.dimensionId, par4WorldSettings, theProfiler);
		throw new IllegalAccessError("WorldServerShim cannot be extended. Extend WorldServerProxy instead.");
	}

}
