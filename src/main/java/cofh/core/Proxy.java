package cofh.core;

import cofh.core.chat.PacketIndexedChat;
import cofh.core.key.KeyBindingMultiMode;
import cofh.core.key.KeyHandler;
import cofh.core.key.PacketKey;
import cofh.core.network.PacketHandler;
import cofh.core.network.PacketSocial;
import cofh.core.network.PacketTile;
import cofh.core.network.PacketTileInfo;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Proxy {

	/* INIT */
	public void preInit(FMLPreInitializationEvent event) {

		PacketHandler.instance.initialize();
	}

	public void initialize(FMLInitializationEvent event) {

	}

	public void postInit(FMLPostInitializationEvent event) {

		registerPacketInformation();

		PacketHandler.instance.postInit();
	}

	/* REGISTRATION */
	public void registerKeyBinds() {

		KeyHandler.addServerKeyBind(KeyBindingMultiMode.instance);
		// KeyHandler.addServerKeyBind(KeyBindingAugments.instance);
	}

	public void registerPacketInformation() {

		PacketIndexedChat.initialize();
		PacketKey.initialize();
		PacketTile.initialize();
		PacketSocial.initialize();
		PacketTileInfo.initialize();
	}

	/* HELPERS */
	public void addIndexedChatMessage(ITextComponent chat, int index) {

	}

	public int getKeyBind(String key) {

		return 0;
	}

	/* SERVER UTILS */
	public boolean isOp(String playerName) {

		MinecraftServer theServer = FMLCommonHandler.instance().getMinecraftServerInstance();
		playerName = playerName.trim();
		for (String a : theServer.getPlayerList().getOppedPlayerNames()) {
			if (playerName.equalsIgnoreCase(a)) {
				return true; // TODO: This is awful but likely no way to improve. Thanks Mojang.
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

	public void updateSocialGui() {

	}

}
