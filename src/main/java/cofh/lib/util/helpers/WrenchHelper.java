package cofh.lib.util.helpers;

import cofh.api.item.IToolHammer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

// TODO: Update for 1.8.9 APIs.
public final class WrenchHelper {

	private WrenchHelper() {

	}

	public static boolean isHoldingUsableWrench(EntityPlayer player, RayTraceResult traceResult) {

		EnumHand hand = EnumHand.MAIN_HAND;
		ItemStack stack = player.getHeldItem(hand);
		if (stack.isEmpty()) {
			hand = EnumHand.OFF_HAND;
			stack = player.getHeldItem(hand);
		}
		if (stack.isEmpty()) {
			return false;
		}
		if (stack.getItem() instanceof IToolHammer) {
			BlockPos pos = traceResult.getBlockPos();
			return ((IToolHammer) stack.getItem()).isUsable(stack, player, pos);
		} else if (bcWrenchExists) {
			//return canHandleBCWrench(player, hand, stack, traceResult);
		}
		return false;
	}

	public static void usedWrench(EntityPlayer player, RayTraceResult traceResult) {

		EnumHand hand = EnumHand.MAIN_HAND;
		ItemStack stack = player.getHeldItem(hand);
		if (stack.isEmpty()) {
			hand = EnumHand.OFF_HAND;
			stack = player.getHeldItem(hand);
		}
		if (stack.isEmpty()) {
			return;
		}
		if (stack.getItem() instanceof IToolHammer) {
			BlockPos pos = traceResult.getBlockPos();
			((IToolHammer) stack.getItem()).toolUsed(stack, player, pos);
		} else if (bcWrenchExists) {
			//bcWrenchUsed(player, hand, stack, traceResult);
		}
	}

	/* HELPERS */
	private static boolean bcWrenchExists = false;

	static {
		try {
			Class.forName("buildcraft.api.tools.IToolWrench");
			bcWrenchExists = true;
		} catch (Throwable t) {
			// pokemon!
		}
	}

	//	private static boolean canHandleBCWrench(Item item, EntityPlayer player, BlockPos pos) {
	//
	//		return item instanceof IToolWrench && ((IToolWrench) item).canWrench(player, pos);
	//	}
	//
	//	private static void bcWrenchUsed(Item item, EntityPlayer player, BlockPos pos) {
	//
	//		if (item instanceof IToolWrench) {
	//			((IToolWrench) item).wrenchUsed(player, pos);
	//		}
	//	}
}
