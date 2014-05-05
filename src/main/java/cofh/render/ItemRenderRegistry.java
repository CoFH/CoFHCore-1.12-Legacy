package cofh.render;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class ItemRenderRegistry {

	public static Map<Integer, IItemRenderer> itemRenders = new HashMap<Integer, IItemRenderer>();

	public static boolean addItemRenderer(ItemStack theItem, IItemRenderer theItemRenderer) {

		if (itemRenders.containsKey(getItemKey(theItem))) {
			return false;
		}
		itemRenders.put(getItemKey(theItem), theItemRenderer);
		return true;
	}

	public static IItemRenderer getItemRenderer(ItemStack theItem) {

		return itemRenders.get(getItemKey(theItem));
	}

	public static boolean validItem(ItemStack theItem) {

		return itemRenders.containsKey(getItemKey(theItem));
	}

	private static int getItemKey(ItemStack theItem) {

		return theItem.getItemDamage() | Item.getIdFromItem(theItem.getItem()) << 16;
	}

}
