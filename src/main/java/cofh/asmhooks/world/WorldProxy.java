package cofh.asmhooks.world;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public abstract class WorldProxy extends World {

	protected World proxiedWorld;

	private static String getWorldName(World world) {

		return world.getWorldInfo().getWorldName();
	}

	private static WorldSettings getWorldSettings(World world) {

		return new WorldSettings(world.getWorldInfo());
	}

	public WorldProxy(World world) {

		super(world.getSaveHandler(), world.getWorldInfo(), world.provider, world.theProfiler, world.isRemote);
		this.proxiedWorld = world;

		ReflectionHelper.setPrivateValue(World.class, this, world.getPerWorldStorage(), "perWorldStorage"); // forge-added, no reobf
		ReflectionHelper.setPrivateValue(World.class, this, world.capturedBlockSnapshots, "capturedBlockSnapshots"); // forge-added, no reobf
		ReflectionHelper.setPrivateValue(World.class, this, world.loadedEntityList, "field_72996_f", "loadedEntityList");
		ReflectionHelper.setPrivateValue(World.class, this, world.loadedTileEntityList, "field_147482_g", "loadedTileEntityList");
		ReflectionHelper.setPrivateValue(World.class, this, world.playerEntities, "field_73010_i", "playerEntities");
		ReflectionHelper.setPrivateValue(World.class, this, world.weatherEffects, "field_73007_j", "weatherEffects");
		ReflectionHelper.setPrivateValue(World.class, this, world.rand, "field_75169_l", "rand");

		mapStorage = world.getMapStorage();
		cofh_updateProps();
	}

	protected void cofh_updateProps() {

		scheduledUpdatesAreImmediate = proxiedWorld.scheduledUpdatesAreImmediate;
		skylightSubtracted = proxiedWorld.skylightSubtracted;
		prevRainingStrength = proxiedWorld.prevRainingStrength;
		rainingStrength = proxiedWorld.rainingStrength;
		prevThunderingStrength = proxiedWorld.prevThunderingStrength;
		thunderingStrength = proxiedWorld.thunderingStrength;
		getWorldInfo().setDifficulty(proxiedWorld.getDifficulty());
		lastLightningBolt = proxiedWorld.lastLightningBolt;
		chunkProvider = proxiedWorld.getChunkProvider();
		captureBlockSnapshots = proxiedWorld.captureBlockSnapshots;
		restoringBlockSnapshots = proxiedWorld.restoringBlockSnapshots;
		villageCollectionObj = proxiedWorld.villageCollectionObj;
	}

	@Override
	public IChunkProvider createChunkProvider() {

		return null;
	}

	@Override
	public Entity getEntityByID(int id) {

		return null;
	}

}
