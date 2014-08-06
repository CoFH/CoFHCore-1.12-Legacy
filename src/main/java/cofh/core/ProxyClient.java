package cofh.core;

import cofh.CoFHCore;
import cofh.core.gui.client.GuiFriendsList;
import cofh.core.gui.element.TabAugment;
import cofh.core.gui.element.TabConfiguration;
import cofh.core.gui.element.TabEnergy;
import cofh.core.gui.element.TabInfo;
import cofh.core.gui.element.TabRedstone;
import cofh.core.gui.element.TabSecurity;
import cofh.core.gui.element.TabTutorial;
import cofh.core.key.CoFHKey;
import cofh.core.render.CoFHFontRender;
import cofh.core.render.IconRegistry;
import cofh.core.util.KeyBindingEmpower;
import cofh.core.util.SocialRegistry;
import cofh.core.util.TickHandlerEnderRegistry;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.config.Configuration;

@SideOnly(Side.CLIENT)
public class ProxyClient extends Proxy {

	public static CoFHFontRender fontRenderer;

	@Override
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {

		if (event.map.getTextureType() == 0) {

		} else if (event.map.getTextureType() == 1) {

			IconRegistry.addIcon("IconAccessFriends", "cofh:icons/Icon_Access_Friends", event.map);
			IconRegistry.addIcon("IconAccessGuild", "cofh:icons/Icon_Access_Guild", event.map);
			IconRegistry.addIcon("IconAccessPrivate", "cofh:icons/Icon_Access_Private", event.map);
			IconRegistry.addIcon("IconAccessPublic", "cofh:icons/Icon_Access_Public", event.map);
			IconRegistry.addIcon("IconAccept", "cofh:icons/Icon_Accept", event.map);
			IconRegistry.addIcon("IconAcceptInactive", "cofh:icons/Icon_Accept_Inactive", event.map);
			IconRegistry.addIcon("IconAugment", "cofh:icons/Icon_Augment", event.map);
			IconRegistry.addIcon("IconButton", "cofh:icons/Icon_Button", event.map);
			IconRegistry.addIcon("IconButtonHighlight", "cofh:icons/Icon_Button_Highlight", event.map);
			IconRegistry.addIcon("IconButtonInactive", "cofh:icons/Icon_Button_Inactive", event.map);
			IconRegistry.addIcon("IconCancel", "cofh:icons/Icon_Cancel", event.map);
			IconRegistry.addIcon("IconCancelInactive", "cofh:icons/Icon_Cancel_Inactive", event.map);
			IconRegistry.addIcon("IconConfig", "cofh:icons/Icon_Config", event.map);
			IconRegistry.addIcon("IconEnergy", "cofh:icons/Icon_Energy", event.map);
			IconRegistry.addIcon("IconNope", "cofh:icons/Icon_Nope", event.map);
			IconRegistry.addIcon("IconInformation", "cofh:icons/Icon_Information", event.map);
			IconRegistry.addIcon("IconTutorial", "cofh:icons/Icon_Tutorial", event.map);

			IconRegistry.addIcon("IconGunpowder", Items.gunpowder.getIconFromDamage(0));
			IconRegistry.addIcon("IconRedstone", Items.redstone.getIconFromDamage(0));
			IconRegistry.addIcon("IconRSTorchOff", "cofh:icons/Icon_RSTorchOff", event.map);
			IconRegistry.addIcon("IconRSTorchOn", "cofh:icons/Icon_RSTorchOn", event.map);

			IconRegistry.addIcon("IconArrowDown0", "cofh:icons/Icon_ArrowDown_Inactive", event.map);
			IconRegistry.addIcon("IconArrowDown1", "cofh:icons/Icon_ArrowDown", event.map);
			IconRegistry.addIcon("IconArrowUp0", "cofh:icons/Icon_ArrowUp_Inactive", event.map);
			IconRegistry.addIcon("IconArrowUp1", "cofh:icons/Icon_ArrowUp", event.map);
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
	public EntityPlayer getClientPlayer() {

		return Minecraft.getMinecraft().thePlayer;
	}

	@Override
	public World getClientWorld() {

		return Minecraft.getMinecraft().theWorld;
	}

	@Override
	public List<EntityPlayer> getPlayerList() {

		return new LinkedList<EntityPlayer>();
	}

	@Override
	public void updateFriendListGui() {

		if (Minecraft.getMinecraft().currentScreen != null) {
			((GuiFriendsList) Minecraft.getMinecraft().currentScreen).taFriendsList.textLines = SocialRegistry.clientPlayerFriends;
		}
	}

	@Override
	public void registerKeyBinds() {

		super.registerKeyBinds();
		FMLCommonHandler.instance().bus().register(new CoFHKey());
		CoFHKey.addKeyBind(KeyBindingEmpower.instance);
	}

	@Override
	public void registerRenderInformation() {

		TabAugment.initialize();
		TabConfiguration.initialize();
		TabEnergy.initialize();
		TabInfo.initialize();
		TabRedstone.initialize();
		TabSecurity.initialize();
		TabTutorial.initialize();

		String comment = "Set to false to disable any particles from spawning in minecraft.";
		if (!CoFHCore.configClient.get(Configuration.CATEGORY_GENERAL, "EnableParticles", true, comment)) {
			CoFHCore.log.info("Replacing EffectRenderer");
			Minecraft.getMinecraft().effectRenderer = new cofh.core.render.CustomEffectRenderer();
		}

		comment = "Set to false to disable rendering from sorting chunks";
		if (!CoFHCore.configClient.get(Configuration.CATEGORY_GENERAL, "EnableRenderSorting", true, comment)) {
			CoFHProps.enableRenderSorting = false;
		}

		CoFHCore.configClient.save();

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

		super.registerTickHandlers();
		FMLCommonHandler.instance().bus().register(TickHandlerEnderRegistry.instance);
	}

	/* SOUND UTILS */
	@Override
	public float getSoundVolume(int category) {

		if (category > SoundCategory.values().length) {
			return 0;
		}
		return FMLClientHandler.instance().getClient().gameSettings.getSoundLevel(SoundCategory.values()[category]);
	}

}
