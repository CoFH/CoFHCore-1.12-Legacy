package cofh.core.util;

import cofh.CoFHCore;
import cofh.core.CoFHProps;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class CoreUtils {

	public static int entityId = 0;

	public static int getEntityId() {

		entityId++;
		return entityId;
	}

	public static void dismantleLog(String playerName, BlockPos pos, IBlockState state) {

		if (CoFHProps.enableDismantleLogging) {
			CoFHCore.LOG.info("Player " + playerName + " dismantled " + " (" + state.getBlock() + ":" + state.getBlock().getMetaFromState(state) + ") at ("
					+ pos.getX() + "," + pos.getY() + "," + pos.getZ() + ")");
		}
	}

	/* PLAYER UTILS */
	public static EntityPlayer getClientPlayer() {

		return CoFHCore.proxy.getClientPlayer();
	}

	public static boolean isPlayer(EntityPlayer player) {

		return player instanceof EntityPlayerMP;
	}

	public static boolean isFakePlayer(EntityPlayer player) {

		return (player instanceof FakePlayer);
	}

	public static boolean isOp(EntityPlayer player) {

		return CoFHCore.proxy.isOp(player.getName());
	}

	public static boolean isOp(String playerName) {

		return CoFHCore.proxy.isOp(playerName);
	}

	/* SERVER UTILS */
	public static boolean isClient() {

		return CoFHCore.proxy.isClient();
	}

	public static boolean isServer() {

		return CoFHCore.proxy.isServer();
	}

	/* SOUND UTILS */
	public static final String getSoundName(String modId, String soundpath) {

		soundpath = soundpath.replaceAll("/", ".");
		return String.format("%s:%s", modId, soundpath);
	}

	/* ENTITY UTILS */
	public static boolean dropItemStackIntoWorld(ItemStack stack, World world, double x, double y, double z) {

		return dropItemStackIntoWorld(stack, world, x, y, z, false);
	}

	public static boolean dropItemStackIntoWorld(ItemStack stack, World world, BlockPos pos) {

		return dropItemStackIntoWorld(stack, world, pos.getX(), pos.getY(), pos.getZ(), false);
	}

	public static boolean dropItemStackIntoWorldWithVelocity(ItemStack stack, World world, double x, double y, double z) {

		return dropItemStackIntoWorld(stack, world, x, y, z, true);
	}

	public static boolean dropItemStackIntoWorldWithVelocity(ItemStack stack, World world, BlockPos pos) {

		return dropItemStackIntoWorld(stack, world, pos.getX(), pos.getY(), pos.getZ(), true);
	}

	public static boolean dropItemStackIntoWorld(ItemStack stack, World world, double x, double y, double z, boolean velocity) {

		if (stack == null) {
			return false;
		}
		float x2 = 0.5F;
		float y2 = 0.0F;
		float z2 = 0.5F;

		if (velocity) {
			x2 = world.rand.nextFloat() * 0.8F + 0.1F;
			y2 = world.rand.nextFloat() * 0.8F + 0.1F;
			z2 = world.rand.nextFloat() * 0.8F + 0.1F;
		}
		EntityItem entity = new EntityItem(world, x + x2, y + y2, z + z2, stack.copy());

		if (velocity) {
			entity.motionX = (float) world.rand.nextGaussian() * 0.05F;
			entity.motionY = (float) world.rand.nextGaussian() * 0.05F + 0.2F;
			entity.motionZ = (float) world.rand.nextGaussian() * 0.05F;
		} else {
			entity.motionY = -0.05F;
			entity.motionX = 0;
			entity.motionZ = 0;
		}
		world.spawnEntityInWorld(entity);

		return true;
	}

	public static boolean teleportEntityTo(Entity entity, double x, double y, double z) {

		if (entity instanceof EntityLivingBase) {
			return teleportEntityTo((EntityLivingBase) entity, x, y, z);
		} else {
			entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
			entity.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
		}
		return true;
	}

	public static boolean teleportEntityTo(EntityLivingBase entity, double x, double y, double z) {

		EnderTeleportEvent event = new EnderTeleportEvent(entity, x, y, z, 0);

		if (MinecraftForge.EVENT_BUS.post(event)) {
			return false;
		}
		entity.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
		entity.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);

		return true;
	}

	public static boolean teleportEntityTo(EntityLivingBase entity, double x, double y, double z, boolean cooldown) {

		if (cooldown) {
			NBTTagCompound tag = entity.getEntityData();
			long time = entity.worldObj.getTotalWorldTime();
			if (tag.getLong("cofh:tD") > time) {
				return false;
			}
			tag.setLong("cofh:tD", time + 35);
		}
		return teleportEntityTo(entity, x, y, z);
	}

}
