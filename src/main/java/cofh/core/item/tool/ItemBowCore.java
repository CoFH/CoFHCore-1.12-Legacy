package cofh.core.item.tool;

import cofh.api.item.IEmpowerableItem;
import cofh.core.enchantment.CoFHEnchantment;
import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemBowCore extends ItemBow {

	protected String repairIngot = "";
	protected ToolMaterial toolMaterial;

	protected float arrowDamageMultiplier = 1.25F;
	protected float arrowSpeedMultiplier = 2.0F;

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

	public int cofh_canEnchantApply(ItemStack stack, Enchantment ench) {

		if (ench == Enchantments.LOOTING) {
			return 1;
		}
		if (ench.type == EnumEnchantmentType.BOW) {
			return 1;
		}
		return -1;
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

	public ItemBowCore setShowInCreative(boolean showInCreative) {

		this.showInCreative = showInCreative;
		return this;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void getSubItems(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {

		if (showInCreative) {
			list.add(new ItemStack(item, 1, 0));
		}
	}

	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

		return ItemHelper.isOreNameEqual(stack, repairIngot);
	}

	@Override
	public boolean isItemTool(ItemStack stack) {

		return true;
	}

	@Override
	public int getItemEnchantability() {

		return toolMaterial.getEnchantability();
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

		//        int draw = this.getMaxItemUseDuration(stack) - timeLeft;
		//
		//        ArrowLooseEvent event = new ArrowLooseEvent(livingBase, stack, draw);
		//        MinecraftForge.EVENT_BUS.post(event);
		//        if (event.isCanceled()) {
		//            return;
		//        }
		//        draw = event.charge;
		//
		//        boolean flag = livingBase.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;
		//
		//        if (flag || livingBase.inventory.hasItem(Items.arrow)) {
		//            float drawStrength = draw / 20.0F;
		//            drawStrength = (drawStrength * drawStrength + drawStrength * 2.0F) / 3.0F;
		//
		//            if (drawStrength > 1.0F) {
		//                drawStrength = 1.0F;
		//            } else if (drawStrength < 0.1F) {
		//                return;
		//            }
		//            int enchantPower = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
		//            int enchantKnockback = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
		//            int enchantFire = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack);
		//            int enchantMultishot = EnchantmentHelper.getEnchantmentLevel(CoFHEnchantment.multishot.effectId, stack);
		//
		//            EntityArrow arrow = new EntityArrow(world, livingBase, drawStrength * arrowSpeedMultiplier);
		//            double damage = arrow.getDamage() * arrowDamageMultiplier;
		//            arrow.setDamage(damage);
		//
		//            if (drawStrength == 1.0F) {
		//                arrow.setIsCritical(true);
		//            }
		//            if (enchantPower > 0) {
		//                arrow.setDamage(damage + enchantPower * 0.5D + 0.5D);
		//            }
		//            if (enchantKnockback > 0) {
		//                arrow.setKnockbackStrength(enchantKnockback);
		//            }
		//            if (enchantFire > 0) {
		//                arrow.setFire(100);
		//            }
		//            if (flag) {
		//                arrow.canBePickedUp = 2;
		//            } else {
		//                livingBase.inventory.consumeInventoryItem(Items.arrow);
		//            }
		//            world.playSoundAtEntity(livingBase, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + drawStrength * 0.5F);
		//
		//            if (ServerHelper.isServerWorld(world)) {
		//                world.spawnEntityInWorld(arrow);
		//            }
		//            for (int i = 0; i < enchantMultishot; i++) {
		//                arrow = new EntityArrow(world, livingBase, drawStrength * arrowSpeedMultiplier);
		//                arrow.setThrowableHeading(arrow.motionX, arrow.motionY, arrow.motionZ, 1.5f * drawStrength * arrowSpeedMultiplier, 3.0F);
		//
		//                arrow.setDamage(damage);
		//
		//                if (drawStrength == 1.0F) {
		//                    arrow.setIsCritical(true);
		//                }
		//                if (enchantPower > 0) {
		//                    arrow.setDamage(damage + enchantPower * 0.5D + 0.5D);
		//                }
		//                if (enchantKnockback > 0) {
		//                    arrow.setKnockbackStrength(enchantKnockback);
		//                }
		//                if (enchantFire > 0) {
		//                    arrow.setFire(100);
		//                }
		//                arrow.canBePickedUp = 2;
		//
		//                if (ServerHelper.isServerWorld(world)) {
		//                    world.spawnEntityInWorld(arrow);
		//                }
		//            }
		//            if (!livingBase.capabilities.isCreativeMode) {
		//                stack.damageItem(1, livingBase);
		//            }
		//        }
	}

	public void onBowFired(EntityPlayer player, ItemStack stack) {

	}
}
