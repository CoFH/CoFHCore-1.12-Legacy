package cofh.core.item;

import cofh.core.render.FontRendererCoFH;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.SecurityHelper;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemCoFHBase extends Item {

	protected TMap<Integer, ItemEntry> itemMap = new THashMap<>();
	protected ArrayList<Integer> itemList = new ArrayList<>(); // This is actually more memory efficient than a LinkedHashMap

	protected String modName = "cofh";
	protected String name;

	public ItemCoFHBase() {

		setHasSubtypes(true);
	}

	public ItemCoFHBase(String modName) {

		this();
		this.modName = modName;
	}

	public ItemStack addItem(int number, ItemEntry entry) {

		if (itemMap.containsKey(Integer.valueOf(number))) {
			return null;
		}
		itemMap.put(Integer.valueOf(number), entry);
		itemList.add(Integer.valueOf(number));

		return new ItemStack(this, 1, number);
	}

	public ItemStack addItem(int number, String name, EnumRarity rarity) {

		return addItem(number, new ItemEntry(name, rarity));
	}

	public ItemStack addItem(int number, String name) {

		return addItem(number, name, EnumRarity.COMMON);
	}

	public ItemStack addOreDictItem(int number, String name, EnumRarity rarity) {

		ItemStack stack = addItem(number, name, rarity);
		OreDictionary.registerOre(name, stack);

		return stack;
	}

	public ItemStack addOreDictItem(int number, String name) {

		ItemStack stack = addItem(number, name);
		OreDictionary.registerOre(name, stack);

		return stack;
	}

	public String getRawName(ItemStack stack) {

		int i = ItemHelper.getItemDamage(stack);
		if (!itemMap.containsKey(Integer.valueOf(i))) {
			return "invalid";
		}
		return itemMap.get(i).name;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		int i = stack.getItemDamage();
		if (!itemMap.containsKey(Integer.valueOf(i))) {
			return EnumRarity.COMMON;
		}
		return itemMap.get(stack.getItemDamage()).rarity;
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
		ItemEntry item = itemMap.get(i);

		if (item.altName) {
			return new StringBuilder().append(getUnlocalizedName()).append('.').append(item.name).append("Alt").toString();
		}
		return new StringBuilder().append(getUnlocalizedName()).append('.').append(item.name).toString();
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {

		return SecurityHelper.isSecure(stack);
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack stack) {

		if (SecurityHelper.isSecure(stack)) {
			location.invulnerable = true;
			location.isImmuneToFire = true;
			((EntityItem) location).lifespan = Integer.MAX_VALUE;
		}
		return null;
	}

	@Override
	public Item setUnlocalizedName(String name) {

		this.name = name;
		name = modName + "." + name;
		return super.setUnlocalizedName(name);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public FontRenderer getFontRenderer(ItemStack stack) {

		return FontRendererCoFH.loadFontRendererStack(stack);
	}

	public class ItemEntry {

		public String name;
		public EnumRarity rarity = EnumRarity.COMMON;
		public int maxDamage = 0;
		public boolean altName = false;

		public ItemEntry(String name, EnumRarity rarity, int maxDamage) {

			this.name = name;
			this.rarity = rarity;
			this.maxDamage = maxDamage;
		}

		public ItemEntry(String name, EnumRarity rarity) {

			this.name = name;
			this.rarity = rarity;
		}

		public ItemEntry(String name) {

			this.name = name;
		}

		public ItemEntry useAltName(boolean altName) {

			this.altName = altName;
			return this;
		}
	}
}
