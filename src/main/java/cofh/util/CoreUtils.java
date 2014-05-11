package cofh.util;

import cofh.CoFHCore;
import cofh.entity.EntityLightningBoltFake;
import cofh.util.position.BlockPosition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class CoreUtils {

	public static int entityId = 0;

	public static int getEntityId() {

		entityId++;
		return entityId;
	}

	/* PLAYER UTILS */
	public static boolean isPlayer(EntityPlayer player) {

		return player instanceof EntityPlayerMP;
	}

	public static boolean isFakePlayer(EntityPlayer player) {

		return !(player instanceof EntityPlayerMP);
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

	public static boolean isRedstonePowered(TileEntity te) {

		return isRedstonePowered(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
	}

	/* FILE UTILS */
	@SuppressWarnings("resource")
	public static void copyFileUsingChannel(File source, File dest) throws IOException {

		FileChannel sourceChannel = null;
		FileChannel destChannel = null;
		try {
			sourceChannel = new FileInputStream(source).getChannel();
			destChannel = new FileOutputStream(dest).getChannel();
			destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
		} finally {
			sourceChannel.close();
			destChannel.close();
		}
	}

	/* SOUND UTILS */
	public static final String getSoundName(String soundpath) {

		return getSoundName("cofh", soundpath, false);
	}

	public static final String getSoundName(String modId, String soundpath, boolean registering) {

		if (registering) {
			soundpath += ".ogg";
		} else {
			soundpath = soundpath.replaceAll("/", ".");
		}
		return String.format("%s:%s", modId, soundpath);
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

	public static boolean teleportEntityTo(EntityLivingBase entity, double x, double y, double z) {

		EnderTeleportEvent event = new EnderTeleportEvent(entity, x, y, z, 0);
		if (MinecraftForge.EVENT_BUS.post(event)) {
			return false;
		}
		double x2 = entity.posX;
		double y2 = entity.posY;
		double z2 = entity.posZ;

		entity.posX = event.targetX;
		entity.posY = event.targetY;
		entity.posZ = event.targetZ;

		entity.setPositionAndUpdate(event.targetX, event.targetY, event.targetZ);
		entity.worldObj.playSoundEffect(x2, y2, z2, "mob.endermen.portal", 1.0F, 1.0F);
		entity.playSound("mob.endermen.portal", 1.0F, 1.0F);

		return true;
	}

}
