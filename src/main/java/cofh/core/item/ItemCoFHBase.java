package cofh.core.item;

import cofh.api.core.IModelRegister;
import cofh.core.render.CoFHFontRenderer;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ItemCoFHBase extends Item implements IModelRegister {

	protected String name;
	protected String modName;

	protected TMap<Integer, ItemEntry> itemMap = new THashMap<Integer, ItemEntry>();
	protected ArrayList<Integer> itemList = new ArrayList<Integer>(); // This is actually more memory efficient than a LinkedHashMap

	public ItemCoFHBase() {

		this("cofh");
	}

	public ItemCoFHBase(String modName) {

		this.modName = modName;
		setHasSubtypes(true);
	}

	protected void addInformationDelegate(ItemStack stack, EntityPlayer player, List<String> list, boolean check) {

		int i = ItemHelper.getItemDamage(stack);
		if (!itemMap.containsKey(Integer.valueOf(i))) {
			return;
		}
		ItemEntry item = itemMap.get(i);

		list.add(StringHelper.getInfoText("info." + modName + "." + name + "." + item.name));
	}

	/* ADD ITEMS */
	public ItemStack addItem(int number, ItemEntry entry) {

		if (itemMap.containsKey(Integer.valueOf(number))) {
			return null;
		}
		itemMap.put(Integer.valueOf(number), entry);
		itemList.add(Integer.valueOf(number));

		ItemStack stack = new ItemStack(this, 1, number);
		return stack;
	}

	public ItemStack addItem(int number, String name, EnumRarity rarity) {

		return addItem(number, new ItemEntry(name, rarity));
	}

	public ItemStack addItem(int number, String name) {

		return addItem(number, new ItemEntry(name));
	}

	public ItemStack addOreDictItem(int number, String name, String oreName, EnumRarity rarity) {

		ItemStack stack = addItem(number, name, rarity);
		OreDictionary.registerOre(oreName, stack);

		return stack;
	}

	public ItemStack addOreDictItem(int number, String name, String oreName) {

		ItemStack stack = addItem(number, name);
		OreDictionary.registerOre(oreName, stack);

		return stack;
	}

	public ItemStack addOreDictItem(int number, String name, EnumRarity rarity) {

		return addOreDictItem(number, name, name, rarity);
	}

	public ItemStack addOreDictItem(int number, String name) {

		return addOreDictItem(number, name, name);
	}

	/* STANDARD METHODS */
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {

		for (int i = 0; i < itemList.size(); i++) {
			list.add(new ItemStack(item, 1, itemList.get(i)));
		}
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {

		return SecurityHelper.isSecure(stack);
	}

	@Override
	public boolean isItemTool(ItemStack stack) {

		return false;
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
	public String getUnlocalizedName(ItemStack stack) {

		int i = ItemHelper.getItemDamage(stack);
		if (!itemMap.containsKey(Integer.valueOf(i))) {
			return "item.invalid";
		}
		ItemEntry item = itemMap.get(i);
		return getUnlocalizedName() + "." + item.name;
	}

	@Override
	public Item setUnlocalizedName(String name) {

		GameRegistry.registerItem(this, name);
		this.name = name;
		name = modName + "." + name;
		return super.setUnlocalizedName(name);
	}

	public Item setUnlocalizedName(String name, String registrationName) {

		GameRegistry.registerItem(this, registrationName);
		this.name = name;
		name = modName + "." + name;
		return super.setUnlocalizedName(name);
	}

	public Item setUnlocalizedNamePass(String name) {

		return super.setUnlocalizedName(name);
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
    @SideOnly(Side.CLIENT)
    public FontRenderer getFontRenderer(ItemStack stack) {

        return CoFHFontRenderer.loadFontRendererStack(stack);
    }

	/* IModelRegister */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		for (Map.Entry<Integer, ItemEntry> entry : itemMap.entrySet()) {
			ModelLoader.setCustomModelResourceLocation(this, entry.getKey(), new ModelResourceLocation(getRegistryName(), entry.getValue().name));
		}
	}

	/* ITEM ENTRY */
	public class ItemEntry {

		public String name;
		public EnumRarity rarity;

		public ItemEntry(String name, EnumRarity rarity) {

			this.name = name;
			this.rarity = rarity;
		}

		public ItemEntry(String name) {

			this(name, EnumRarity.COMMON);
		}
	}

}
