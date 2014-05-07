package skyboy.core.world;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class WorldProxy extends World {

	protected World proxiedWorld;

	private static String getPar2String(World world) {

		return world.getWorldInfo().getWorldName();
	}

	private static WorldSettings getPar4WorldSettings(World world) {

		return new WorldSettings(world.getWorldInfo());
	}

	public WorldProxy(World world) {

		super(world.getSaveHandler(), getPar2String(world), world.provider, getPar4WorldSettings(world), world.theProfiler);
		this.proxiedWorld = world;
		// perWorldStorage = world.perWorldStorage; // final, set in super; requires reflection
		ReflectionHelper.setPrivateValue(World.class, this, world.perWorldStorage, new String[] { "perWorldStorage" }); // forge-added, no reobf
		scheduledUpdatesAreImmediate = world.scheduledUpdatesAreImmediate;
		loadedEntityList = world.loadedEntityList;
		loadedTileEntityList = world.loadedTileEntityList;
		playerEntities = world.playerEntities;
		weatherEffects = world.weatherEffects;
		skylightSubtracted = world.skylightSubtracted;
		prevRainingStrength = world.prevRainingStrength;
		rainingStrength = world.rainingStrength;
		prevThunderingStrength = world.prevThunderingStrength;
		thunderingStrength = world.thunderingStrength;
		lastLightningBolt = world.lastLightningBolt;
		difficultySetting = world.difficultySetting;
		rand = world.rand;
		// provider = world.provider; // handled by super
		findingSpawnPoint = world.findingSpawnPoint;
		mapStorage = world.mapStorage;
		villageCollectionObj = world.villageCollectionObj;
		// theProfiler = world.theProfiler; // handled by super
		isRemote = world.isRemote;
	}

	@Override
	protected IChunkProvider createChunkProvider() {

		return null;
	}

	@Override
	public Entity getEntityByID(int var1) {

		return null;
	}
}
