package cofh.core.util.helpers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * This class contains various helper functions related to Entities.
 *
 * @author King Lemming
 */
public class EntityHelper {

	private EntityHelper() {

	}

	public static int getEntityFacingCardinal(EntityLivingBase living) {

		int quadrant = cofh.core.util.helpers.MathHelper.floor(living.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		switch (quadrant) {
			case 0:
				return 2;
			case 1:
				return 5;
			case 2:
				return 3;
			default:
				return 4;
		}
	}

	public static EnumFacing getEntityFacingForgeDirection(EntityLivingBase living) {

		return EnumFacing.VALUES[getEntityFacingCardinal(living)];
	}

	public static void transferEntityToDimension(Entity entity, int dimension, PlayerList manager) {

		if (entity instanceof EntityPlayerMP) {
			transferPlayerToDimension((EntityPlayerMP) entity, dimension, manager);
			return;
		}
		WorldServer worldserver = manager.getServerInstance().getWorld(entity.dimension);
		entity.dimension = dimension;
		WorldServer worldserver1 = manager.getServerInstance().getWorld(entity.dimension);
		worldserver.removeEntityDangerously(entity);
		if (entity.isBeingRidden()) {
			entity.removePassengers();
		}
		if (entity.isRiding()) {
			entity.dismountRidingEntity();
		}
		entity.isDead = false;
		transferEntityToWorld(entity, worldserver, worldserver1);
	}

	public static void transferEntityToWorld(Entity entity, WorldServer oldWorld, WorldServer newWorld) {

		WorldProvider pOld = oldWorld.provider;
		WorldProvider pNew = newWorld.provider;
		double moveFactor = pOld.getMovementFactor() / pNew.getMovementFactor();
		double x = entity.posX * moveFactor;
		double z = entity.posZ * moveFactor;

		oldWorld.profiler.startSection("placing");
		x = MathHelper.clamp(x, -29999872, 29999872);
		z = MathHelper.clamp(z, -29999872, 29999872);

		if (entity.isEntityAlive()) {
			entity.setLocationAndAngles(x, entity.posY, z, entity.rotationYaw, entity.rotationPitch);
			newWorld.spawnEntity(entity);
			newWorld.updateEntityWithOptionalForce(entity, false);
		}
		oldWorld.profiler.endSection();
		entity.setWorld(newWorld);
	}

	public static void transferPlayerToDimension(EntityPlayerMP player, int dimension, PlayerList manager) {

		int oldDim = player.dimension;
		WorldServer worldserver = manager.getServerInstance().getWorld(player.dimension);
		player.dimension = dimension;
		WorldServer worldserver1 = manager.getServerInstance().getWorld(player.dimension);
		player.connection.sendPacket(new SPacketRespawn(player.dimension, player.world.getDifficulty(), player.world.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
		worldserver.removeEntityDangerously(player);
		if (player.isBeingRidden()) {
			player.removePassengers();
		}
		if (player.isRiding()) {
			player.dismountRidingEntity();
		}
		player.isDead = false;
		transferEntityToWorld(player, worldserver, worldserver1);
		manager.preparePlayer(player, worldserver);
		player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
		player.interactionManager.setWorld(worldserver1);
		manager.updateTimeAndWeatherForPlayer(player, worldserver1);
		manager.syncPlayerInventory(player);

		for (PotionEffect potioneffect : player.getActivePotionEffects()) {
			player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
		}
		FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, oldDim, dimension);
	}

	public static void transferEntityToDimension(Entity entity, double x, double y, double z, int dimension, PlayerList manager) {

		if (entity instanceof EntityPlayerMP) {
			transferPlayerToDimension((EntityPlayerMP) entity, dimension, manager);
			return;
		}
		WorldServer worldserver = manager.getServerInstance().getWorld(entity.dimension);
		entity.dimension = dimension;
		WorldServer worldserver1 = manager.getServerInstance().getWorld(entity.dimension);
		worldserver.removeEntityDangerously(entity);
		if (entity.isBeingRidden()) {
			entity.removePassengers();
		}
		if (entity.isRiding()) {
			entity.dismountRidingEntity();
		}
		entity.isDead = false;
		transferEntityToWorld(entity, x, y, z, worldserver, worldserver1);
	}

	public static void transferEntityToWorld(Entity entity, double x, double y, double z, WorldServer oldWorld, WorldServer newWorld) {

		oldWorld.profiler.startSection("placing");
		x = MathHelper.clamp(x, -29999872, 29999872);
		z = MathHelper.clamp(z, -29999872, 29999872);

		if (entity.isEntityAlive()) {
			entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
			newWorld.spawnEntity(entity);
			newWorld.updateEntityWithOptionalForce(entity, false);
		}
		oldWorld.profiler.endSection();
		entity.setWorld(newWorld);
	}

	public static void transferPlayerToDimension(EntityPlayerMP player, double x, double y, double z, int dimension, PlayerList manager) {

		int oldDim = player.dimension;
		WorldServer worldserver = manager.getServerInstance().getWorld(player.dimension);
		player.dimension = dimension;
		WorldServer worldserver1 = manager.getServerInstance().getWorld(player.dimension);
		player.connection.sendPacket(new SPacketRespawn(player.dimension, player.world.getDifficulty(), player.world.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
		worldserver.removeEntityDangerously(player);
		if (player.isBeingRidden()) {
			player.removePassengers();
		}
		if (player.isRiding()) {
			player.dismountRidingEntity();
		}
		player.isDead = false;
		transferEntityToWorld(player, worldserver, worldserver1);
		manager.preparePlayer(player, worldserver);
		player.connection.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
		player.interactionManager.setWorld(worldserver1);
		manager.updateTimeAndWeatherForPlayer(player, worldserver1);
		manager.syncPlayerInventory(player);

		for (PotionEffect potioneffect : player.getActivePotionEffects()) {
			player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
		}
		FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, oldDim, dimension);
	}

}
