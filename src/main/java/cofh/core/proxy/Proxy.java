package cofh.core.proxy;

import cofh.core.init.CoreProps;
import cofh.core.key.KeyBindingItemMultiMode;
import cofh.core.key.KeyHandlerCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
	public void handleLivingDeathEvent(LivingDeathEvent event) {

		Entity entity = event.getEntity();

		if (!CoreProps.enableLivingEntityDeathMessages || entity.worldObj.isRemote || !(entity instanceof EntityLiving) || !event.getEntityLiving().hasCustomName()) {
			return;
		}
		entity.worldObj.getMinecraftServer().getPlayerList().sendChatMsg(event.getEntityLiving().getCombatTracker().getDeathMessage());
	}

	@SubscribeEvent
	public void handleLivingDropsEvent(LivingDropsEvent event) {

	}

	@SubscribeEvent
	public void handleSaplingGrowTreeEvent(SaplingGrowTreeEvent event) {

		if (CoreProps.treeGrowthChance < 100 && event.getWorld().rand.nextInt(100) >= CoreProps.treeGrowthChance) {
			event.setResult(Result.DENY);
		}
	}

	@SideOnly (Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {

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

		List<EntityPlayer> result = new LinkedList<EntityPlayer>();

		for (int i = 0; i < FMLCommonHandler.instance().getMinecraftServerInstance().worldServers.length; i++) {
			if (FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[i] != null) {
				result.addAll(FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[i].playerEntities);
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
