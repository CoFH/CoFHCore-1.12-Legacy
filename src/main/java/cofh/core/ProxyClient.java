package cofh.core;

import cofh.gui.element.TabInfo;
import cofh.gui.element.TabTutorial;
import cofh.key.CoFHKey;
import cofh.render.CoFHFontRender;
import cofh.render.IconRegistry;
import cofh.util.KeyBindingEmpower;
import cofh.util.TickHandlerEnderRegistry;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;

@SideOnly(Side.CLIENT)
public class ProxyClient extends Proxy {

	public static CoFHFontRender fontRenderer;

	@Override
	public void registerKeyBinds() {

		super.registerKeyBinds();
		FMLCommonHandler.instance().bus().register(new CoFHKey());
		CoFHKey.addKeyBind(KeyBindingEmpower.instance);
	}

	@Override
	public void registerRenderInformation() {

		TabInfo.enable = CoFHProps.enableInformationTabs;
		TabTutorial.enable = CoFHProps.enableTutorialTabs;

		fontRenderer = new CoFHFontRender(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"),
				Minecraft.getMinecraft().renderEngine, false);

		if (Minecraft.getMinecraft().gameSettings.language != null) {
			fontRenderer.setUnicodeFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLocaleUnicode());
			fontRenderer.setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
		}

		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(fontRenderer);
	}

	@Override
	public void registerTickHandlers() {

		FMLCommonHandler.instance().bus().register(TickHandlerEnderRegistry.instance);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {

		if (event.map.getTextureType() == 0) {

		} else if (event.map.getTextureType() == 1) {

			IconRegistry.addIcon("IconAccessFriends", "cofh:icons/Icon_Access_Friends", event.map);
			IconRegistry.addIcon("IconAccessPrivate", "cofh:icons/Icon_Access_Private", event.map);
			IconRegistry.addIcon("IconAccessPublic", "cofh:icons/Icon_Access_Public", event.map);
			IconRegistry.addIcon("IconAccept", "cofh:icons/Icon_Accept", event.map);
			IconRegistry.addIcon("IconAcceptInactive", "cofh:icons/Icon_Accept_Inactive", event.map);
			IconRegistry.addIcon("IconButton", "cofh:icons/Icon_Button", event.map);
			IconRegistry.addIcon("IconButtonHighlight", "cofh:icons/Icon_Button_Highlight", event.map);
			IconRegistry.addIcon("IconButtonInactive", "cofh:icons/Icon_Button_Inactive", event.map);
			IconRegistry.addIcon("IconCancel", "cofh:icons/Icon_Cancel", event.map);
			IconRegistry.addIcon("IconCancelInactive", "cofh:icons/Icon_Cancel_Inactive", event.map);
			IconRegistry.addIcon("IconEnergy", "cofh:icons/Icon_Energy", event.map);
			IconRegistry.addIcon("IconNope", "cofh:icons/Icon_Nope", event.map);
			IconRegistry.addIcon("IconInformation", "cofh:icons/Icon_Information", event.map);
			IconRegistry.addIcon("IconTutorial", "cofh:icons/Icon_Tutorial", event.map);

			IconRegistry.addIcon("IconGunpowder", Items.gunpowder.getIconFromDamage(0));
			IconRegistry.addIcon("IconRedstone", Items.redstone.getIconFromDamage(0));
			IconRegistry.addIcon("IconRSTorchOff", "cofh:icons/Icon_RSTorchOff", event.map);
			IconRegistry.addIcon("IconRSTorchOn", "cofh:icons/Icon_RSTorchOn", event.map);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void initializeIcons(TextureStitchEvent.Post event) {

	}

	@Override
	public boolean isOp(String playerName) {

		return true;
	}

	@Override
	public boolean isClient() {

		return true;
	}

	@Override
	public boolean isServer() {

		return false;
	}

	@Override
	public EntityPlayer findPlayer(String playerName) {

		for (Object a : FMLClientHandler.instance().getClient().theWorld.playerEntities) {
			EntityPlayer player = (EntityPlayer) a;
			if (player.getCommandSenderName().toLowerCase().equals(playerName.toLowerCase())) {
				return player;
			}
		}
		return null;
	}

	@Override
	public List<EntityPlayer> getPlayerList() {

		return new LinkedList<EntityPlayer>();
	}

	@Override
	public EntityPlayer getClientPlayer() {

		return Minecraft.getMinecraft().thePlayer;
	}

}
