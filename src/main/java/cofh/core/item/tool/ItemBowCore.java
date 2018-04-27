package cofh.core.item.tool;

import cofh.api.item.IToolBow;
import cofh.core.init.CoreEnchantments;
import cofh.core.item.IEnchantableItem;
import cofh.core.item.IFOVUpdateItem;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.MathHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class ItemBowCore extends ItemBow implements IEnchantableItem, IFOVUpdateItem, IToolBow {

	protected String repairIngot = "";
	protected ToolMaterial toolMaterial;

	protected float arrowDamageMultiplier = 0.0F;
	protected float arrowSpeedMultiplier = 0.0F;
	protected float zoomMultiplier = 0.15F;

	protected boolean showInCreative = true;

	public ItemBowCore(ToolMaterial toolMaterial) {

		this.toolMaterial = toolMaterial;
		setMaxStackSize(1);
		setMaxDamage(toolMaterial.getMaxUses() + 325);

		addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
			@SideOnly (Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {

				if (entityIn == null) {
					return 0.0F;
				} else {
					ItemStack itemstack = entityIn.getActiveItemStack();
					return !itemstack.isEmpty() && itemstack.getItem() instanceof ItemBowCore ? (float) (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F : 0.0F;
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

	public ItemBowCore setRepairIngot(String repairIngot) {

		this.repairIngot = repairIngot;
		return this;
	}

	public ItemBowCore setArrowDamage(float multiplier) {

		arrowDamageMultiplier = multiplier;
		return this;
	}

	public ItemBowCore setArrowSpeed(float multiplier) {

		this.arrowSpeedMultiplier = multiplier;
		return this;
	}

	public ItemBowCore setZoomMultiplier(float multiplier) {

		this.zoomMultiplier = multiplier;
		return this;
	}

	public ItemBowCore setShowInCreative(boolean showInCreative) {

		this.showInCreative = showInCreative;
		return this;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (isInCreativeTab(tab) && showInCreative) {
			items.add(new ItemStack(this, 1, 0));
		}
	}

	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

		return ItemHelper.isOreNameEqual(stack, repairIngot);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {

		return true;
	}

	@Override
	public int getItemEnchantability(ItemStack stack) {

		return toolMaterial.getEnchantability();
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		ItemStack stack = player.getHeldItem(hand);
		boolean flag = !this.findAmmo(player).isEmpty();

		ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(stack, world, player, hand, flag);
		if (ret != null) {
			return ret;
		}
		if (!player.capabilities.isCreativeMode && !flag) {
			return !flag ? new ActionResult<>(EnumActionResult.FAIL, stack) : new ActionResult<>(EnumActionResult.PASS, stack);
		} else {
			player.setActiveHand(hand);
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase livingBase, int timeLeft) {

		if (livingBase instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) livingBase;
			ItemStack arrowStack = this.findAmmo(player);
			boolean flag = player.capabilities.isCreativeMode || (arrowStack.getItem() instanceof ItemArrow && ((ItemArrow) arrowStack.getItem()).isInfinite(arrowStack, stack, player));

			int charge = this.getMaxItemUseDuration(stack) - timeLeft;
			charge = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, world, (EntityPlayer) livingBase, charge, !arrowStack.isEmpty() || flag);
			if (charge < 0) {
				return;
			}
			if (!arrowStack.isEmpty() || flag) {
				if (arrowStack.isEmpty()) {
					arrowStack = new ItemStack(Items.ARROW);
				}
				float f = getArrowVelocity(charge);
				float speedMod = 1.0F + arrowSpeedMultiplier;

				if ((double) f >= 0.1D) {
					if (!world.isRemote) {
						int encMultishot = MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.multishot, stack), 0, 10);
						int encPunch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
						int encPower = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
						boolean encFlame = EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0;
						onBowFired(player, stack);

						ItemArrow arrowItem = (ItemArrow) (arrowStack.getItem() instanceof ItemArrow ? arrowStack.getItem() : Items.ARROW);

						for (int shot = 0; shot <= encMultishot; shot++) {
							EntityArrow arrow = arrowItem.createArrow(world, arrowStack, player);
							arrow.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 3.0F * speedMod, 1.0F + (1.5F - f) * shot);
							arrow.setDamage(arrow.getDamage() * (1 + arrowDamageMultiplier));

							if (f >= 1.0F) {
								arrow.setIsCritical(true);
							}
							if (encPower > 0) {
								arrow.setDamage(arrow.getDamage() + (double) encPower * 0.5D + 0.5D);
							}
							if (encPunch > 0) {
								arrow.setKnockbackStrength(encPunch);
							}
							if (encFlame) {
								arrow.setFire(100);
							}
							if (flag || shot > 0) {
								arrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
							}
							world.spawnEntity(arrow);
						}
						stack.damageItem(1, player);
					}
					world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

					if (!flag) {
						arrowStack.shrink(1);

						if (arrowStack.getCount() == 0) {
							player.inventory.deleteStack(arrowStack);
						}
					}
					player.addStat(StatList.getObjectUseStats(this));
				}
			}
		}
	}

	/* IEnchantableItem */
	@Override
	public boolean canEnchant(ItemStack stack, Enchantment enchantment) {

		return enchantment == CoreEnchantments.multishot;
	}

	/* IFOVUpdateItem */
	@Override
	public float getFOVMod(ItemStack stack, EntityPlayer player) {

		float progress = MathHelper.clamp((stack.getMaxItemUseDuration() - player.getItemInUseCount()) / 20.0F, 0, 1.0F);
		return progress * progress * zoomMultiplier;
	}

	/* IToolBow */
	@Override
	public void onBowFired(EntityPlayer player, ItemStack item) {

	}

	@Override
	public float getArrowDamageMultiplier(ItemStack item) {

		return arrowDamageMultiplier;
	}

	@Override
	public float getArrowSpeedMultiplier(ItemStack item) {

		return arrowSpeedMultiplier;
	}

}
