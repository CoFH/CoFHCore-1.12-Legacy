package cofh.core.item;

import cofh.core.render.CoFHFontRenderer;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class ItemBase extends Item {

	public class ItemEntry {

		public String name;
		public IIcon icon;
		public int rarity = 0;
		public int maxDamage = 0;
		public boolean altName = false;

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

		public ItemEntry useAltName(boolean altName) {

			this.altName = altName;
			return this;
		}
	}

	public TMap<Integer, ItemEntry> itemMap = new THashMap<Integer, ItemEntry>();
	public ArrayList<Integer> itemList = new ArrayList<Integer>(); // This is actually more memory efficient than a LinkedHashMap

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

	public ItemStack addItem(int number, ItemEntry entry, boolean register) {

		if (itemMap.containsKey(Integer.valueOf(number))) {
			return null;
		}
		itemMap.put(Integer.valueOf(number), entry);
		itemList.add(Integer.valueOf(number));

		ItemStack item = new ItemStack(this, 1, number);
		if (register) {
			GameRegistry.registerCustomItemStack(entry.name, item);
		}
		return item;
	}

	public ItemStack addItem(int number, ItemEntry entry) {

		return addItem(number, entry, true);
	}

	public ItemStack addItem(int number, String name, int rarity, boolean register) {

		return addItem(number, new ItemEntry(name, rarity), register);
	}

	public ItemStack addItem(int number, String name, int rarity) {

		return addItem(number, name, rarity, true);
	}

	public ItemStack addItem(int number, String name) {

		return addItem(number, name, 0);
	}

	public ItemStack addOreDictItem(int number, String name, int rarity) {

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
	public IIcon getIconFromDamage(int i) {

		if (!itemMap.containsKey(i)) {
			return null;
		}
		return itemMap.get(i).icon;
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

		GameRegistry.registerItem(this, name);
		name = modName + "." + name;
		return super.setUnlocalizedName(name);
	}

	public Item setUnlocalizedName(String textureName, String registrationName) {

		GameRegistry.registerItem(this, registrationName);
		textureName = modName + "." + textureName;
		return super.setUnlocalizedName(textureName);
	}

	@Override
	public void registerIcons(IIconRegister ir) {

		if (!hasTextures) {
			return;
		}
		for (int i = 0; i < itemList.size(); i++) {
			ItemEntry item = itemMap.get(itemList.get(i));
			item.icon = registerIcon(ir, item);
		}
	}

	protected IIcon registerIcon(IIconRegister ir, ItemEntry item) {

		return ir.registerIcon(modName + ":" + getUnlocalizedName().replace("item." + modName + ".", "") + "/" + StringHelper.titleCase(item.name));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public FontRenderer getFontRenderer(ItemStack stack) {

		return CoFHFontRenderer.loadFontRendererStack(stack);
	}

}
