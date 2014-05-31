package skyboy.core.world;

import cpw.mods.fml.relauncher.ReflectionHelper;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;

public abstract class WorldServerProxy extends WorldServerShim {

	protected WorldServer proxiedWorld;

	private static String getWorldName(World world) {

		return world.getWorldInfo().getWorldName();
	}

	private static WorldSettings getWorldSettings(World world) {

		return new WorldSettings(world.getWorldInfo());
	}

	public WorldServerProxy(WorldServer world) {

		super(world.func_73046_m(), world.getSaveHandler(), getWorldName(world), world.provider, getWorldSettings(world), world.theProfiler);
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
		theChunkProviderServer = world.theChunkProviderServer;
		levelSaving = world.levelSaving;
		// allPlayersSleeping = world.allPlayersSleeping;
		customTeleporters = world.customTeleporters;
	}

}
