package cofh.core;

import cofh.CoFHCore;
import cofh.key.CoFHKey;
import cofh.key.KeyPacket;
import cofh.network.CoFHTileInfoPacket;
import cofh.network.CoFHTilePacket;
import cofh.social.SocialPacket;
import cofh.util.KeyBindingEmpower;
import cofh.util.oredict.OreDictionaryArbiter;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;

public class Proxy {

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void initializeIcons(TextureStitchEvent.Post event) {

	}

	@SubscribeEvent
	public void handleOreRegisterEvent(OreRegisterEvent event) {

		OreDictionaryArbiter.registerOreDictionaryEntry(event.Ore, event.Name);
	}

	public boolean isOp(String playerName) {

		MinecraftServer theServer = FMLCommonHandler.instance().getMinecraftServerInstance();
		playerName = playerName.trim();
		for (String a : theServer.getConfigurationManager().func_152606_n()) {
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

	public void playSound() {

	}

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

	public World getWorld() {

		return null;
	}

	public int registerGui(String guiName, boolean isTileEntity) {

		Class<?> gui = null;
		Class<?> container = null;
		try {
			gui = Proxy.class.getClassLoader().loadClass("cofh.gui.client.Gui" + guiName);
		} catch (ClassNotFoundException e) {

		}
		try {
			container = Proxy.class.getClassLoader().loadClass("cofh.gui.container.Container" + guiName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (gui == null) {
			if (isTileEntity) {
				return CoFHCore.guiHandler.registerServerGuiTile(container);
			}
			return CoFHCore.guiHandler.registerServerGui(container);
		} else {
			if (isTileEntity) {
				return CoFHCore.guiHandler.registerClientGuiTile(gui, container);
			}
			return CoFHCore.guiHandler.registerClientGui(gui, container);
		}
	}

	public int registerGui(String guiName, String containerName, boolean isTileEntity) {

		Class<?> gui = null;
		Class<?> container = null;
		try {
			gui = Proxy.class.getClassLoader().loadClass("cofh.gui.client.Gui" + guiName);
		} catch (ClassNotFoundException e) {
		}
		try {
			container = Proxy.class.getClassLoader().loadClass("cofh.gui.container.Container" + containerName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (gui == null) {
			if (isTileEntity) {
				return CoFHCore.guiHandler.registerServerGuiTile(container);
			}
			return CoFHCore.guiHandler.registerServerGui(container);
		} else {
			if (isTileEntity) {
				return CoFHCore.guiHandler.registerClientGuiTile(gui, container);
			}
			return CoFHCore.guiHandler.registerClientGui(gui, container);
		}
	}

	public void registerKeyBinds() {

		CoFHKey.addServerKeyBind(KeyBindingEmpower.instance);
	}

	public void registerPacketInformation() {

		SocialPacket.initialize();
		KeyPacket.initialize();
		CoFHTileInfoPacket.initialize();
		CoFHTilePacket.initialize();
	}

	public void registerRenderInformation() {

	}

	public void registerTickHandlers() {

	}

}
