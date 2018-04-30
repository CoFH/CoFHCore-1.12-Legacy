package cofh.core.init;

import cofh.CoFHCore;
import cofh.core.enchantment.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class CoreEnchantments {

	public static final CoreEnchantments INSTANCE = new CoreEnchantments();
	public static boolean disableAll = false;
	public static boolean registered = false;

	private CoreEnchantments() {

	}

	/* INIT */
	public static void preInit() {

		holding = new EnchantmentHolding("cofhcore:holding");
		insight = new EnchantmentInsight("cofhcore:insight");
		leech = new EnchantmentLeech("cofhcore:leech");
		multishot = new EnchantmentMultishot("cofhcore:multishot");
		smashing = new EnchantmentSmashing("cofhcore:smashing");
		smelting = new EnchantmentSmelting("cofhcore:smelting");
		soulbound = new EnchantmentSoulbound("cofhcore:soulbound");
		vorpal = new EnchantmentVorpal("cofhcore:vorpal");

		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}

	/* MUST BE CALLED IN PRE-INIT BY SOMETHING */
	public static void register() {

		if (disableAll || registered) {
			return;
		}
		ModContainer callingContainer = Loader.instance().activeModContainer();
		ModContainer cofhContainer = FMLCommonHandler.instance().findContainerFor(CoFHCore.MOD_ID);

		Loader.instance().setActiveModContainer(cofhContainer);
		MinecraftForge.EVENT_BUS.register(INSTANCE);
		registered = true;
		Loader.instance().setActiveModContainer(callingContainer);
	}

	public static boolean registered() {

		return registered;
	}

	/* EVENT HANDLING */
	@SubscribeEvent
	public void registerEnchantments(RegistryEvent.Register<Enchantment> event) {

		IForgeRegistry<Enchantment> registry = event.getRegistry();

		registry.register(holding);
		registry.register(insight);
		registry.register(leech);
		registry.register(multishot);
		registry.register(smashing);
		registry.register(smelting);
		registry.register(soulbound);
		registry.register(vorpal);
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
	public static Enchantment smashing;
	public static Enchantment smelting;
	public static Enchantment soulbound;
	public static Enchantment vorpal;

}
