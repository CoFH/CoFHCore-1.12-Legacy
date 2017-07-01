package cofh.core.util.helpers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Contains helper functions for the {@link NBTTagCompound} on an {@link ItemStack}
 *
 * @author Jotato
 */
public final class NBTHelper {

	private NBTHelper() {

	}

	public static NBTTagCompound getTagCompound(ItemStack stack) {

		if (stack.isEmpty()) {
			return null;
		}
		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		return stack.getTagCompound();
	}

	public static boolean keyExists(ItemStack stack, String key) {

		if (stack.isEmpty()) {
			return false;
		}
		return getTagCompound(stack).hasKey(key);
	}

	public static int getInt(ItemStack stack, String key, int defaultValue) {

		if (!keyExists(stack, key)) {
			return defaultValue;
		}
		return getTagCompound(stack).getInteger(key);
	}

	public static void setInt(ItemStack stack, String key, int value) {

		getTagCompound(stack).setInteger(key, value);
	}

	public static long getLong(ItemStack stack, String key, long defaultValue) {

		if (!keyExists(stack, key)) {
			return defaultValue;
		}
		return getTagCompound(stack).getLong(key);
	}

	public static void setLong(ItemStack stack, String key, Long value) {

		getTagCompound(stack).setLong(key, value);
	}

	public static boolean getBoolean(ItemStack stack, String key, boolean defaultValue) {

		if (!keyExists(stack, key)) {
			return defaultValue;
		}
		return getTagCompound(stack).getBoolean(key);
	}

	public static void setBoolean(ItemStack stack, String key, boolean value) {

		getTagCompound(stack).setBoolean(key, value);
	}

	public static byte getByte(ItemStack stack, String key, byte defaultValue) {

		if (!keyExists(stack, key)) {
			return defaultValue;
		}
		return getTagCompound(stack).getByte(key);
	}

	public static void setByte(ItemStack stack, String key, byte value) {

		getTagCompound(stack).setByte(key, value);
	}

	public static byte[] getByteArray(ItemStack stack, String key, byte[] defaultValue) {

		if (!keyExists(stack, key)) {
			return defaultValue;
		}
		return getTagCompound(stack).getByteArray(key);
	}

	public static void setByteArray(ItemStack stack, String key, byte[] value) {

		getTagCompound(stack).setByteArray(key, value);
	}

	public static double getDouble(ItemStack stack, String key, double defaultValue) {

		if (!keyExists(stack, key)) {
			return defaultValue;
		}
		return getTagCompound(stack).getDouble(key);
	}

	public static void setDouble(ItemStack stack, String key, double value) {

		getTagCompound(stack).setDouble(key, value);
	}

	public static float getFloat(ItemStack stack, String key, float defaultValue) {

		if (!keyExists(stack, key)) {
			return defaultValue;
		}
		return getTagCompound(stack).getFloat(key);
	}

	public static void setFloat(ItemStack stack, String key, float value) {

		getTagCompound(stack).setFloat(key, value);
	}

	public static int[] getIntArray(ItemStack stack, String key, int[] defaultValue) {

		if (!keyExists(stack, key)) {
			return defaultValue;
		}
		return getTagCompound(stack).getIntArray(key);
	}

	public static void setIntArray(ItemStack stack, String key, int[] value) {

		getTagCompound(stack).setIntArray(key, value);
	}

	public static short getShort(ItemStack stack, String key, short defaultValue) {

		if (!keyExists(stack, key)) {
			return defaultValue;
		}
		return getTagCompound(stack).getShort(key);
	}

	public static void setShort(ItemStack stack, String key, short value) {

		getTagCompound(stack).setShort(key, value);
	}

	public static String getString(ItemStack stack, String key, String defaultValue) {

		if (!keyExists(stack, key)) {
			return defaultValue;
		}
		return getTagCompound(stack).getString(key);
	}

	public static void setString(ItemStack stack, String key, String value) {

		getTagCompound(stack).setString(key, value);
	}

	public static NBTBase getTag(ItemStack stack, String key) {

		if (!keyExists(stack, key)) {
			return null;
		}
		return getTagCompound(stack).getTag(key);
	}

	public static void setTag(ItemStack stack, String key, NBTBase value) {

		getTagCompound(stack).setTag(key, value);
	}

	public static NBTTagCompound getCompoundTag(ItemStack stack, String key) {

		if (!keyExists(stack, key)) {
			return null;
		}
		return getTagCompound(stack).getCompoundTag(key);
	}

	public static void removeTag(ItemStack stack, String key) {

		getTagCompound(stack).removeTag(key);
	}

}
