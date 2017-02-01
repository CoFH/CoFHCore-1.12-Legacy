package cofh.core.util;

import cofh.CoFHCore;
import cofh.core.entity.EntityLightningBoltFake;
import cofh.core.init.CoreProps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import org.apache.logging.log4j.core.helpers.Loader;

import java.io.*;
import java.nio.channels.FileChannel;

public class CoreUtils {

	/* MOD UTILS */
	@Deprecated
	public static String getModName(Item item) {

		return item.getRegistryName().getResourcePath();
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

	/* BLOCK UTILS */
	public static boolean isBlockUnbreakable(World world, BlockPos pos) {

		IBlockState state = world.getBlockState(pos);
		return state.getBlock() instanceof BlockLiquid || state.getBlockHardness(world, pos) < 0;
	}

	public static boolean isRedstonePowered(World world, BlockPos pos) {

		if (world.isBlockIndirectlyGettingPowered(pos) > 0) {
			return true;
		}
		for (EnumFacing face : EnumFacing.VALUES) {
			BlockPos step = pos.offset(face);
			IBlockState state = world.getBlockState(step);
			if (state.equals(Blocks.REDSTONE_WIRE) && state.getWeakPower(world, step, EnumFacing.UP) > 0) {//TODO
				return true;
			}
		}
		return false;
	}

	public static boolean isRedstonePowered(TileEntity tile) {

		return isRedstonePowered(tile.getWorld(), tile.getPos());
	}

	public static void dismantleLog(String playerName, Block block, int metadata, BlockPos pos) {

		if (CoreProps.enableDismantleLogging) {
			CoFHCore.LOG.info("Player " + playerName + " dismantled " + " (" + block + ":" + metadata + ") at (" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + ")");
		}
	}

	/* FILE UTILS */
	public static void copyFileUsingStream(String source, String dest) throws IOException {

		copyFileUsingStream(source, new File(dest));
	}

	public static void copyFileUsingStream(String source, File dest) throws IOException {

		InputStream is = Loader.getResource(source, null).openStream();
		OutputStream os = new FileOutputStream(dest);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = is.read(buffer)) > 0) {
			os.write(buffer, 0, length);
		}
	}

	public static void copyFileUsingChannel(File source, File dest) throws IOException {

		FileInputStream sourceStream = new FileInputStream(source);
		FileChannel sourceChannel = sourceStream.getChannel();
		FileOutputStream outputStream = new FileOutputStream(dest);
		outputStream.getChannel().transferFrom(sourceChannel, 0, sourceChannel.size());
	}

	/* SOUND UTILS */
	public static String getSoundName(String modId, String soundpath) {

		soundpath = soundpath.replaceAll("/", ".");
		return String.format("%s:%s", modId, soundpath);
	}

	public static float getSoundVolume(int category) {

		return CoFHCore.proxy.getSoundVolume(category);
	}

	/* ENTITY UTILS */
	public static boolean dropItemStackIntoWorld(ItemStack stack, World world, Vec3d pos) {

		return dropItemStackIntoWorld(stack, world, pos, false);
	}

	public static boolean dropItemStackIntoWorldWithVelocity(ItemStack stack, World world, BlockPos pos) {

		return dropItemStackIntoWorld(stack, world, new Vec3d(pos), true);
	}

	public static boolean dropItemStackIntoWorldWithVelocity(ItemStack stack, World world, Vec3d pos) {

		return dropItemStackIntoWorld(stack, world, pos, true);
	}

	public static boolean dropItemStackIntoWorld(ItemStack stack, World world, Vec3d pos, boolean velocity) {

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
		EntityItem entity = new EntityItem(world, pos.xCoord + x2, pos.yCoord + y2, pos.zCoord + z2, stack.copy());

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

		world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, x, y + 1, z, 0.0D, 0.0D, 0.0D);

		if (playSound) {
			world.playSound(null, x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}

	public static void doFakeLightningBolt(World world, double x, double y, double z) {

		EntityLightningBoltFake bolt = new EntityLightningBoltFake(world, x, y, z);
		world.addWeatherEffect(bolt);
	}

	public static boolean teleportEntityTo(Entity entity, BlockPos pos) {

		return teleportEntityTo(entity, pos.getX(), pos.getY(), pos.getZ());
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
