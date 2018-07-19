package cofh.core.util.helpers;

import cofh.core.block.TileReconfigurable;
import cofh.core.init.CoreProps;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ReconfigurableHelper {

	public static final byte DEFAULT_FACING = 3;
	public static final byte[] DEFAULT_SIDES = new byte[] { 0, 0, 0, 0, 0, 0 };

	private ReconfigurableHelper() {

	}

	/* NBT TAG HELPERS */
	public static NBTTagCompound setItemStackTagReconfig(NBTTagCompound tag, TileReconfigurable tile) {

		if (tile == null) {
			return null;
		}
		if (tag == null) {
			tag = new NBTTagCompound();
		}
		tag.setByte(CoreProps.FACING, (byte) tile.getFacing());
		tag.setByteArray(CoreProps.SIDE_CACHE, tile.sideCache);
		return tag;
	}

	public static byte getFacingFromNBT(NBTTagCompound tag) {

		return !tag.hasKey(CoreProps.FACING) ? DEFAULT_FACING : tag.getByte(CoreProps.FACING);
	}

	public static byte[] getSideCacheFromNBT(NBTTagCompound tag, byte[] defaultSides) {

		if (tag == null) {
			return defaultSides.clone();
		}
		byte[] retSides = tag.getByteArray(CoreProps.SIDE_CACHE);
		return retSides.length < 6 ? defaultSides.clone() : retSides;
	}

	/* ITEM HELPERS */
	public static boolean hasReconfigInfo(ItemStack stack) {

		return stack.getTagCompound() != null && (stack.getTagCompound().hasKey(CoreProps.FACING) && stack.getTagCompound().hasKey(CoreProps.SIDE_CACHE));
	}

	public static boolean setFacing(ItemStack stack, int facing) {

		if (facing < 0 || facing > 5) {
			return false;
		}
		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setByte(CoreProps.FACING, (byte) facing);
		return true;
	}

	public static boolean setSideCache(ItemStack stack, byte[] sideCache) {

		if (sideCache.length < 6) {
			return false;
		}
		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setByteArray(CoreProps.SIDE_CACHE, sideCache);
		return true;
	}

	public static byte getFacing(ItemStack stack) {

		return stack.getTagCompound() == null || !stack.getTagCompound().hasKey(CoreProps.FACING) ? DEFAULT_FACING : stack.getTagCompound().getByte(CoreProps.FACING);
	}

	public static byte[] getSideCache(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			return DEFAULT_SIDES.clone();
		}
		byte[] retSides = stack.getTagCompound().getByteArray(CoreProps.SIDE_CACHE);
		return retSides.length < 6 ? DEFAULT_SIDES.clone() : retSides;
	}

	public static byte[] getSideCache(ItemStack stack, byte[] defaultSides) {

		if (stack.getTagCompound() == null) {
			return defaultSides.clone();
		}
		byte[] retSides = stack.getTagCompound().getByteArray(CoreProps.SIDE_CACHE);
		return retSides.length < 6 ? defaultSides.clone() : retSides;
	}

}
