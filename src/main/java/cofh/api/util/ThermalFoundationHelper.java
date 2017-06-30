package cofh.api.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLInterModComms;

/**
 * The purpose of this class is to show how to use and provide an interface for Thermal Foundation's IMC Lexicon Blacklist manipulation.
 *
 * This is really the only safe way to do this. Please do not attempt any direct Lexicon manipulation.
 *
 * @author King Lemming
 */
public class ThermalFoundationHelper {

	private ThermalFoundationHelper() {

	}

	/* Lexicon */
	public static void addBlacklistEntry(ItemStack entry) {

		if (entry.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag("entry", new NBTTagCompound());

		entry.writeToNBT(toSend.getCompoundTag("entry"));
		FMLInterModComms.sendMessage("thermalfoundation", "AddLexiconBlacklistEntry", toSend);
	}

	public static void removeBlacklistEntry(ItemStack entry) {

		if (entry.isEmpty()) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag("entry", new NBTTagCompound());

		entry.writeToNBT(toSend.getCompoundTag("entry"));
		FMLInterModComms.sendMessage("thermalfoundation", "RemoveLexiconBlacklistEntry", toSend);
	}

}
