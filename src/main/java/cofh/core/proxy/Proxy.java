package cofh.core.proxy;

import cofh.core.init.CoreEnchantments;
import cofh.core.init.CoreProps;
import cofh.core.item.tool.ItemShieldCore;
import cofh.core.key.KeyBindingItemMultiMode;
import cofh.core.key.KeyHandlerCore;
import cofh.core.util.core.IBowImproved;
import cofh.core.util.core.IQuiverItem;
import cofh.core.util.helpers.MathHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedList;
import java.util.List;

public class Proxy {

	/* INIT */
	public void preInit(FMLPreInitializationEvent event) {

	}

	public void initialize(FMLInitializationEvent event) {

		registerKeyBinds();
	}

	public void postInit(FMLPostInitializationEvent event) {

	}

	/* REGISTRATION */
	public void registerKeyBinds() {

		KeyHandlerCore.addServerKeyBind(KeyBindingItemMultiMode.INSTANCE);
		// KeyHandlerCore.addServerKeyBind(KeyBindingPlayerAugments.instance);
	}

	/* HELPERS */
	public int getKeyBind(String key) {

		return 0;
	}

	public void addIndexedChatMessage(ITextComponent chat, int index) {

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

	public boolean isArrow(ItemStack stack) {

		return !isQuiver(stack) && stack.getItem() instanceof ItemArrow;
	}

	public boolean isQuiver(ItemStack stack) {

		return stack.getItem() instanceof IQuiverItem;
	}

	public EntityArrow createArrow(World world, ItemStack stack, EntityPlayer player) {

		if (isArrow(stack)) {
			return ((ItemArrow) stack.getItem()).createArrow(world, stack, player);
		}
		if (isQuiver(stack)) {
			return ((IQuiverItem) stack.getItem()).createArrow(world, stack, player);
		}
		return ((ItemArrow) Items.ARROW).createArrow(world, stack, player);
	}

	/* EVENT HANDLING */
	@SubscribeEvent (priority = EventPriority.HIGHEST)
	public void handleArrowLooseEvent(ArrowLooseEvent event) {

		if (!(event.getBow().getItem() instanceof ItemBow)) {
			return;
		}
		ItemStack stack = event.getBow();
		ItemBow bowItem = (ItemBow) stack.getItem();
		IBowImproved bowImproved = null;
		EntityPlayer player = event.getEntityPlayer();
		ItemStack arrowStack = findAmmo(player);
		World world = event.getWorld();

		if (bowItem instanceof IBowImproved) {
			bowImproved = (IBowImproved) stack.getItem();
		}
		boolean flag = player.capabilities.isCreativeMode || (arrowStack.getItem() instanceof ItemArrow && ((ItemArrow) arrowStack.getItem()).isInfinite(arrowStack, stack, player));

		if (!arrowStack.isEmpty() || flag) {
			if (arrowStack.isEmpty()) {
				arrowStack = new ItemStack(Items.ARROW);
			}
			float f = ItemBow.getArrowVelocity(event.getCharge());
			float speedMod = bowImproved != null ? 1.0F + bowImproved.getArrowSpeedMultiplier() : 1.0F;

			if ((double) f >= 0.1D) {
				if (!world.isRemote) {
					int encMultishot = MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.multishot, stack), 0, 10);
					int encPunch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
					int encPower = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
					boolean encFlame = EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0;

					if (bowImproved != null) {
						bowImproved.onBowFired(player, stack);
					}
					for (int shot = 0; shot <= encMultishot; shot++) {
						EntityArrow arrow = createArrow(world, arrowStack, player);
						arrow.setAim(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 3.0F * speedMod, 1.0F + (1.5F - f) * shot);

						if (bowImproved != null) {
							arrow.setDamage(arrow.getDamage() * (1 + bowImproved.getArrowDamageMultiplier()));
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
						if (flag) {
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
				player.addStat(StatList.getObjectUseStats(bowItem));
			}
			event.setCanceled(true);
		}
	}

	@SubscribeEvent (priority = EventPriority.HIGHEST)
	public void handleArrowNockEvent(ArrowNockEvent event) {

		if (!(event.getBow().getItem() instanceof ItemBow)) {
			return;
		}
		ItemStack stack = event.getBow();
		EntityPlayer player = event.getEntityPlayer();
		ItemStack arrowStack = findAmmo(player);

		if (arrowStack.isEmpty() && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0) {
			arrowStack = new ItemStack(Items.ARROW);
		}
		if (arrowStack.isEmpty() && !player.capabilities.isCreativeMode) {
			event.setAction(new ActionResult<>(EnumActionResult.FAIL, stack));
		}
	}

	@SubscribeEvent
	public void handleLivingAttackEvent(LivingAttackEvent event) {

		if ((event.getEntityLiving() instanceof EntityPlayer)) {
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
	}

	@SubscribeEvent
	public void handleLivingDeathEvent(LivingDeathEvent event) {

		Entity entity = event.getEntity();

		if (!CoreProps.enableLivingEntityDeathMessages || entity.world.isRemote || !(entity instanceof EntityLiving) || !event.getEntityLiving().hasCustomName()) {
			return;
		}
		entity.world.getMinecraftServer().getPlayerList().sendMessage(event.getEntityLiving().getCombatTracker().getDeathMessage());
	}

	@SubscribeEvent
	public void handleLivingDropsEvent(LivingDropsEvent event) {

	}

	/* SERVER UTILS */
	public boolean isOp(String playerName) {

		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		playerName = playerName.trim();
		for (String a : server.getPlayerList().getOppedPlayerNames()) {
			if (playerName.equalsIgnoreCase(a)) {
				return true; // TODO: this is completely horrible. needs improvement. will probably still be horrible.
			}
		}
		return false;
	}

	public boolean isClient() {

		return false;
	}

	public boolean isServer() {

		return true;
	}

	public World getClientWorld() {

		return null;
	}

	public IThreadListener getClientListener() {

		// If this is called on the server, expect a crash.
		return null;
	}

	public IThreadListener getServerListener() {

		return FMLCommonHandler.instance().getMinecraftServerInstance();
	}

	/* PLAYER UTILS */
	public EntityPlayer findPlayer(String player) {

		return null;
	}

	public EntityPlayer getClientPlayer() {

		return null;
	}

	public List<EntityPlayer> getPlayerList() {

		List<EntityPlayer> result = new LinkedList<>();

		for (int i = 0; i < FMLCommonHandler.instance().getMinecraftServerInstance().worlds.length; i++) {
			if (FMLCommonHandler.instance().getMinecraftServerInstance().worlds[i] != null) {
				result.addAll(FMLCommonHandler.instance().getMinecraftServerInstance().worlds[i].playerEntities);
			}
		}
		return result;
	}

	public void updateFriendListGui() {

	}

	/* SOUND UTILS */
	public float getSoundVolume(int category) {

		return 0;
	}

}
