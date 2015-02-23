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
		ReflectionHelper.setPrivateValue(World.class, this, world.perWorldStorage, "perWorldStorage"); // forge-added, no reobf
		loadedEntityList = world.loadedEntityList;
		loadedTileEntityList = world.loadedTileEntityList;
		playerEntities = world.playerEntities;
		weatherEffects = world.weatherEffects;
		rand = world.rand;
		// provider = world.provider; // handled by super
		mapStorage = world.mapStorage;
		villageCollectionObj = world.villageCollectionObj;
		// theProfiler = world.theProfiler; // handled by super
		isRemote = world.isRemote;
		customTeleporters = world.customTeleporters;
		cofh_updateProps();
	}

	protected void cofh_updateProps() {

		scheduledUpdatesAreImmediate = proxiedWorld.scheduledUpdatesAreImmediate;
		skylightSubtracted = proxiedWorld.skylightSubtracted;
		prevRainingStrength = proxiedWorld.prevRainingStrength;
		rainingStrength = proxiedWorld.rainingStrength;
		prevThunderingStrength = proxiedWorld.prevThunderingStrength;
		thunderingStrength = proxiedWorld.thunderingStrength;
		lastLightningBolt = proxiedWorld.lastLightningBolt;
		difficultySetting = proxiedWorld.difficultySetting;
		findingSpawnPoint = proxiedWorld.findingSpawnPoint;
		theChunkProviderServer = proxiedWorld.theChunkProviderServer;
		allPlayersSleeping = proxiedWorld.allPlayersSleeping;
		levelSaving = proxiedWorld.levelSaving;
	}

}
