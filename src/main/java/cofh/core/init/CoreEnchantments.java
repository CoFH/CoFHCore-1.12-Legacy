package cofh.core.init;

import cofh.api.item.IInventoryContainerItem;
import cofh.core.enchantment.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CoreEnchantments {

	public static final CoreEnchantments INSTANCE = new CoreEnchantments();

	private CoreEnchantments() {

	}

	/* INIT */
	public static void preInit() {

		holding = new EnchantmentHolding("cofhcore:holding");
		insight = new EnchantmentInsight("cofhcore:insight");
		leech = new EnchantmentLeech("cofhcore:leech");
		multishot = new EnchantmentMultishot("cofhcore:multishot");
		// vorpal = new EnchantmentVorpal("cofhcore:vorpal");

		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}

	/* EVENT HANDLING */
	@SubscribeEvent
	public void registerEnchantments(RegistryEvent.Register<Enchantment> event) {

		event.getRegistry().register(holding);
		event.getRegistry().register(insight);
		event.getRegistry().register(leech);
		event.getRegistry().register(multishot);
		// event.getRegistry().register(vorpal);
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
	public static Enchantment insight;
	public static Enchantment leech;
	public static Enchantment multishot;
	public static Enchantment vorpal;

	/* TYPES */
	public static final EnumEnchantmentType ENCHANTMENT_TYPE_STORAGE = EnumHelper.addEnchantmentType("COFH:STORAGE", item -> item instanceof IInventoryContainerItem);
	public static final EnumEnchantmentType ENCHANTMENT_TYPE_WEAPON_TOOL = EnumHelper.addEnchantmentType("COFH:WEAPON_TOOL", item -> item instanceof ItemSword || item instanceof ItemTool);

}
