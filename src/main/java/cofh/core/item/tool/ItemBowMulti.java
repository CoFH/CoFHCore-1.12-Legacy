package cofh.core.item.tool;

import cofh.api.core.IModelRegister;
import cofh.core.init.CoreEnchantments;
import cofh.core.render.FontRendererCore;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.SecurityHelper;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
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
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemBowMulti extends ItemBow implements IModelRegister {

	protected TMap<Integer, ToolEntry> itemMap = new THashMap<>();
	protected ArrayList<Integer> itemList = new ArrayList<>(); // This is actually more memory efficient than a LinkedHashMap
	protected TMap<Integer, ModelResourceLocation> textureMap = new THashMap<>();

	protected String name;
	protected String modName;
	protected boolean showInCreative = true;

	public ItemBowMulti() {

		this("cofh");
	}

	public ItemBowMulti(String modName) {

		this.modName = modName;
		setMaxStackSize(1);
		setHasSubtypes(true);

		addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
			@SideOnly (Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {

				if (entityIn == null) {
					return 0.0F;
				} else {
					ItemStack itemstack = entityIn.getActiveItemStack();
					return itemstack != null && itemstack.getItem() instanceof ItemBow ? (float) (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F : 0.0F;
				}
			}
		});
		addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter() {
			@SideOnly (Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {

				return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
			}
		});
	}

	public ItemBowMulti setShowInCreative(boolean showInCreative) {

		this.showInCreative = showInCreative;
		return this;
	}

	protected float getDamageModifier(ItemStack stack) {

		ToolEntry entry = getToolEntry(stack);
		return entry.damageModifier;
	}

	protected float getSpeedModifier(ItemStack stack) {

		ToolEntry entry = getToolEntry(stack);
		return entry.speedModifier;
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

	public ItemStack addItem(int type, String name, Item.ToolMaterial material, String ingot, float damageModifier, float speedModifier, EnumRarity rarity) {

		return addItem(type, new ToolEntry(name, material, ingot, damageModifier, speedModifier, rarity));
	}

	public ItemStack addItem(int type, String name, Item.ToolMaterial material, String ingot, float damageModifier, float speedModifier) {

		return addItem(type, new ToolEntry(name, material, ingot, damageModifier, speedModifier));
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

		return getToolMaterial(stack).getMaxUses() + 325;
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
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {

		boolean flag = this.findAmmo(player) != null;
		ActionResult<ItemStack> ret = ForgeEventFactory.onArrowNock(itemStack, world, player, hand, flag);

		if (ret != null) {
			return ret;
		}
		if (!player.capabilities.isCreativeMode && !flag) {
			return !flag ? new ActionResult<>(EnumActionResult.FAIL, itemStack) : new ActionResult<>(EnumActionResult.PASS, itemStack);
		} else {
			player.setActiveHand(hand);
			return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
		}
	}

	//TODO Multishot enchant can use Arrow Loose Event for better mod compatibility.
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
				float speedMod = 1 + getSpeedModifier(stack);

				if ((double) f >= 0.1D) {
					if (!world.isRemote) {
						int enchantMultishot = EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.multishot, stack);
						int punchLvl = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
						int powerLvl = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
						boolean flame = EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0;
						onBowFired(entityplayer, stack);

						for (int shot = 0; shot <= enchantMultishot; shot++) {
							ItemArrow itemarrow = (ItemArrow) (itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW);
							EntityArrow arrow = itemarrow.createArrow(world, itemstack, entityplayer);
							arrow.setAim(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, f * 3.0F, 1.0F);
							arrow.setVelocity(arrow.motionX * speedMod, arrow.motionY * speedMod, arrow.motionZ * speedMod);
							arrow.setDamage(arrow.getDamage() + getDamageModifier(stack));

							if (f >= 1.0F) {
								arrow.setIsCritical(true);
							}
							if (powerLvl > 0) {
								arrow.setDamage(arrow.getDamage() + (double) powerLvl * 0.5D + 0.5D);
							}
							if (punchLvl > 0) {
								arrow.setKnockbackStrength(punchLvl);
							}
							if (flame) {
								arrow.setFire(100);
							}
							if (flag) {
								arrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
							}
							world.spawnEntityInWorld(arrow);
						}
						stack.damageItem(1, entityplayer);
					}
					world.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

					if (!flag) {
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
