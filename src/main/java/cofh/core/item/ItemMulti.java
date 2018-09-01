package cofh.core.item;

import cofh.api.core.ISecurable.AccessMode;
import cofh.core.init.CoreProps;
import cofh.core.render.IModelRegister;
import cofh.core.util.CoreUtils;
import cofh.core.util.RegistrySocial;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.SecurityHelper;
import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class ItemMulti extends ItemCore implements IModelRegister {

	protected Map<Integer, ItemEntry> itemMap = new Int2ObjectOpenHashMap<>();
	protected ArrayList<Integer> itemList = new ArrayList<>();

	public ItemMulti() {

		this("cofh");
		setMaxDamage(0);
		setHasSubtypes(true);
		setNoRepair();
	}

	public ItemMulti(String modName) {

		super(modName);
		setMaxDamage(0);
		setHasSubtypes(true);
		setNoRepair();
	}

	/* HELPERS */
	public static boolean canPlayerAccess(ItemStack stack, EntityPlayer player) {

		if (!SecurityHelper.isSecure(stack)) {
			return true;
		}
		String name = player.getName();
		AccessMode access = SecurityHelper.getAccess(stack);
		if (access.isPublic() || (CoreProps.enableOpSecureAccess && CoreUtils.isOp(name))) {
			return true;
		}
		GameProfile profile = SecurityHelper.getOwner(stack);
		UUID ownerID = profile.getId();
		if (SecurityHelper.isDefaultUUID(ownerID)) {
			return true;
		}
		UUID otherID = SecurityHelper.getID(player);
		return ownerID.equals(otherID) || access.isFriendsOnly() && RegistrySocial.playerHasAccess(name, profile);
	}

	public static boolean isCreative(ItemStack stack) {

		return ItemHelper.getItemDamage(stack) == CREATIVE;
	}

	/* ADD ITEMS */
	public ItemStack addItem(int number, ItemEntry entry) {

		if (itemMap.containsKey(number)) {
			return ItemStack.EMPTY;
		}
		itemMap.put(number, entry);
		itemList.add(number);

		return new ItemStack(this, 1, number);
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

	public ImmutableList<ItemStack> getAllItems() {

		ArrayList<ItemStack> items = new ArrayList<>();

		for (int metadata : itemList) {
			items.add(new ItemStack(this, 1, metadata));
		}
		return ImmutableList.copyOf(items);
	}

	/* STANDARD METHODS */
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (isInCreativeTab(tab)) {
			for (int metadata : itemList) {
				items.add(new ItemStack(this, 1, metadata));
			}
		}
	}

	@Override
	public boolean isDamageable() {

		return false;
	}

	@Override
	public boolean hasEffect(ItemStack stack) {

		return CoreProps.enableEnchantEffects && stack.isItemEnchanted();
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

	public static final int CREATIVE = 32000;

}
