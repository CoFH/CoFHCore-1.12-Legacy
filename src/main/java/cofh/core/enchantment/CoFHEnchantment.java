package cofh.core.enchantment;

import cofh.CoFHCore;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CoFHEnchantment {

	private CoFHEnchantment() {

	}

	public static void postInit() {

		GameRegistry.register(new EnchantmentHolding(), new ResourceLocation("cofh:holding"));
		GameRegistry.register(new EnchantmentMultishot(), new ResourceLocation("cofh:multishot"));
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
