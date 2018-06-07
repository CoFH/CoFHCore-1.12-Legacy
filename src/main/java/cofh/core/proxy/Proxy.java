package cofh.core.proxy;

import cofh.core.key.KeyBindingItemMultiMode;
import cofh.core.key.KeyHandlerCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.LinkedList;
import java.util.List;

public class Proxy {

	/* INIT */
	public void preInit(FMLPreInitializationEvent event) {

		MinecraftForge.EVENT_BUS.register(EventHandler.INSTANCE);
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

	/* SERVER UTILS */
	public boolean isOp(String playerName) {

		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		playerName = playerName.trim();
		for (String opName : server.getPlayerList().getOppedPlayerNames()) {
			if (playerName.equalsIgnoreCase(opName)) {
				return true;
			}
		}
		return false;
	}

	public boolean isClient() {

		return false;
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

}
