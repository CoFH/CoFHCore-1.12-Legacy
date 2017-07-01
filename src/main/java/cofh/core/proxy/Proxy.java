package cofh.core.proxy;

import cofh.core.init.CoreProps;
import cofh.core.item.tool.ItemShieldCore;
import cofh.core.key.KeyBindingItemMultiMode;
import cofh.core.key.KeyHandlerCore;
import cofh.core.util.helpers.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
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

		KeyHandlerCore.addServerKeyBind(KeyBindingItemMultiMode.instance);
		// KeyHandlerCore.addServerKeyBind(KeyBindingPlayerAugments.instance);
	}

	/* HELPERS */
	public int getKeyBind(String key) {

		return 0;
	}

	public void addIndexedChatMessage(ITextComponent chat, int index) {

	}

	/* EVENT HANDLERS */
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
