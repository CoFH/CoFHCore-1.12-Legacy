package cofh.core.item.tool;

import cofh.core.render.CoFHFontRenderer;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.SecurityHelper;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.gui.FontRenderer;
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

public class ItemShieldMulti extends Item {

	protected TMap<Integer, ToolEntry> itemMap = new THashMap<Integer, ToolEntry>();
	protected ArrayList<Integer> itemList = new ArrayList<Integer>(); // This is actually more memory efficient than a LinkedHashMap
	protected TMap<Integer, ModelResourceLocation> textureMap = new THashMap<Integer, ModelResourceLocation>();

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

	protected int getStackDamage(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setInteger("Durability", 0);
		}
		return stack.getTagCompound().getInteger("Durability");
	}

	protected String getRepairIngot(ItemStack stack) {

		int i = ItemHelper.getItemDamage(stack);
		if (!itemMap.containsKey(Integer.valueOf(i))) {
			return "ingotIron";
		}
		return itemMap.get(ItemHelper.getItemDamage(stack)).ingot;
	}

	protected Item.ToolMaterial getToolMaterial(ItemStack stack) {

		int i = ItemHelper.getItemDamage(stack);
		if (!itemMap.containsKey(Integer.valueOf(i))) {
			return ToolMaterial.IRON;
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
		stack.getTagCompound().setInteger("Durability", 0);
		return stack;
	}

	public ItemStack addItem(int number, String name, Item.ToolMaterial material, String ingot, EnumRarity rarity) {

		return addItem(number, new ToolEntry(name, material, ingot, rarity));
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
			ItemStack stack = new ItemStack(item, 1, itemList.get(i));
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setInteger("Durability", 0);

			list.add(stack);
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
	public boolean isDamaged(ItemStack stack) {

		return getStackDamage(stack) > 0;
	}

	@Override
	public boolean isItemTool(ItemStack stack) {

		return true;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

		return !ItemHelper.itemsEqualWithMetadata(oldStack, newStack);
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {

		return getStackDamage(stack) > 0;
	}

	@Override
	public int getDamage(ItemStack stack) {

		return getStackDamage(stack);
	}

	@Override
	public int getMetadata(ItemStack stack) {

		return getStackDamage(stack);
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
	public String getUnlocalizedName(ItemStack stack) {

		int i = ItemHelper.getItemDamage(stack);
		if (!itemMap.containsKey(Integer.valueOf(i))) {
			return "item.invalid";
		}
		ToolEntry item = itemMap.get(i);
		return getUnlocalizedName() + "." + item.name;
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

	public EnumAction getItemUseAction(ItemStack stack) {

		return EnumAction.BLOCK;
	}

	/* ITEM ENTRY */
	public class ToolEntry {

		public String name;
		public Item.ToolMaterial material;
		public String ingot;
		public EnumRarity rarity;

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
