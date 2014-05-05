package cofh.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cofh.render.IconRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemBase extends Item {

	public class ItemEntry {

		public String name;
		public int rarity = 0;
		public int maxDamage = 0;

		public ItemEntry(String name, int rarity, int maxDamage) {

			this.name = name;
			this.rarity = rarity;
			this.maxDamage = maxDamage;
		}

		public ItemEntry(String name, int rarity) {

			this.name = name;
			this.rarity = rarity;
		}

		public ItemEntry(String name) {

			this.name = name;
		}
	}

	public HashMap<Integer, ItemEntry> itemMap = new HashMap<Integer, ItemEntry>();
	public ArrayList<Integer> itemList = new ArrayList<Integer>();

	public ItemBase() {

		setHasSubtypes(true);
	}

	public ItemStack addItem(int number, String name, int rarity, boolean register) {

		if (itemMap.containsKey(Integer.valueOf(number))) {
			return null;
		}
		itemMap.put(Integer.valueOf(number), new ItemEntry(name, rarity));
		itemList.add(Integer.valueOf(number));

		ItemStack item = new ItemStack(this, 1, number);
		if (register) {
			GameRegistry.registerCustomItemStack(name, item);
		}
		return item;
	}

	public ItemStack addItem(int number, String name, int rarity) {

		return addItem(number, name, rarity, true);
	}

	public ItemStack addItem(int number, String name) {

		return addItem(number, name, 0);
	}

	@Override
	public IIcon getIconFromDamage(int i) {

		if (!itemMap.containsKey(Integer.valueOf(i))) {
			return null;
		}
		return IconRegistry.getIcon(itemMap.get(Integer.valueOf(i)).name);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		int i = stack.getItemDamage();
		if (!itemMap.containsKey(Integer.valueOf(i))) {
			return "item.invalid";
		}
		return new StringBuilder().append(getUnlocalizedName()).append('.').append(itemMap.get(i).name).toString();
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		int i = stack.getItemDamage();
		if (!itemMap.containsKey(Integer.valueOf(i))) {
			return EnumRarity.common;
		}
		return EnumRarity.values()[itemMap.get(stack.getItemDamage()).rarity];
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {

		for (int i = 0; i < itemList.size(); i++) {
			list.add(new ItemStack(item, 1, itemList.get(i)));
		}
	}

}
