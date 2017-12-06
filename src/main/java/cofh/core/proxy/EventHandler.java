package cofh.core.proxy;

import cofh.core.enchantment.EnchantmentVorpal;
import cofh.core.init.CoreEnchantments;
import cofh.core.init.CoreProps;
import cofh.core.item.tool.ItemShieldCore;
import cofh.core.util.core.IBowImproved;
import cofh.core.util.core.IQuiverItem;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.NBTHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class EventHandler {

	public static final EventHandler INSTANCE = new EventHandler();

	@SubscribeEvent (priority = EventPriority.HIGHEST)
	public void handleArrowLooseEvent(ArrowLooseEvent event) {

		if (!(event.getBow().getItem() == Items.BOW) && !(event.getBow().getItem() instanceof IBowImproved)) {
			return;
		}
		ItemStack stack = event.getBow();
		IBowImproved bowImproved = null;
		EntityPlayer player = event.getEntityPlayer();
		ItemStack arrowStack = findAmmo(player);
		World world = event.getWorld();

		if (stack.getItem() instanceof IBowImproved) {
			bowImproved = (IBowImproved) stack.getItem();
		}
		boolean flag = player.capabilities.isCreativeMode || (arrowStack.getItem() instanceof ItemArrow && ((ItemArrow) arrowStack.getItem()).isInfinite(arrowStack, stack, player));

		if (!arrowStack.isEmpty() || flag) {
			if (arrowStack.isEmpty()) {
				arrowStack = new ItemStack(Items.ARROW);
			}
			float f = ItemBow.getArrowVelocity(event.getCharge());
			float speedMod = bowImproved != null ? 1.0F + bowImproved.getArrowSpeedMultiplier(stack) : 1.0F;

			if ((double) f >= 0.1D) {
				if (!world.isRemote) {
					int encMultishot = MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.multishot, stack), 0, CoreEnchantments.multishot.getMaxLevel() + 1);
					int encPunch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
					int encPower = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
					boolean encFlame = EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0;

					if (bowImproved != null) {
						bowImproved.onBowFired(player, stack);
					}
					for (int shot = 0; shot <= encMultishot; shot++) {
						EntityArrow arrow = createArrow(world, arrowStack, player);
						arrow.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 3.0F * speedMod, 1.0F + (1.5F - f) * shot);

						if (bowImproved != null) {
							arrow.setDamage(arrow.getDamage() * (1 + bowImproved.getArrowDamageMultiplier(stack)));
						}
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
							arrow.pickupStatus = PickupStatus.CREATIVE_ONLY;
						}
						world.spawnEntity(arrow);
					}
					stack.damageItem(1, player);
				}
				world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

				if (!flag && !player.capabilities.isCreativeMode) {
					if (isQuiver(arrowStack)) {
						((IQuiverItem) arrowStack.getItem()).onArrowFired(arrowStack, player);
					} else {
						arrowStack.shrink(1);
						if (arrowStack.isEmpty()) {
							player.inventory.deleteStack(arrowStack);
						}
					}
				}
				player.addStat(StatList.getObjectUseStats(stack.getItem()));
			}
			event.setCanceled(true);
		}
	}

	@SubscribeEvent (priority = EventPriority.HIGHEST)
	public void handleArrowNockEvent(ArrowNockEvent event) {

		if (!(event.getBow().getItem() == Items.BOW) && !(event.getBow().getItem() instanceof IBowImproved)) {
			return;
		}
		ItemStack stack = event.getBow();
		EntityPlayer player = event.getEntityPlayer();
		ItemStack arrowStack = findAmmo(player);

		if (arrowStack.isEmpty() && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0) {
			arrowStack = new ItemStack(Items.ARROW);
		}
		if (!arrowStack.isEmpty()) {
			player.setActiveHand(event.getHand());
			event.setAction(new ActionResult<>(EnumActionResult.SUCCESS, stack));
		} else if (!player.capabilities.isCreativeMode) {
			event.setAction(new ActionResult<>(EnumActionResult.FAIL, stack));
		}
	}

	@SubscribeEvent (priority = EventPriority.HIGH)
	public void handleLivingAttackEvent(LivingAttackEvent event) {

		Entity entity = event.getEntity();

		if (!(entity instanceof EntityPlayer)) {
			return;
		}
		EntityPlayer player = (EntityPlayer) event.getEntityLiving();

		if (!player.getActiveItemStack().isEmpty()) {
			ItemStack stack = player.getActiveItemStack();
			float damage = event.getAmount();

			if (damage >= 3.0F && !stack.isEmpty() && ((stack.getItem() instanceof ItemShieldCore))) {
				((ItemShieldCore) stack.getItem()).damageShield(stack, 1 + MathHelper.floor(damage), player, event.getSource().getTrueSource());

				if (stack.getCount() <= 0) {
					EnumHand enumhand = player.getActiveHand();
					ForgeEventFactory.onPlayerDestroyItem(player, player.activeItemStack, enumhand);

					if (enumhand == EnumHand.MAIN_HAND) {
						player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
					} else {
						player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
					}
					player.activeItemStack = ItemStack.EMPTY;
					player.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + player.world.rand.nextFloat() * 0.4F);
				}
			}
		}
	}

	@SubscribeEvent
	public void handleLivingDeathEvent(LivingDeathEvent event) {

		Entity entity = event.getEntity();
		Entity attacker = event.getSource().getTrueSource();

		if (attacker instanceof EntityPlayer) {
			int encLeech = getHeldEnchantmentLevel((EntityPlayer) attacker, CoreEnchantments.leech);
			int encInsight = getHeldEnchantmentLevel((EntityPlayer) attacker, CoreEnchantments.insight);

			if (encLeech > 0) {
				((EntityPlayer) attacker).heal(encLeech);
			}
			if (encInsight > 0) {
				entity.world.spawnEntity(new EntityXPOrb(entity.world, entity.posX, entity.posY + 0.5D, entity.posZ, encInsight + entity.world.rand.nextInt(1 + encInsight * 3)));
			}
		}
		if (!CoreProps.enableLivingEntityDeathMessages || entity.world.isRemote || !(entity instanceof EntityLiving) || !event.getEntityLiving().hasCustomName()) {
			return;
		}
		entity.world.getMinecraftServer().getPlayerList().sendMessage(event.getEntityLiving().getCombatTracker().getDeathMessage());
	}

	@SubscribeEvent
	public void handleLivingHurtEvent(LivingHurtEvent event) {

		Entity entity = event.getEntity();
		Entity attacker = event.getSource().getTrueSource();

		if (attacker instanceof EntityPlayer) {
			int encVorpal = getHeldEnchantmentLevel((EntityPlayer) attacker, CoreEnchantments.vorpal);

			if (encVorpal > 0 && entity.world.rand.nextInt(100) < EnchantmentVorpal.CRIT_CHANCE * encVorpal) {
				event.setAmount(event.getAmount() * EnchantmentVorpal.CRIT_DAMAGE);
			}
		}
	}

	@SubscribeEvent
	public void handleBlockBreakEvent(BlockEvent.BreakEvent event) {

		EntityPlayer player = event.getPlayer();

		if (player == null) {
			return;
		}
		if (event.getExpToDrop() > 0) {
			int encInsight = MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.insight, player.getHeldItemMainhand()), 0, CoreEnchantments.insight.getMaxLevel() + 1);

			if (encInsight > 0) {
				event.setExpToDrop(event.getExpToDrop() + encInsight + player.world.rand.nextInt(1 + encInsight * 3));
			}
		}
	}

	@SubscribeEvent (priority = EventPriority.HIGHEST)
	public void handleHarvestDropsEvent(BlockEvent.HarvestDropsEvent event) {

		EntityPlayer player = event.getHarvester();

		if (player == null || event.isSilkTouching()) {
			return;
		}
		int encSmelting = MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.smelting, player.getHeldItemMainhand()), 0, CoreEnchantments.smelting.getMaxLevel());

		if (encSmelting > 0) {
			List<ItemStack> drops = event.getDrops();

			for (int i = 0; i < drops.size(); i++) {
				ItemStack result = FurnaceRecipes.instance().getSmeltingResult(drops.get(i));
				if (!result.isEmpty()) {
					drops.set(i, result.copy());
				}
			}
		}
	}

	@SubscribeEvent (priority = EventPriority.HIGHEST)
	public void handleLivingDropsEvent(LivingDropsEvent event) {

		Entity source = event.getSource().getTrueSource();

		if (!(source instanceof EntityPlayer) || !event.isRecentlyHit()) {
			return;
		}
		EntityPlayer player = (EntityPlayer) source;
		int encVorpal = MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.vorpal, player.getHeldItemMainhand()), 0, CoreEnchantments.vorpal.getMaxLevel() + 1);

		if (encVorpal > 0) {
			Entity entity = event.getEntity();
			ItemStack itemSkull = ItemStack.EMPTY;

			if (entity.world.rand.nextInt(100) < EnchantmentVorpal.HEAD_CHANCE * encVorpal) {
				if (entity instanceof EntityPlayerMP) {
					EntityPlayer target = (EntityPlayerMP) event.getEntity();
					itemSkull = new ItemStack(Items.SKULL, 1, 3);
					NBTHelper.setString(itemSkull, "SkullOwner", target.getName());
				} else if (entity instanceof EntitySkeleton) {
					itemSkull = new ItemStack(Items.SKULL, 1, 0);
				} else if (entity instanceof EntityWitherSkeleton) {
					itemSkull = new ItemStack(Items.SKULL, 1, 1);
				} else if (entity instanceof EntityZombie) {
					itemSkull = new ItemStack(Items.SKULL, 1, 2);
				} else if (entity instanceof EntityCreeper) {
					itemSkull = new ItemStack(Items.SKULL, 1, 4);
				}
			}
			if (itemSkull.isEmpty()) {
				return;
			}
			EntityItem drop = new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ, itemSkull);
			drop.setPickupDelay(10);
			event.getDrops().add(drop);
		}
	}

	/* HELPERS */
	public boolean isArrow(ItemStack stack) {

		return stack.getItem() instanceof ItemArrow;
	}

	public boolean isQuiver(ItemStack stack) {

		return stack.getItem() instanceof IQuiverItem;
	}

	public EntityArrow createArrow(World world, ItemStack stack, EntityPlayer player) {

		if (isArrow(stack)) {
			return ((ItemArrow) stack.getItem()).createArrow(world, stack, player);
		}
		if (isQuiver(stack)) {
			return ((IQuiverItem) stack.getItem()).createEntityArrow(world, stack, player);
		}
		return ((ItemArrow) Items.ARROW).createArrow(world, stack, player);
	}

	public ItemStack findAmmo(EntityPlayer player) {

		ItemStack offHand = player.getHeldItemOffhand();
		ItemStack mainHand = player.getHeldItemMainhand();

		if (isQuiver(offHand) && !((IQuiverItem) offHand.getItem()).isEmpty(offHand, player) || isArrow(offHand)) {
			return offHand;
		} else if (isQuiver(mainHand) && !((IQuiverItem) mainHand.getItem()).isEmpty(mainHand, player) || isArrow(mainHand)) {
			return mainHand;
		}
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);

			if (isQuiver(stack) && !((IQuiverItem) stack.getItem()).isEmpty(stack, player) || isArrow(stack)) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	public int getHeldEnchantmentLevel(EntityPlayer player, Enchantment enc) {

		return Math.max(EnchantmentHelper.getEnchantmentLevel(enc, player.getHeldItemMainhand()), EnchantmentHelper.getEnchantmentLevel(enc, player.getHeldItemOffhand()));
	}

}
