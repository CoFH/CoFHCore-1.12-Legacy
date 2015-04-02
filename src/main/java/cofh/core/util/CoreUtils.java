package cofh.core.util;

import cofh.CoFHCore;
import cofh.core.CoFHProps;
import cofh.core.entity.EntityLightningBoltFake;
import cofh.lib.util.position.BlockPosition;
import cpw.mods.fml.common.registry.GameData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

import org.apache.logging.log4j.core.helpers.Loader;

public class CoreUtils {

	public static int entityId = 0;

	public static int getEntityId() {

		entityId++;
		return entityId;
	}

	/* MOD UTILS */
	public static String getModName(Item item) {

		String s = GameData.getItemRegistry().getNameForObject(item);
		return s.substring(0, s.indexOf(':'));
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

		return CoFHCore.proxy.isOp(player.getCommandSenderName());
	}

	public static boolean isOp(String playerName) {

		return CoFHCore.proxy.isOp(playerName);
	}

	public static boolean isOpOrServer(String senderName) {

		return CoFHCore.proxy.isOp(senderName) || senderName.equals("Server");
	}

	/* SERVER UTILS */
	public static boolean isClient() {

		return CoFHCore.proxy.isClient();
	}

	public static boolean isServer() {

		return CoFHCore.proxy.isServer();
	}

	/* BLOCK UTILS */
	public static boolean isBlockUnbreakable(World world, int x, int y, int z) {

		Block b = world.getBlock(x, y, z);
		return b instanceof BlockLiquid || b.getBlockHardness(world, x, y, z) < 0;
	}

	public static boolean isRedstonePowered(World world, int x, int y, int z) {

		if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
			return true;
		}
		for (BlockPosition bp : new BlockPosition(x, y, z).getAdjacent(false)) {
			Block block = world.getBlock(bp.x, bp.y, bp.z);
			if (block.equals(Blocks.redstone_wire) && block.isProvidingStrongPower(world, bp.x, bp.y, bp.z, 1) > 0) {
				return true;
			}
		}
		return false;
	}

	public static boolean isRedstonePowered(TileEntity tile) {

		return isRedstonePowered(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
	}

	public static void dismantleLog(String playerName, Block block, int metadata, double x, double y, double z) {

		if (CoFHProps.enableDismantleLogging) {
			CoFHCore.log.info("Player " + playerName + " dismantled " + " (" + block + ":" + metadata + ") at (" + x + "," + y + "," + z + ")");
		}
	}

	/* FILE UTILS */
	public static void copyFileUsingStream(String source, File dest) throws IOException {

		InputStream is = null;
		OutputStream os = null;
		try {
			is = Loader.getResource(source, null).openStream();
			os = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			if (is != null) {
				is.close();
			}
			if (os != null) {
				os.close();
			}
		}
	}

	public static void copyFileUsingStream(String source, String dest) throws IOException {

		InputStream is = null;
		OutputStream os = null;
		try {
			is = Loader.getResource(source, null).openStream();
			os = new FileOutputStream(new File(dest));

			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			if (is != null) {
				is.close();
			}
			if (os != null) {
				os.close();
			}
		}
	}

	public static void copyFileUsingChannel(File source, File dest) throws IOException {

		FileChannel sourceChannel = null;
		FileChannel destChannel = null;
		try {
			sourceChannel = new FileInputStream(source).getChannel();
			destChannel = new FileOutputStream(dest).getChannel();
			destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
		} finally {
			// sourceChannel.close();
			// destChannel.close();
		}
	}

	/* SOUND UTILS */
	public static final String getSoundName(String modId, String soundpath) {

		soundpath = soundpath.replaceAll("/", ".");
		return String.format("%s:%s", modId, soundpath);
	}

	public static final float getSoundVolume(int category) {

		return CoFHCore.proxy.getSoundVolume(category);
	}

	/* ENTITY UTILS */
	public static boolean dropItemStackIntoWorld(ItemStack stack, World world, double x, double y, double z) {

		return dropItemStackIntoWorld(stack, world, x, y, z, false);
	}

	public static boolean dropItemStackIntoWorldWithVelocity(ItemStack stack, World world, double x, double y, double z) {

		return dropItemStackIntoWorld(stack, world, x, y, z, true);
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

	public static void doFakeExplosion(World world, double x, double y, double z, boolean playSound) {

		world.spawnParticle("largeexplode", x, y + 1, z, 0.0D, 0.0D, 0.0D);

		if (playSound) {
			world.playSound(x, y, z, "random.explode", 1.0F, 1.0F, true);
		}
	}

	public static void doFakeLightningBolt(World world, double x, double y, double z) {

		EntityLightningBoltFake bolt = new EntityLightningBoltFake(world, x, y, z);
		world.addWeatherEffect(bolt);
	}

	public static boolean teleportEntityTo(Entity entity, double x, double y, double z) {

		if (entity instanceof EntityLivingBase) {
			return teleportEntityTo((EntityLivingBase) entity, x, y, z);
		} else {
			entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
			entity.worldObj.playSoundAtEntity(entity, "mob.endermen.portal", 1.0F, 1.0F);
		}
		return true;
	}

	public static boolean teleportEntityTo(EntityLivingBase entity, double x, double y, double z) {

		EnderTeleportEvent event = new EnderTeleportEvent(entity, x, y, z, 0);
		if (MinecraftForge.EVENT_BUS.post(event)) {
			return false;
		}

		entity.setPositionAndUpdate(event.targetX, event.targetY, event.targetZ);
		entity.worldObj.playSoundAtEntity(entity, "mob.endermen.portal", 1.0F, 1.0F);

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
