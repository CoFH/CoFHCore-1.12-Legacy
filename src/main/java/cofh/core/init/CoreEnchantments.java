package cofh.core.init;

import cofh.core.enchantment.EnchantmentHolding;
import cofh.core.enchantment.EnchantmentMultishot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CoreEnchantments {

	private CoreEnchantments() {

	}

	/* INIT */
	public static void preInit() {

		holding = new EnchantmentHolding("cofhcore:holding");
		multishot = new EnchantmentMultishot("cofhcore:multishot");
		// vorpal = new EnchantmentVorpal("cofhcore:vorpal");

		GameRegistry.register(holding);
		GameRegistry.register(multishot);
	}

	/* HELPERS */
	public static void addEnchantment(ItemStack stack, Enchantment ench, int level) {

		addEnchantment(stack.getTagCompound(), Enchantment.getEnchantmentID(ench), level);
	}

	public static void addEnchantment(NBTTagCompound nbt, Enchantment ench, int level) {

		addEnchantment(nbt, Enchantment.getEnchantmentID(ench), level);
	}

	public static void addEnchantment(ItemStack stack, int id, int level) {

		addEnchantment(stack.getTagCompound(), id, level);
	}

	public static void addEnchantment(NBTTagCompound nbt, int id, int level) {

		if (nbt == null) {
			nbt = new NBTTagCompound();
		}
		NBTTagList list = getEnchantmentTagList(nbt);

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

	private static NBTTagList getEnchantmentTagList(NBTTagCompound nbt) {

		return nbt.getTagList("ench", 10);
	}

	/* REFERENCES */
	public static Enchantment holding;
	public static Enchantment multishot;
	public static Enchantment vorpal;

	/* TYPES */
	public static final EnumEnchantmentType ENCHANTMENT_TYPE_STORAGE = EnumHelper.addEnchantmentType("COFH:STORAGE");

}
