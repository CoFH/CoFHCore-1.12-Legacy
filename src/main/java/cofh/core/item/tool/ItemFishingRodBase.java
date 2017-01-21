package cofh.core.item.tool;

import cofh.api.core.IModelRegister;
import cofh.core.entity.EntityCoFHFishHook;
import cofh.core.render.CoFHFontRenderer;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemFishingRodBase extends ItemFishingRod implements IModelRegister {

	protected TMap<Integer, ToolEntry> itemMap = new THashMap<Integer, ToolEntry>();
	protected ArrayList<Integer> itemList = new ArrayList<Integer>(); // This is actually more memory efficient than a LinkedHashMap

	protected String name;
	protected String modName;
	protected boolean showInCreative = true;

	public ItemFishingRodBase() {

		this("cofh");
	}

	public ItemFishingRodBase(String modName) {

		this.modName = modName;
		setMaxStackSize(1);
		setHasSubtypes(true);

		addPropertyOverride(new ResourceLocation("cast"), new IItemPropertyGetter() {
			@SideOnly (Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {

				return entityIn == null ? 0.0F : (entityIn.getHeldItemMainhand() == stack && entityIn instanceof EntityPlayer && ((EntityPlayer) entityIn).fishEntity != null ? 1.0F : 0.0F);
			}
		});
	}

	public ItemFishingRodBase setShowInCreative(boolean showInCreative) {

		this.showInCreative = showInCreative;
		return this;
	}

	protected void addInformationDelegate(ItemStack stack, EntityPlayer player, List<String> list, boolean check) {

		int i = ItemHelper.getItemDamage(stack);
		if (!itemMap.containsKey(Integer.valueOf(i))) {
			return;
		}
		ToolEntry item = itemMap.get(i);

		list.add(StringHelper.getInfoText("info." + modName + "." + name + "." + item.name));
	}

	protected int getLuckModifier() {

		return 0;
	}

	protected int getSpeedModifier() {

		return 0;
	}

	protected String getRepairIngot(ItemStack stack) {

		int i = ItemHelper.getItemDamage(stack);
		if (!itemMap.containsKey(Integer.valueOf(i))) {
			return "cobblestone";
		}
		return itemMap.get(ItemHelper.getItemDamage(stack)).ingot;
	}

	protected Item.ToolMaterial getToolMaterial(ItemStack stack) {

		int i = ItemHelper.getItemDamage(stack);
		if (!itemMap.containsKey(Integer.valueOf(i))) {
			return ToolMaterial.WOOD;
		}
		return itemMap.get(ItemHelper.getItemDamage(stack)).material;
	}

	/* ADD ITEMS */
	public ItemStack addItem(int number, ToolEntry entry) {

		if (itemMap.containsKey(Integer.valueOf(number))) {
			return null;
		}
		itemMap.put(Integer.valueOf(number), entry);
		itemList.add(Integer.valueOf(number));

		ItemStack stack = new ItemStack(this, 1, number);
		stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setInteger("Damage", 0);
		return stack;
	}

	public ItemStack addItem(int number, String name, Item.ToolMaterial material, String ingot, int luckModifier, int speedModifier, EnumRarity rarity) {

		return addItem(number, new ToolEntry(name, material, ingot, luckModifier, speedModifier, rarity));
	}

	public ItemStack addItem(int number, String name, Item.ToolMaterial material, String ingot, int luckModifier, int speedModifier) {

		return addItem(number, new ToolEntry(name, material, ingot, luckModifier, speedModifier));
	}

	public ItemStack addItem(int number, String name, Item.ToolMaterial material, String ingot) {

		return addItem(number, new ToolEntry(name, material, ingot));
	}

	/* STANDARD METHODS */
	@Override
	@SideOnly (Side.CLIENT)
	public void getSubItems(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {

		if (!showInCreative) {
			return;
		}
		for (int i = 0; i < itemList.size(); i++) {
			list.add(new ItemStack(item, 1, itemList.get(i)));
		}
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {

		if (damage < 0) {
			damage = 0;
		}
		stack.getTagCompound().setInteger("Damage", damage);
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
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

		return false;
	}

	@Override
	public int getDamage(ItemStack stack) {

		return stack.getTagCompound().getInteger("Damage");
	}

	@Override
	public int getItemEnchantability(ItemStack stack) {

		int i = ItemHelper.getItemDamage(stack);
		if (!itemMap.containsKey(Integer.valueOf(i))) {
			return 0;
		}
		return itemMap.get(ItemHelper.getItemDamage(stack)).material.getEnchantability();
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
	@SideOnly (Side.CLIENT)
	public FontRenderer getFontRenderer(ItemStack stack) {

		return CoFHFontRenderer.loadFontRendererStack(stack);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		int i = ItemHelper.getItemDamage(stack);
		if (!itemMap.containsKey(Integer.valueOf(i))) {
			return EnumRarity.COMMON;
		}
		return itemMap.get(ItemHelper.getItemDamage(stack)).rarity;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {

		if (player.fishEntity != null) {
			int i = player.fishEntity.handleHookRetraction();
			stack.damageItem(i, player);
			player.swingArm(hand);
		} else {
			world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if (ServerHelper.isServerWorld(world)) {
				world.spawnEntityInWorld(new EntityCoFHFishHook(world, player, getLuckModifier(), getSpeedModifier()));
			}
			player.swingArm(hand);
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
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

		for (Map.Entry<Integer, ToolEntry> entry : itemMap.entrySet()) {
			ModelLoader.setCustomModelResourceLocation(this, entry.getKey(), new ModelResourceLocation(getRegistryName(), "type=" + entry.getValue().name));
		}
	}

	/* ITEM ENTRY */
	public class ToolEntry {

		public String name;
		public Item.ToolMaterial material;
		public String ingot;
		public int luckModifier;
		public int speedModifier;
		public EnumRarity rarity;

		ToolEntry(String name, Item.ToolMaterial material, String ingot, int luckModifier, int speedModifier, EnumRarity rarity) {

			this.name = name;
			this.material = material;
			this.ingot = ingot;
			this.luckModifier = luckModifier;
			this.speedModifier = speedModifier;
			this.rarity = rarity;
		}

		ToolEntry(String name, Item.ToolMaterial material, String ingot, int luckModifier, int speedModifier) {

			this(name, material, ingot, luckModifier, speedModifier, EnumRarity.COMMON);
		}

		ToolEntry(String name, Item.ToolMaterial material, String ingot) {

			this(name, material, ingot, 0, 0, EnumRarity.COMMON);
		}
	}

}
