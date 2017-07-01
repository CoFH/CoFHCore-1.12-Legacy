package cofh.core.energy;

import cofh.core.util.ItemWrapper;
import gnu.trove.map.hash.THashMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Map.Entry;

public class FurnaceFuelHandler implements IFuelHandler {

	public static FurnaceFuelHandler instance = new FurnaceFuelHandler();
	private static THashMap<ItemWrapper, Integer> fuels = new THashMap<>();

	public static void initialize() {

		GameRegistry.registerFuelHandler(instance);
	}

	private FurnaceFuelHandler() {

	}

	@Override
	public int getBurnTime(ItemStack fuel) {

		if (fuel.isEmpty() || !fuels.containsKey(new ItemWrapper(fuel))) {
			return 0;
		}
		return fuels.get(new ItemWrapper(fuel));
	}

	public static boolean registerFuel(ItemStack fuel, int burnTime) {

		if (fuel.isEmpty() || burnTime <= 0 || fuels.containsKey(new ItemWrapper(fuel))) {
			return false;
		}
		fuels.put(new ItemWrapper(fuel), burnTime);
		return true;
	}

	public static void refresh() {

		THashMap<ItemWrapper, Integer> tempMap = new THashMap<>();

		for (Entry<ItemWrapper, Integer> entry : fuels.entrySet()) {
			ItemWrapper tempItem = new ItemWrapper(entry.getKey().item, entry.getKey().metadata);
			tempMap.put(tempItem, entry.getValue());
		}
		fuels.clear();
		fuels = tempMap;
	}

}
