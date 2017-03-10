package cofh.core.item.tool;

import cofh.core.render.IModelRegister;
import cofh.core.render.FontRendererCore;
import cofh.lib.util.helpers.ItemHelper;
import cofh.core.util.helpers.SecurityHelper;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemShieldMulti extends Item implements IModelRegister {

	protected TMap<Integer, ToolEntry> itemMap = new THashMap<>();
	protected ArrayList<Integer> itemList = new ArrayList<>(); // This is actually more memory efficient than a LinkedHashMap
	protected TMap<Integer, ModelResourceLocation> textureMap = new THashMap<>();

	protected String name;
	protected String modName;
	protected boolean showInCreative = true;

	public ItemShieldMulti() {

		this("cofh");
	}

	public ItemShieldMulti(String modName) {

		this.modName = modName;
		setMaxStackSize(1);
		setHasSubtypes(true);

		addPropertyOverride(new ResourceLocation("blocking"), new IItemPropertyGetter() {
			@SideOnly (Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {

				return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
			}
		});
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, ItemArmor.DISPENSER_BEHAVIOR);
	}

	public ItemShieldMulti setShowInCreative(boolean showInCreative) {

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
	public void setDamage(ItemStack stack, int damage) {

		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setInteger("Durability", 0);
		}
		if (damage < 0) {
			damage = 0;
		}
		stack.getTagCompound().setInteger("Durability", damage);
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

		return getToolMaterial(stack).getMaxUses() + 275;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {

		return 72000;
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

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {

		playerIn.setActiveHand(hand);
		return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {

		return EnumAction.BLOCK;
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		// ModelLoader.setCustomMeshDefinition(this, new ToolMeshDefinition());

		ModelResourceLocation texture = new ModelResourceLocation(modName + ":tool/" + name, "inventory");
		ModelBakery.registerItemVariants(this, texture);

		// TODO: Fix this once shield rendering is in.
		//		for (Map.Entry<Integer, ToolEntry> entry : itemMap.entrySet()) {
		//
		//			ModelResourceLocation texture = new ModelResourceLocation(modName + ":tool/" + name + "_" + entry.getValue().name, "inventory");
		//
		//			textureMap.put(entry.getKey(), texture);
		//			ModelBakery.registerItemVariants(this, texture);
		//		}
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
