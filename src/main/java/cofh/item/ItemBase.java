package cofh.item;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cofh.render.IconRegistry;
import cofh.util.ItemHelper;
import cofh.util.StringHelper;
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

	public TMap<Integer, ItemEntry> itemMap = new THashMap<Integer, ItemEntry>();
	public ArrayList<Integer> itemList = new ArrayList<Integer>();

	public boolean hasTextures = true;
	public String modName = "cofh";

	public ItemBase() {

		setHasSubtypes(true);
	}

	public ItemBase(String modName) {

		this.modName = modName;
		setHasSubtypes(true);
	}

	public ItemBase setHasTextures(boolean hasTextures) {

		this.hasTextures = hasTextures;
		return this;
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

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		int i = ItemHelper.getItemDamage(stack);
		if (!itemMap.containsKey(Integer.valueOf(i))) {
			return "item.invalid";
		}
		return new StringBuilder().append(getUnlocalizedName()).append('.').append(itemMap.get(i).name).toString();
	}

	@Override
	public Item setUnlocalizedName(String name) {

		GameRegistry.registerItem(this, name);
		name = modName + "." + name;
		return super.setUnlocalizedName(name);
	}

	@Override
	public void registerIcons(IIconRegister ir) {

		if (!hasTextures) {
			return;
		}
		for (int i = 0; i < itemList.size(); i++) {
			ItemEntry item = itemMap.get(itemList.get(i));
			IconRegistry.addIcon(item.name,
					modName + ":" + getUnlocalizedName().replace("item." + modName + ".", "") + "/" + StringHelper.titleCase(item.name), ir);
		}
	}

}
