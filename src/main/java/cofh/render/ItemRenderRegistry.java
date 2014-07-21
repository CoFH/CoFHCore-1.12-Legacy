package cofh.render;

import cofh.util.ItemWrapper;

import gnu.trove.map.hash.THashMap;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class ItemRenderRegistry {

	public static Map<ItemWrapper, IItemRenderer> itemRenders = new THashMap();

	public static boolean addItemRenderer(ItemStack stack, IItemRenderer renderer) {

		if (validItem(stack)) {
			return false;
		}
		itemRenders.put(ItemWrapper.fromItemStack(stack), renderer);
		return true;
	}

	public static IItemRenderer getItemRenderer(ItemStack stack) {

		return itemRenders.get(ItemWrapper.fromItemStack(stack));
	}

	public static boolean validItem(ItemStack stack) {

		return itemRenders.containsKey(ItemWrapper.fromItemStack(stack));
	}

	public static void refreshMap() {

		Map<ItemWrapper, IItemRenderer> tempMap = new THashMap(itemRenders.size());

		for (Entry<ItemWrapper, IItemRenderer> entry : itemRenders.entrySet()) {
			tempMap.put(entry.getKey(), entry.getValue());
		}
		itemRenders.clear();
		itemRenders = tempMap;
	}

}
