package cofh.core;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.event.TextureStitchEvent;
import cofh.CoFHCore;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Proxy {

	public void registerRenderInformation() {

	}

	public void registerTickHandlers() {

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void initializeIcons(TextureStitchEvent.Post event) {

	}

	public boolean isOp(String playerName) {

		MinecraftServer theServer = FMLCommonHandler.instance().getMinecraftServerInstance();
		return theServer.getConfigurationManager().getOps().contains(playerName.trim().toLowerCase());
	}

	public boolean isClient() {

		return false;
	}

	public boolean isServer() {

		return true;
	}

	public EntityPlayer findPlayer(String player) {

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

	public EntityPlayer getClientPlayer() {

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

	public void registerPacketInformation() {

	}

}
