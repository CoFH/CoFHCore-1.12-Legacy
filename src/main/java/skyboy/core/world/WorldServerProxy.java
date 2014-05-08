package skyboy.core.world;

import cpw.mods.fml.relauncher.ReflectionHelper;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;

public class WorldServerProxy extends WorldServer {    
	protected WorldServer proxiedWorld;
	
	/*  TODO: override all methods of super using an ASM transformer
	 *  instead of keeping it updated from version to version
	 */

	private static String getPar2String(World world) {
		return world.getWorldInfo().getWorldName();
	}

	private static WorldSettings getPar4WorldSettings(World world) {
		return new WorldSettings(world.getWorldInfo());
	}
	
	public WorldServerProxy(WorldServer world) {
		// FIXME: this needs a custom constructor, as this one will break the game
		super(world.func_73046_m(), world.getSaveHandler(), getPar2String(world), world.provider.dimensionId, getPar4WorldSettings(world), world.theProfiler);
		this.proxiedWorld = world;
		//perWorldStorage = world.perWorldStorage; // final, set in super; requires reflection
		ReflectionHelper.setPrivateValue(World.class, this, world.perWorldStorage, new String[]{"perWorldStorage"}); // forge-added, no reobf
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
		//provider = world.provider; // handled by super
		findingSpawnPoint = world.findingSpawnPoint;
		mapStorage = world.mapStorage;
		villageCollectionObj = world.villageCollectionObj;
		//theProfiler = world.theProfiler; // handled by super
		isRemote = world.isRemote;
		theChunkProviderServer = world.theChunkProviderServer;
		levelSaving = world.levelSaving;
	    //allPlayersSleeping = world.allPlayersSleeping;
		customTeleporters = world.customTeleporters;
	}
}
