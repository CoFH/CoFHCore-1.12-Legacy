package cofh.core.proxy;

import cofh.CoFHCore;
import cofh.api.item.IToolBow;
import cofh.api.item.IToolQuiver;
import cofh.core.enchantment.EnchantmentSmashing;
import cofh.core.enchantment.EnchantmentSmelting;
import cofh.core.enchantment.EnchantmentSoulbound;
import cofh.core.enchantment.EnchantmentVorpal;
import cofh.core.init.CoreEnchantments;
import cofh.core.init.CoreProps;
import cofh.core.item.tool.ItemShieldCore;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.NBTHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerList;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.ListIterator;

public class EventHandler {

	public static final EventHandler INSTANCE = new EventHandler();

	@SubscribeEvent (priority = EventPriority.HIGHEST)
	public void handleArrowLooseEvent(ArrowLooseEvent event) {

		if (!(event.getBow().getItem() == Items.BOW) && !(event.getBow().getItem() instanceof IToolBow)) {
			return;
		}
		ItemStack bowStack = event.getBow();
		IToolBow bow = null;
		EntityPlayer player = event.getEntityPlayer();
		ItemStack arrowStack = findAmmo(player);
		World world = event.getWorld();
		boolean customArrow = false;

		if (bowStack.getItem() instanceof IToolBow) {
			bow = (IToolBow) bowStack.getItem();
		}
		boolean flag = player.capabilities.isCreativeMode || (arrowStack.getItem() instanceof ItemArrow && ((ItemArrow) arrowStack.getItem()).isInfinite(arrowStack, bowStack, player));

		if (arrowStack.isEmpty() && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, bowStack) > 0) {
			flag = true;
		}
		if (!arrowStack.isEmpty() || flag) {
			if (arrowStack.isEmpty()) {
				arrowStack = new ItemStack(Items.ARROW);
			}
			float f = ItemBow.getArrowVelocity(event.getCharge());
			float speedMod = 1.0F;

			if (bow != null) {
				speedMod += bow.getArrowSpeedMultiplier(bowStack);
			}
			if ((double) f >= 0.1D) {
				if (!world.isRemote) {
					int encMultishot = MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.multishot, bowStack), 0, CoreEnchantments.multishot.getMaxLevel() + 1);
					int encPunch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, bowStack);
					int encPower = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, bowStack);
					int encFlame = EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, bowStack);

					if (bow != null) {
						bow.onBowFired(player, bowStack);
						if (isQuiver(arrowStack) && !((IToolQuiver) arrowStack.getItem()).allowCustomArrowOverride(arrowStack)) {
							customArrow = false;
						} else {
							customArrow = bow.hasCustomArrow(bowStack);
						}
					}
					for (int shot = 0; shot <= encMultishot; shot++) {
						EntityArrow arrow = createArrow(world, arrowStack, bowStack, customArrow, player);
						arrow.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 3.0F * speedMod, 1.0F + (1.5F - f) * (shot * 2));

						if (bow != null) {
							arrow.setDamage(arrow.getDamage() * (1 + bow.getArrowDamageMultiplier(bowStack)));
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
						if (encFlame > 0) {
							arrow.setFire(100);
						}
						if (flag || shot > 0) {
							arrow.pickupStatus = PickupStatus.CREATIVE_ONLY;
						}
						world.spawnEntity(arrow);
					}
					bowStack.damageItem(1, player);
				}
				world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

				if (!flag && !player.capabilities.isCreativeMode) {
					if (isQuiver(arrowStack)) {
						((IToolQuiver) arrowStack.getItem()).onArrowFired(arrowStack, player);
					} else {
						arrowStack.shrink(1);
						if (arrowStack.isEmpty()) {
							player.inventory.deleteStack(arrowStack);
						}
					}
				}
				player.addStat(StatList.getObjectUseStats(bowStack.getItem()));
			}
			event.setCanceled(true);
		}
	}

	@SubscribeEvent (priority = EventPriority.HIGHEST)
	public void handleArrowNockEvent(ArrowNockEvent event) {

		if (!(event.getBow().getItem() == Items.BOW) && !(event.getBow().getItem() instanceof IToolBow)) {
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
		DamageSource source = event.getSource();

		if (source instanceof EntityDamageSourceIndirect || source.isUnblockable() || source.isProjectile()) {
			return;
		}
		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		ItemStack stack = player.getActiveItemStack();

		if (!stack.isEmpty() && stack.getItem() instanceof ItemShieldCore) {
			((ItemShieldCore) stack.getItem()).onHit(stack, player, source.getTrueSource());
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
		PlayerList playerList = entity.world.getMinecraftServer().getPlayerList();
		ITextComponent deathMessage = event.getEntityLiving().getCombatTracker().getDeathMessage();

		if (playerList == null) {
			CoFHCore.LOG.error("Null Player List! That's not good.", event);
		}
		if (deathMessage == null) {
			CoFHCore.LOG.error("Null Death Message! Please report to the mod responsible.", event.getSource().getClass());
		}
		entity.world.getMinecraftServer().getPlayerList().sendMessage(event.getEntityLiving().getCombatTracker().getDeathMessage());
	}

	@SubscribeEvent
	public void handleLivingHurtEvent(LivingHurtEvent event) {

		Entity entity = event.getEntity();

		if (entity instanceof IProjectile) {
			return;
		}
		DamageSource source = event.getSource();
		Entity attacker = event.getSource().getTrueSource();

		if (!(attacker instanceof EntityLivingBase)) {
			return;
		}
		if (source.damageType.equals("arrow")) {
			int encMultishot = MathHelper.clamp(getHeldEnchantmentLevel((EntityLivingBase) attacker, CoreEnchantments.multishot), 0, CoreEnchantments.multishot.getMaxLevel() + 1);
			if (encMultishot > 0) {
				entity.hurtResistantTime = 0;
			}
		} else if (source.damageType.equals("player")) {
			int encVorpal = getHeldEnchantmentLevel((EntityLivingBase) attacker, CoreEnchantments.vorpal);
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

	@SubscribeEvent (priority = EventPriority.LOW)
	public void handleHarvestDropsEvent(BlockEvent.HarvestDropsEvent event) {

		EntityPlayer player = event.getHarvester();

		if (player == null || event.isSilkTouching() || event.isCanceled()) {
			return;
		}
		// ItemStack tool = ItemHelper.getMainhandStack(player);

		int encSmashing = EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.smashing, player.getHeldItemMainhand());
		List<ItemStack> drops = event.getDrops();
		if (encSmashing > 0) {
			for (int i = 0; i < drops.size(); i++) {
				ItemStack result = EnchantmentSmashing.getItemStack(drops.get(i));
				if (!result.isEmpty()) {
					drops.set(i, result.copy());
				}
			}
			//			if (!tool.isEmpty()) {
			//				tool.damageItem(1, player);
			//			}
		}

		int encSmelting = EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.smelting, player.getHeldItemMainhand());
		if (encSmelting > 0) {
			for (int i = 0; i < drops.size(); i++) {
				ItemStack result = EnchantmentSmelting.getItemStack(drops.get(i));
				if (!result.isEmpty()) {
					drops.set(i, result.copy());
				}
			}
			//			if (!tool.isEmpty()) {
			//				tool.damageItem(1, player);
			//			}
		}
	}

	@SubscribeEvent (priority = EventPriority.HIGHEST)
	public void handleLivingDropsEvent(LivingDropsEvent event) {

		Entity source = event.getSource().getTrueSource();

		if (!(source instanceof EntityPlayer) || !event.isRecentlyHit() || event.isCanceled()) {
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

	@SubscribeEvent (priority = EventPriority.HIGH)
	public void handlePlayerDropsEvent(PlayerDropsEvent event) {

		if (event.isCanceled()) {
			return;
		}
		EntityPlayer player = event.getEntityPlayer();

		if (player instanceof FakePlayer) {
			return;
		}
		if (player.world.getGameRules().getBoolean("keepInventory")) {
			return;
		}
		ListIterator<EntityItem> iter = event.getDrops().listIterator();
		while (iter.hasNext()) {
			EntityItem drop = iter.next();
			ItemStack stack = drop.getItem();
			if (isSoulbound(stack)) {
				if (addToPlayerInventory(player, stack)) {
					iter.remove();
				}
			}
		}
		// TODO: Other weirdness? Unsure.
	}

	@SubscribeEvent (priority = EventPriority.HIGH)
	public void handlePlayerCloneEvent(PlayerEvent.Clone event) {

		if (event.isCanceled() || !event.isWasDeath()) {
			return;
		}
		EntityPlayer player = event.getEntityPlayer();
		EntityPlayer oldPlayer = event.getOriginal();

		if (player instanceof FakePlayer) {
			return;
		}
		if (player.world.getGameRules().getBoolean("keepInventory")) {
			return;
		}
		for (int i = 0; i < oldPlayer.inventory.armorInventory.size(); i++) {
			ItemStack stack = oldPlayer.inventory.armorInventory.get(i);
			int encSoulbound = EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.soulbound, stack);
			if (encSoulbound > 0) {
				if (EnchantmentSoulbound.permanent) {
					if (encSoulbound > 1) {
						ItemHelper.removeEnchantment(stack, CoreEnchantments.soulbound);
						ItemHelper.addEnchantment(stack, CoreEnchantments.soulbound, 1);
					}
				} else if (MathHelper.RANDOM.nextInt(1 + encSoulbound) == 0) {
					ItemHelper.removeEnchantment(stack, CoreEnchantments.soulbound);
					if (encSoulbound > 1) {
						ItemHelper.addEnchantment(stack, CoreEnchantments.soulbound, encSoulbound - 1);
					}
				}
				if (addToPlayerInventory(player, stack)) {
					oldPlayer.inventory.armorInventory.set(i, ItemStack.EMPTY);
				}
			}
		}
		for (int i = 0; i < oldPlayer.inventory.mainInventory.size(); i++) {
			ItemStack stack = oldPlayer.inventory.mainInventory.get(i);
			int encSoulbound = MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.soulbound, stack), 0, CoreEnchantments.soulbound.getMaxLevel() + 1);
			if (encSoulbound > 0) {
				if (MathHelper.RANDOM.nextInt(1 + encSoulbound) == 0) {
					ItemHelper.removeEnchantment(stack, CoreEnchantments.soulbound);
					if (encSoulbound > 1) {
						ItemHelper.addEnchantment(stack, CoreEnchantments.soulbound, encSoulbound - 1);
					}
				}
				if (addToPlayerInventory(player, stack)) {
					oldPlayer.inventory.mainInventory.set(i, ItemStack.EMPTY);
				}
			}
		}
	}

	/* HELPERS */
	public boolean isArrow(ItemStack stack) {

		return stack.getItem() instanceof ItemArrow;
	}

	public boolean isQuiver(ItemStack stack) {

		return stack.getItem() instanceof IToolQuiver;
	}

	public boolean isSoulbound(ItemStack stack) {

		return getEnchantmentLevel(stack, CoreEnchantments.soulbound) > 0;
	}

	public static boolean addToPlayerInventory(EntityPlayer player, ItemStack stack) {

		if (stack.isEmpty() || player == null) {
			return false;
		}
		if (stack.getItem() instanceof ItemArmor) {
			ItemArmor arm = (ItemArmor) stack.getItem();
			int index = arm.armorType.getIndex();
			if (player.inventory.armorInventory.get(index).isEmpty()) {
				player.inventory.armorInventory.set(index, stack);
				return true;
			}
		}
		InventoryPlayer inv = player.inventory;
		for (int i = 0; i < inv.mainInventory.size(); i++) {
			if (inv.mainInventory.get(i).isEmpty()) {
				inv.mainInventory.set(i, stack.copy());
				return true;
			}
		}
		return false;
	}

	public EntityArrow createArrow(World world, ItemStack arrowStack, ItemStack bowStack, boolean customArrow, EntityPlayer player) {

		if (customArrow) {
			return ((IToolBow) bowStack.getItem()).createEntityArrow(world, bowStack, player);
		}
		if (isArrow(arrowStack)) {
			return ((ItemArrow) arrowStack.getItem()).createArrow(world, arrowStack, player);
		}
		if (isQuiver(arrowStack)) {
			return ((IToolQuiver) arrowStack.getItem()).createEntityArrow(world, arrowStack, player);
		}
		return ((ItemArrow) Items.ARROW).createArrow(world, arrowStack, player);
	}

	public ItemStack findAmmo(EntityPlayer player) {

		ItemStack offHand = player.getHeldItemOffhand();
		ItemStack mainHand = player.getHeldItemMainhand();

		if (isQuiver(offHand) && !((IToolQuiver) offHand.getItem()).isEmpty(offHand, player) || isArrow(offHand)) {
			return offHand;
		} else if (isQuiver(mainHand) && !((IToolQuiver) mainHand.getItem()).isEmpty(mainHand, player) || isArrow(mainHand)) {
			return mainHand;
		}
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);
			if (isQuiver(stack) && !((IToolQuiver) stack.getItem()).isEmpty(stack, player) || isArrow(stack)) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	public int getHeldEnchantmentLevel(EntityLivingBase player, Enchantment enc) {

		return Math.max(EnchantmentHelper.getEnchantmentLevel(enc, player.getHeldItemMainhand()), EnchantmentHelper.getEnchantmentLevel(enc, player.getHeldItemOffhand()));
	}

	public int getEnchantmentLevel(ItemStack item, Enchantment enc) {

		return EnchantmentHelper.getEnchantmentLevel(enc, item);
	}

}
