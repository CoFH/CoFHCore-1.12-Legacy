package cofh.render;

import cofh.util.ItemWrapper;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class ItemRenderRegistry {

	public static TMap<ItemWrapper, IItemRenderer> itemRenders = new THashMap<ItemWrapper, IItemRenderer>();

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

}
