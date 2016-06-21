package cofh.core.enchantment;

import cofh.CoFHCore;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class CoFHEnchantment {

	private CoFHEnchantment() {

	}

	public static void postInit() {

		int enchantId = CoFHCore.CONFIG_CORE.get("Enchantment", "Holding", 100);

		for (int i = enchantId; i < 256; i++) {
			try {
				holding = new EnchantmentHolding(i);
				break;
			} catch (IllegalArgumentException e) {

			}
		}
		CoFHCore.CONFIG_CORE.set("Enchantment", "Holding", holding.effectId);

		enchantId = CoFHCore.CONFIG_CORE.get("Enchantment", "Multishot", 101);

		for (int i = enchantId; i < 256; i++) {
			try {
				multishot = new EnchantmentMultishot(i);
				break;
			} catch (IllegalArgumentException e) {

			}
		}
		CoFHCore.CONFIG_CORE.set("Enchantment", "Multishot", multishot.effectId);
	}

	public static NBTTagList getEnchantmentTagList(NBTTagCompound nbt) {

		return nbt == null ? null : nbt.getTagList("ench", 10);
	}

	public static void addEnchantment(NBTTagCompound nbt, int id, int level) {

		if (nbt == null) {
			nbt = new NBTTagCompound();
		}
		NBTTagList list = getEnchantmentTagList(nbt);

		if (list == null) {
			list = new NBTTagList();
		}
		boolean found = false;
		for (int i = 0; i < list.tagCount() && !found; i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			if (tag.getShort("id") == id) {
				tag.setShort("id", (short) id);
				tag.setShort("lvl", (short) level);
				found = true;
			}
		}
		if (!found) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setShort("id", (short) id);
			tag.setShort("lvl", (short) level);
			list.appendTag(tag);
		}
		nbt.setTag("ench", list);
	}

	public static void addEnchantment(ItemStack stack, int id, int level) {

		addEnchantment(stack.getTagCompound(), id, level);
	}

	public static Enchantment holding;
	public static Enchantment multishot;

}
