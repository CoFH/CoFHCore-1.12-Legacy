package cofh.pcc.oredict;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class OreDictTracker {

	private static Map<ItemIdentifier, List<String>> _oreDictEntries = new HashMap<ItemIdentifier, List<String>>();
	private static Map<ItemIdentifier, List<Integer>> _oreDictIDs = new HashMap<ItemIdentifier, List<Integer>>();

	public static void registerOreDictEntry(ItemStack stack, String name) {

		ItemIdentifier ii = ItemIdentifier.fromItemStack(stack);
		if (_oreDictEntries.get(ii) == null) {
			_oreDictEntries.put(ii, new LinkedList<String>());
			_oreDictIDs.put(ii, new LinkedList<Integer>());
		}
		_oreDictEntries.get(ii).add(name);
		_oreDictIDs.get(ii).add(OreDictionary.getOreID(name));
	}

	public static List<String> getNamesFromItem(ItemStack stack) {

		return _oreDictEntries.get(ItemIdentifier.fromItemStack(stack));
	}

	public static List<Integer> getIDsFromItem(ItemStack stack) {

		return _oreDictIDs.get(ItemIdentifier.fromItemStack(stack));
	}

}
