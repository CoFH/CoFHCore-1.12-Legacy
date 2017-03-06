package cofh.core.item;

import cofh.api.core.IModelRegister;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemMulti extends ItemCore implements IModelRegister {

	protected TMap<Integer, ItemEntry> itemMap = new THashMap<>();
	protected ArrayList<Integer> itemList = new ArrayList<>(); // This is actually more memory efficient than a LinkedHashMap

	public ItemMulti() {

		this("cofh");
	}

	public ItemMulti(String modName) {

		super(modName);
	}

	protected void addInformationDelegate(ItemStack stack, EntityPlayer player, List<String> list, boolean check) {

		int i = ItemHelper.getItemDamage(stack);
		if (!itemMap.containsKey(i)) {
			return;
		}
		ItemEntry item = itemMap.get(i);

		list.add(StringHelper.getInfoText("info." + modName + "." + name + "." + item.name));
	}

	/* ADD ITEMS */
	public ItemStack addItem(int number, ItemEntry entry) {

		if (itemMap.containsKey(number)) {
			return null;
		}
		itemMap.put(number, entry);
		itemList.add(number);

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
	@SideOnly (Side.CLIENT)
	public void getSubItems(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {

		for (int i = 0; i < itemList.size(); i++) {
			list.add(new ItemStack(item, 1, itemList.get(i)));
		}
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		int i = ItemHelper.getItemDamage(stack);
		if (!itemMap.containsKey(i)) {
			return EnumRarity.COMMON;
		}
		return itemMap.get(ItemHelper.getItemDamage(stack)).rarity;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		int i = ItemHelper.getItemDamage(stack);
		if (!itemMap.containsKey(i)) {
			return "item.invalid";
		}
		ItemEntry item = itemMap.get(i);
		return getUnlocalizedName() + "." + item.name;
	}

	@Override
	public Item setUnlocalizedName(String name) {

		GameRegistry.register(setRegistryName(name));
		this.name = name;
		name = modName + "." + name;
		return super.setUnlocalizedName(name);
	}

	public Item setUnlocalizedName(String name, String registrationName) {

		GameRegistry.register(setRegistryName(registrationName));
		this.name = name;
		name = modName + "." + name;
		return super.setUnlocalizedName(name);
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		for (Map.Entry<Integer, ItemEntry> entry : itemMap.entrySet()) {
			ModelLoader.setCustomModelResourceLocation(this, entry.getKey(), new ModelResourceLocation(getRegistryName(), "type=" + entry.getValue().name));
		}
	}

	/* ITEM ENTRY */
	public class ItemEntry {

		public String name;
		public EnumRarity rarity;

		ItemEntry(String name, EnumRarity rarity) {

			this.name = name;
			this.rarity = rarity;
		}

		ItemEntry(String name) {

			this(name, EnumRarity.COMMON);
		}
	}

}
