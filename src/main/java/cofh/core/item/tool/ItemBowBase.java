package cofh.core.item.tool;

import cofh.api.core.IModelRegister;
import cofh.api.item.IEmpowerableItem;
import cofh.core.enchantment.CoFHEnchantment;
import cofh.core.render.CoFHFontRenderer;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemBowBase extends ItemBow implements IModelRegister {

	protected TMap<Integer, ToolEntry> itemMap = new THashMap<Integer, ToolEntry>();
	protected ArrayList<Integer> itemList = new ArrayList<Integer>(); // This is actually more memory efficient than a LinkedHashMap

	protected String name;
	protected String modName;
	protected boolean showInCreative = true;

	public ItemBowBase() {

		this("cofh");
	}

	public ItemBowBase(String modName) {

		this.modName = modName;
		setMaxStackSize(1);
		setHasSubtypes(true);

		//		addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
		//			@SideOnly (Side.CLIENT)
		//			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
		//
		//				if (entityIn == null) {
		//					return 0.0F;
		//				} else {
		//					ItemStack itemstack = entityIn.getActiveItemStack();
		//					return itemstack != null && itemstack.getItem() instanceof ItemBow ? (float) (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F : 0.0F;
		//				}
		//			}
		//		});
		//		addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter() {
		//			@SideOnly (Side.CLIENT)
		//			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
		//
		//				return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
		//			}
		//		});
	}

	public ItemBowBase setShowInCreative(boolean showInCreative) {

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

	protected float getDamageModifier(ItemStack stack) {

		int i = ItemHelper.getItemDamage(stack);
		if (!itemMap.containsKey(Integer.valueOf(i))) {
			return 0;
		}
		return itemMap.get(ItemHelper.getItemDamage(stack)).damageModifier;
	}

	protected float getSpeedModifier(ItemStack stack) {

		int i = ItemHelper.getItemDamage(stack);
		if (!itemMap.containsKey(Integer.valueOf(i))) {
			return 0;
		}
		return itemMap.get(ItemHelper.getItemDamage(stack)).speedModifier;
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
			ItemStack stack = new ItemStack(item, 1, itemList.get(i));
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setInteger("Damage", 0);

			list.add(stack);
		}
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {

		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setInteger("Damage", 0);
		}
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
	public boolean isDamaged(ItemStack stack) {

		return getDamage(stack) > 0;
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
	public int getDamage(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setInteger("Damage", 0);
		}
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

		return getToolMaterial(stack).getMaxUses() + 325;
	}

	@Override
	public int getMetadata(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setInteger("Damage", 0);
		}
		return stack.getTagCompound().getInteger("Damage");
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
		name = modName + "." + name;
		return super.setUnlocalizedName(name);
	}

	public Item setUnlocalizedName(String name, String registrationName) {

		GameRegistry.register(setRegistryName(registrationName));
		this.name = name;
		name = modName + "." + name;
		return super.setUnlocalizedName(name);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {

		boolean flag = this.findAmmo(player) != null;

		ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemStack, world, player, hand, flag);
		if (ret != null) {
			return ret;
		}

		if (!player.capabilities.isCreativeMode && !flag) {
			return !flag ? new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStack) : new ActionResult<ItemStack>(EnumActionResult.PASS, itemStack);
		} else {
			player.setActiveHand(hand);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
		}
	}

	//TODO Multishot enchant can use Arrow Loose Efent for better mod compatibility.
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase livingBase, int timeLeft) {

		if (livingBase instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) livingBase;
			boolean flag = entityplayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
			ItemStack itemstack = this.findAmmo(entityplayer);

			int i = this.getMaxItemUseDuration(stack) - timeLeft;
			i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, world, (EntityPlayer) livingBase, i, itemstack != null || flag);
			if (i < 0) {
				return;
			}

			if (itemstack != null || flag) {
				if (itemstack == null) {
					itemstack = new ItemStack(Items.ARROW);
				}

				float f = getArrowVelocity(i);

				if ((double) f >= 0.1D) {
					boolean flag1 = entityplayer.capabilities.isCreativeMode || (itemstack.getItem() instanceof ItemArrow ? ((ItemArrow) itemstack.getItem()).isInfinite(itemstack, stack, entityplayer) : false);
					boolean empowered = this instanceof IEmpowerableItem && ((IEmpowerableItem) this).isEmpowered(stack);

					if (!world.isRemote) {
						int enchantMultishot = EnchantmentHelper.getEnchantmentLevel(CoFHEnchantment.multishot, stack);
						int punchLvl = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
						int powerLvl = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
						boolean flame = EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0;
						stack.damageItem(1, entityplayer);
						onBowFired(entityplayer, stack);

						for (int shot = 0; shot <= enchantMultishot; shot++) {
							ItemArrow itemarrow = (ItemArrow) (itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW);
							EntityArrow entityarrow = itemarrow.createArrow(world, itemstack, entityplayer);
							entityarrow.setAim(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, f * 3.0F, 1.0F);
							if (empowered) {
								entityarrow.setDamage(entityarrow.getDamage() + 1.5);
							}

							if (f == 1.0F) {
								entityarrow.setIsCritical(true);
							}

							if (powerLvl > 0) {
								entityarrow.setDamage(entityarrow.getDamage() + (double) powerLvl * 0.5D + 0.5D);
							}

							if (punchLvl > 0) {
								entityarrow.setKnockbackStrength(punchLvl);
							}

							if (flame) {
								entityarrow.setFire(100);
							}

							if (flag1) {
								entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
							}

							world.spawnEntityInWorld(entityarrow);
						}
					}
					world.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

					if (!flag1) {
						--itemstack.stackSize;

						if (itemstack.stackSize == 0) {
							entityplayer.inventory.deleteStack(itemstack);
						}
					}

					entityplayer.addStat(StatList.getObjectUseStats(this));
				}
			}
		}
	}

	public void onBowFired(EntityPlayer player, ItemStack stack) {

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
		public float damageModifier;
		public float speedModifier;
		public EnumRarity rarity;

		ToolEntry(String name, Item.ToolMaterial material, String ingot, float damageModifier, float speedModifier, EnumRarity rarity) {

			this.name = name;
			this.material = material;
			this.ingot = ingot;
			this.damageModifier = damageModifier;
			this.speedModifier = speedModifier;
			this.rarity = rarity;
		}

		ToolEntry(String name, Item.ToolMaterial material, String ingot, float damageModifier, float speedModifier) {

			this(name, material, ingot, damageModifier, speedModifier, EnumRarity.COMMON);
		}

		ToolEntry(String name, Item.ToolMaterial material, String ingot) {

			this(name, material, ingot, 0, 0, EnumRarity.COMMON);
		}
	}

}
