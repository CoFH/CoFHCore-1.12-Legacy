package cofh.core.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RayTracer {

	public static RayTraceResult retrace(EntityPlayer player) {

		return retrace(player, getBlockReachDistance(player), true);
	}

	public static RayTraceResult retrace(EntityPlayer player, double reach) {

		return retrace(player, reach, true);
	}

	public static RayTraceResult retrace(EntityPlayer player, boolean stopOnFluid) {

		return retrace(player, stopOnFluid, false, true);
	}

	public static RayTraceResult retrace(EntityPlayer player, double reach, boolean stopOnFluid) {

		return retrace(player, reach, stopOnFluid, false, true);
	}

	public static RayTraceResult retrace(EntityPlayer player, boolean stopOnFluid, boolean ignoreNoBoundingBox, boolean returnUncollidable) {

		Vec3d startVec = getStartVec(player);
		Vec3d endVec = getEndVec(player);
		return player.world.rayTraceBlocks(startVec, endVec, stopOnFluid, ignoreNoBoundingBox, returnUncollidable);
	}

	public static RayTraceResult retrace(EntityPlayer player, double reach, boolean stopOnFluid, boolean ignoreNoBoundingBox, boolean returnUncollidable) {

		Vec3d startVec = getStartVec(player);
		Vec3d endVec = getEndVec(player, reach);
		return player.world.rayTraceBlocks(startVec, endVec, stopOnFluid, ignoreNoBoundingBox, returnUncollidable);
	}

	public static RayTraceResult retraceBlock(World world, EntityPlayer player, BlockPos pos) {

		Vec3d startVec = getStartVec(player);
		Vec3d endVec = getEndVec(player);
		return world.getBlockState(pos).collisionRayTrace(world, pos, startVec, endVec);
	}

	public static Vec3d getStartVec(EntityPlayer player) {

		return getCorrectedHeadVec(player);
	}

	public static Vec3d getEndVec(EntityPlayer player) {

		Vec3d headVec = getCorrectedHeadVec(player);
		Vec3d lookVec = player.getLook(1.0F);
		double reach = getBlockReachDistance(player);
		return headVec.addVector(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
	}

	public static Vec3d getEndVec(EntityPlayer player, double reach) {

		Vec3d headVec = getCorrectedHeadVec(player);
		Vec3d lookVec = player.getLook(1.0F);
		return headVec.addVector(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
	}

	public static Vec3d getCorrectedHeadVec(EntityPlayer player) {

		return new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
	}

	public static double getBlockReachDistance(EntityPlayer player) {

		return player.world.isRemote ? getBlockReachDistanceClient() : player instanceof EntityPlayerMP ? getBlockReachDistanceServer((EntityPlayerMP) player) : 5D;
	}

	private static double getBlockReachDistanceServer(EntityPlayerMP player) {

		return player.interactionManager.getBlockReachDistance();
	}

	@SideOnly (Side.CLIENT)
	private static double getBlockReachDistanceClient() {

		return Minecraft.getMinecraft().playerController.getBlockReachDistance();
	}

}
