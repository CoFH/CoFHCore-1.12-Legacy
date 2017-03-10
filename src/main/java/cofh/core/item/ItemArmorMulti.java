package cofh.core.item;

import cofh.core.render.IModelRegister;
import cofh.core.render.FontRendererCore;
import cofh.lib.util.helpers.ItemHelper;
import cofh.core.util.helpers.SecurityHelper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemArmorMulti extends ItemArmor implements ISpecialArmor, IModelRegister {

	private static final UUID[] ARMOR_MODIFIERS = new UUID[] { UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150") };

	protected TMap<Integer, ArmorEntry> itemMap = new THashMap<>();
	protected ArrayList<Integer> itemList = new ArrayList<>(); // This is actually more memory efficient than a LinkedHashMap
	protected TMap<Integer, ModelResourceLocation> textureMap = new THashMap<>();

	protected String name;
	protected String modName;
	protected boolean showInCreative = true;

	public ItemArmorMulti(EntityEquipmentSlot type) {

		this("cofh", type);
	}

	public ItemArmorMulti(String modName, EntityEquipmentSlot type) {

		super(ArmorMaterial.IRON, 0, type);
		this.modName = modName;
		setMaxStackSize(1);
		setHasSubtypes(true);
	}

	public ItemArmorMulti setShowInCreative(boolean showInCreative) {

		this.showInCreative = showInCreative;
		return this;
	}

	public ItemStack setDefaultTag(ItemStack stack) {

		return setDefaultTag(stack, 0);
	}

	public ItemStack setDefaultTag(ItemStack stack, int type) {

		stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setInteger("Type", type);

		return stack;
	}

	protected int getArmorType(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			setDefaultTag(stack);
		}
		return stack.getTagCompound().getInteger("Type");
	}

	protected ArmorEntry getArmorEntry(ItemStack stack) {

		int type = getArmorType(stack);
		return itemMap.get(type);
	}

	protected ItemArmor.ArmorMaterial getArmorMaterial(ItemStack stack) {

		ArmorEntry entry = getArmorEntry(stack);
		return entry.material;
	}

	protected String getRepairIngot(ItemStack stack) {

		ArmorEntry entry = getArmorEntry(stack);
		return entry.ingot;
	}

	/* ADD ITEMS */
	public ItemStack addItem(int type, ArmorEntry entry) {

		if (itemMap.containsKey(type)) {
			return null;
		}
		itemMap.put(type, entry);
		itemList.add(type);

		ItemStack stack = setDefaultTag(new ItemStack(this), type);
		return stack;
	}

	public ItemStack addItem(int type, String name, ItemArmor.ArmorMaterial material, String[] textures, String ingot, EnumRarity rarity) {

		return addItem(type, new ArmorEntry(name, material, textures, ingot, rarity));
	}

	public ItemStack addItem(int type, String name, ItemArmor.ArmorMaterial material, String[] textures, String ingot) {

		return addItem(type, new ArmorEntry(name, material, textures, ingot));
	}

	/* STANDARD METHODS */
	@Override
	@SideOnly (Side.CLIENT)
	public void getSubItems(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {

		if (!showInCreative) {
			return;
		}
		for (int i = 0; i < itemList.size(); i++) {
			list.add(setDefaultTag(new ItemStack(this), itemList.get(i)));
		}
	}

	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

		return ItemHelper.isOreNameEqual(stack, getRepairIngot(stack));
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {

		return SecurityHelper.isSecure(stack);
	}

	@Override
	public boolean isItemTool(ItemStack stack) {

		return true;
	}

	@Override
	public int getItemEnchantability(ItemStack stack) {

		return getArmorMaterial(stack).getEnchantability();
	}

	@Override
	public int getMaxDamage(ItemStack stack) {

		return getArmorMaterial(stack).getDurability(armorType);
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

	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {

		Multimap<String, AttributeModifier> multimap = HashMultimap.create();

		if (slot == this.armorType) {
			multimap.put(SharedMonsterAttributes.ARMOR.getAttributeUnlocalizedName(), new AttributeModifier(ARMOR_MODIFIERS[slot.getIndex()], "Armor modifier", (double) getArmorMaterial(stack).getDamageReductionAmount(slot), 0));
			multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getAttributeUnlocalizedName(), new AttributeModifier(ARMOR_MODIFIERS[slot.getIndex()], "Armor toughness", (double) getArmorMaterial(stack).getToughness(), 0));
		}
		return multimap;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public FontRenderer getFontRenderer(ItemStack stack) {

		return FontRendererCore.loadFontRendererStack(stack);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		ArmorEntry entry = getArmorEntry(stack);
		return entry == null ? EnumRarity.COMMON : entry.rarity;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {

		ArmorEntry entry = getArmorEntry(stack);

		if (slot == EntityEquipmentSlot.LEGS) {
			return entry.textures[1];
		}
		return entry.textures[0];
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		ArmorEntry entry = getArmorEntry(stack);
		return entry == null ? "item.invalid" : getUnlocalizedName() + "." + entry.name;
	}

	@Override
	public Item setUnlocalizedName(String name) {

		GameRegistry.register(setRegistryName(name));
		this.name = name;
		name = modName + ".armor." + name;
		return super.setUnlocalizedName(name);
	}

	public Item setUnlocalizedName(String name, String registrationName) {

		GameRegistry.register(setRegistryName(registrationName));
		this.name = name;
		name = modName + ".armor." + name;
		return super.setUnlocalizedName(name);
	}

	/* ISpecialArmor */
	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {

		return source.isUnblockable() ? null : new ArmorProperties(0, damageReduceAmount / 25D, Integer.MAX_VALUE);
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {

		return getArmorMaterial(armor).getDamageReductionAmount(armorType);
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack armor, DamageSource source, int damage, int slot) {

		armor.damageItem(1, entity);
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		ModelLoader.setCustomMeshDefinition(this, new ArmorMeshDefinition());

		for (Map.Entry<Integer, ArmorEntry> entry : itemMap.entrySet()) {

			ModelResourceLocation texture = new ModelResourceLocation(modName + ":armor/" + name + "_" + entry.getValue().name, "inventory");

			textureMap.put(entry.getKey(), texture);
			ModelBakery.registerItemVariants(this, texture);
		}
	}

	/* ITEM MESH DEFINITION */
	@SideOnly (Side.CLIENT)
	public class ArmorMeshDefinition implements ItemMeshDefinition {

		public ModelResourceLocation getModelLocation(ItemStack stack) {

			return textureMap.get(getArmorType(stack));
		}
	}

	/* ITEM ENTRY */
	public class ArmorEntry {

		final String name;
		final ItemArmor.ArmorMaterial material;
		final String[] textures;
		final String ingot;
		final EnumRarity rarity;

		ArmorEntry(String name, ItemArmor.ArmorMaterial material, String[] textures, String ingot, EnumRarity rarity) {

			this.name = name;
			this.material = material;
			this.textures = textures;
			this.ingot = ingot;
			this.rarity = rarity;
		}

		ArmorEntry(String name, ItemArmor.ArmorMaterial material, String[] textures, String ingot) {

			this(name, material, textures, ingot, EnumRarity.COMMON);
		}
	}

}
