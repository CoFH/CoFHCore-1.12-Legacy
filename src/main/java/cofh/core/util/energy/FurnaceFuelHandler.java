package cofh.core.util.energy;

import cofh.lib.util.ItemWrapper;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

import gnu.trove.map.hash.THashMap;

import java.util.Map.Entry;

import net.minecraft.item.ItemStack;

public class FurnaceFuelHandler implements IFuelHandler {

	public static FurnaceFuelHandler instance = new FurnaceFuelHandler();

	private static THashMap<ItemWrapper, Integer> fuels = new THashMap<ItemWrapper, Integer>();

	public static void initialize() {

	}

	private FurnaceFuelHandler() {

		if (instance != null) {
			throw new IllegalArgumentException();
		}
		GameRegistry.registerFuelHandler(this);
	}

	@Override
	public int getBurnTime(ItemStack fuel) {

		if (fuel == null || !fuels.containsKey(new ItemWrapper(fuel))) {
			return 0;
		}
		return fuels.get(new ItemWrapper(fuel));
	}

	public static boolean registerFuel(ItemStack fuel, int burnTime) {

		if (fuel == null || burnTime <= 0 || fuels.containsKey(new ItemWrapper(fuel))) {
			return false;
		}
		fuels.put(new ItemWrapper(fuel), burnTime);
		return true;
	}

	public static void refreshMap() {

		THashMap<ItemWrapper, Integer> tempMap = new THashMap<ItemWrapper, Integer>();

		for (Entry<ItemWrapper, Integer> entry : fuels.entrySet()) {
			ItemWrapper tempItem = new ItemWrapper(entry.getKey().item, entry.getKey().metadata);
			tempMap.put(tempItem, entry.getValue());
		}
		fuels.clear();
		fuels = tempMap;
	}

}
