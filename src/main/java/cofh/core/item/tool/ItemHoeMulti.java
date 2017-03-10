package cofh.core.item.tool;

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
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemHoeMulti extends ItemHoe implements IModelRegister {

	protected TMap<Integer, ToolEntry> itemMap = new THashMap<>();
	protected ArrayList<Integer> itemList = new ArrayList<>(); // This is actually more memory efficient than a LinkedHashMap
	protected TMap<Integer, ModelResourceLocation> textureMap = new THashMap<>();

	protected String name;
	protected String modName;
	protected boolean showInCreative = true;

	public ItemHoeMulti() {

		this("cofh");
	}

	public ItemHoeMulti(String modName) {

		super(ToolMaterial.IRON);
		this.modName = modName;
		setMaxStackSize(1);
		setHasSubtypes(true);
	}

	public ItemHoeMulti setShowInCreative(boolean showInCreative) {

		this.showInCreative = showInCreative;
		return this;
	}

	/* COMMON METHODS */
	public ItemStack setDefaultTag(ItemStack stack) {

		return setDefaultTag(stack, 0);
	}

	public ItemStack setDefaultTag(ItemStack stack, int type) {

		stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setInteger("Type", type);

		return stack;
	}

	protected int getType(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			setDefaultTag(stack);
		}
		return stack.getTagCompound().getInteger("Type");
	}

	protected ToolEntry getToolEntry(ItemStack stack) {

		int type = getType(stack);
		return itemMap.get(type);
	}

	protected Item.ToolMaterial getToolMaterial(ItemStack stack) {

		ToolEntry entry = getToolEntry(stack);
		return entry.material;
	}

	protected String getRepairIngot(ItemStack stack) {

		ToolEntry entry = getToolEntry(stack);
		return entry.ingot;
	}

	/* ADD ITEMS */
	public ItemStack addItem(int type, ToolEntry entry) {

		if (itemMap.containsKey(type)) {
			return null;
		}
		itemMap.put(type, entry);
		itemList.add(type);

		ItemStack stack = setDefaultTag(new ItemStack(this), type);
		return stack;
	}

	public ItemStack addItem(int type, String name, Item.ToolMaterial material, String ingot, EnumRarity rarity) {

		return addItem(type, new ToolEntry(name, material, ingot, rarity));
	}

	public ItemStack addItem(int type, String name, Item.ToolMaterial material, String ingot) {

		return addItem(type, new ToolEntry(name, material, ingot));
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

		return getToolMaterial(stack).getEnchantability();
	}

	@Override
	public int getMaxDamage(ItemStack stack) {

		return getToolMaterial(stack).getMaxUses();
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
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {

		Multimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();

		return multimap;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public FontRenderer getFontRenderer(ItemStack stack) {

		return FontRendererCore.loadFontRendererStack(stack);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		ToolEntry entry = getToolEntry(stack);
		return entry == null ? EnumRarity.COMMON : entry.rarity;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		ToolEntry entry = getToolEntry(stack);
		return entry == null ? "item.invalid" : getUnlocalizedName() + "." + entry.name;
	}

	@Override
	public String getMaterialName() {

		return "";
	}

	@Override
	public Item setUnlocalizedName(String name) {

		GameRegistry.register(setRegistryName(name));
		this.name = name;
		name = modName + ".tool." + name;
		return super.setUnlocalizedName(name);
	}

	public Item setUnlocalizedName(String name, String registrationName) {

		GameRegistry.register(setRegistryName(registrationName));
		this.name = name;
		name = modName + ".tool." + name;
		return super.setUnlocalizedName(name);
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		ModelLoader.setCustomMeshDefinition(this, new ToolMeshDefinition());

		for (Map.Entry<Integer, ToolEntry> entry : itemMap.entrySet()) {

			ModelResourceLocation texture = new ModelResourceLocation(modName + ":tool/" + name + "_" + entry.getValue().name, "inventory");

			textureMap.put(entry.getKey(), texture);
			ModelBakery.registerItemVariants(this, texture);
		}
	}

	/* ITEM MESH DEFINITION */
	@SideOnly (Side.CLIENT)
	public class ToolMeshDefinition implements ItemMeshDefinition {

		public ModelResourceLocation getModelLocation(ItemStack stack) {

			return textureMap.get(getType(stack));
		}
	}

	/* ITEM ENTRY */
	public class ToolEntry {

		final String name;
		final Item.ToolMaterial material;
		final String ingot;
		final EnumRarity rarity;

		ToolEntry(String name, Item.ToolMaterial material, String ingot, EnumRarity rarity) {

			this.name = name;
			this.material = material;
			this.ingot = ingot;
			this.rarity = rarity;
		}

		ToolEntry(String name, Item.ToolMaterial material, String ingot) {

			this(name, material, ingot, EnumRarity.COMMON);
		}
	}

}
