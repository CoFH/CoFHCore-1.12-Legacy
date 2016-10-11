package cofh.core;

import cofh.CoFHCore;
import cofh.core.key.KeyBindingMultiMode;
import cofh.core.key.KeyHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import cofh.core.render.FontRendererCoFH;
import cofh.lib.gui.element.tab.*;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.StringHelper;
import javafx.scene.control.Tab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.input.Keyboard;

public class ProxyClient extends Proxy {

	public static FontRendererCoFH fontRenderer;

	public static final KeyBindingCoFH KEYBINDING_MULTIMODE = new KeyBindingCoFH("key.cofh.multimode", Keyboard.KEY_V, "key.cofh.category");
	public static final KeyBindingCoFH KEYBINDING_AUGMENTS = null; //new KeyBind("key.cofh.augments", Keyboard.KEY_G, "key.cofh.category");

	/* INIT */

	@Override
	public void preInit(FMLPreInitializationEvent event) {

		initConfig();

		super.preInit(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {

		super.postInit(event);

		registerRenderInformation();
	}

	/* REGISTRATION */
	@Override
	public void registerKeyBinds() {

		super.registerKeyBinds();

		KeyHandler.addKeyBind(KeyBindingMultiMode.instance);
		// KeyHandler.addKeyBind(KeyBindingAugments.instance);

		ClientRegistry.registerKeyBinding(KEYBINDING_MULTIMODE);
		// ClientRegistry.registerKeyBinding(KEYBINDING_AUGMENTS);
	}

	public void registerRenderInformation() {

		fontRenderer = new FontRendererCoFH(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"),
				Minecraft.getMinecraft().renderEngine, false);

		if (Minecraft.getMinecraft().gameSettings.language != null) {
			fontRenderer.setUnicodeFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLocaleUnicode());
			fontRenderer.setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
		}
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(fontRenderer);

		fontRenderer.initSpecialCharacters();
	}

	/* CONFIG */
	private void initConfig() {

		initGlobalConfig();
		initGeneralConfig();
		initInterfaceConfig();
		initSecurityConfig();

		CoFHCore.CONFIG_CLIENT.save();
	}

	private void initSecurityConfig() {

		String category = "Security";

		//TODO FIGURE OUT IF NEEDED
/*
		comment = "Set to false to disable warnings about Ops having access to 'secure' blocks upon logging on to a server.";
		if (!CoFHCore.CONFIG_CLIENT.get(category, "OpsCanAccessSecureBlocksWarning", true, comment)) {
			CoFHProps.enableOpSecureAccessWarning = false;
		}
*/
	}

	private void initInterfaceConfig() {

		String category = "Interface";
		String comment = "Set to true to draw borders on GUI slots in CoFH Mods, where applicable.";
		if (!CoFHCore.CONFIG_CLIENT.get(category, "EnableGUISlotBorders", true, comment)) {
			CoFHProps.enableGUISlotBorders = false;
		}

		/* INTERFACE - TOOLTIPS */
		category = "Interface.Tooltips";
		comment = "Set to false to hide a tooltip prompting you to press Shift for more details on various items.";
		if (!CoFHCore.CONFIG_CLIENT.get(category, "DisplayHoldShiftForDetail", true, comment)) {
			StringHelper.displayShiftForDetail = false;
		}
		comment = "Set to true to display large item counts as stacks rather than a single quantity.";
		if (CoFHCore.CONFIG_CLIENT.get(category, "DisplayContainedItemsAsStackCount", false, comment)) {
			StringHelper.displayStackCount = true;
		}

		/* TABS */
		//TODO refactor this
		/*
			This is ugly but with where tabs are currently there's no neat way without more refactoring. Can't send ConfigHandler over as that would require
			 dependency on CoFHCore, but would probably be the cleanest way as CoFHLib can't load its own config. So likely a library level configHandler
			 would solve this.

		*/
		category = "Tab.Augment";
		// enable = CoFHCore.configClient.get(category, "Enable", true);
		TabAugment.defaultSide = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "Side", TabAugment.defaultSide), 0, 1);
		TabAugment.defaultHeaderColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorHeader", TabAugment.defaultHeaderColor), 0, 0xffffff);
		TabAugment.defaultSubHeaderColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorSubHeader", TabAugment.defaultSubHeaderColor), 0, 0xffffff);
		TabAugment.defaultTextColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorText", TabAugment.defaultTextColor), 0, 0xffffff);
		TabAugment.defaultBackgroundColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorBackground", TabAugment.defaultBackgroundColor), 0, 0xffffff);

		category = "Tab.Configuration";
		// enable = CoFHCore.configClient.get(category, "Enable", true);
		TabConfiguration.defaultSide = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "Side", TabConfiguration.defaultSide), 0, 1);
		TabConfiguration.defaultHeaderColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorHeader", TabConfiguration.defaultHeaderColor), 0, 0xffffff);
		TabConfiguration.defaultSubHeaderColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorSubHeader", TabConfiguration.defaultSubHeaderColor), 0, 0xffffff);
		TabConfiguration.defaultTextColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorText", TabConfiguration.defaultTextColor), 0, 0xffffff);
		TabConfiguration.defaultBackgroundColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorBackground", TabConfiguration.defaultBackgroundColor), 0, 0xffffff);

		category = "Tab.Energy";
		// enable = CoFHCore.configClient.get(category, "Enable", true);
		TabEnergy.defaultSide = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "Side", TabEnergy.defaultSide), 0, 1);
		TabEnergy.defaultHeaderColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorHeader", TabEnergy.defaultHeaderColor), 0, 0xffffff);
		TabEnergy.defaultSubHeaderColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorSubHeader", TabEnergy.defaultSubHeaderColor), 0, 0xffffff);
		TabEnergy.defaultTextColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorText", TabEnergy.defaultTextColor), 0, 0xffffff);
		TabEnergy.defaultBackgroundColorOut = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorBackgroundProducer", TabEnergy.defaultBackgroundColorOut), 0, 0xffffff);
		TabEnergy.defaultBackgroundColorIn = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorBackgroundConsumer", TabEnergy.defaultBackgroundColorIn), 0, 0xffffff);

		category = "Tab.Information";
		TabInfo.enable = CoFHCore.CONFIG_CLIENT.get(category, "Enable", true);
		TabInfo.defaultSide = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "Side", TabInfo.defaultSide), 0, 1);
		TabInfo.defaultHeaderColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorHeader", TabInfo.defaultHeaderColor), 0, 0xffffff);
		TabInfo.defaultSubHeaderColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorSubHeader", TabInfo.defaultSubHeaderColor), 0, 0xffffff);
		TabInfo.defaultTextColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorText", TabInfo.defaultTextColor), 0, 0xffffff);
		TabInfo.defaultBackgroundColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorBackground", TabInfo.defaultBackgroundColor), 0, 0xffffff);

		category = "Tab.Redstone";
		// enable = CoFHCore.configClient.get(category, "Enable", true);
		TabRedstone.defaultSide = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "Side", TabRedstone.defaultSide), 0, 1);
		TabRedstone.defaultHeaderColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorHeader", TabRedstone.defaultHeaderColor), 0, 0xffffff);
		TabRedstone.defaultSubHeaderColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorSubHeader", TabRedstone.defaultSubHeaderColor), 0, 0xffffff);
		TabRedstone.defaultTextColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorText", TabRedstone.defaultTextColor), 0, 0xffffff);
		TabRedstone.defaultBackgroundColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorBackground", TabRedstone.defaultBackgroundColor), 0, 0xffffff);

		category = "Tab.Security";
		// enable = CoFHCore.configClient.get(category, "Enable", true);
		TabSecurity.defaultSide = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "Side", TabSecurity.defaultSide), 0, 1);
		TabSecurity.defaultHeaderColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorHeader", TabSecurity.defaultHeaderColor), 0, 0xffffff);
		TabSecurity.defaultSubHeaderColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorSubHeader", TabSecurity.defaultSubHeaderColor), 0, 0xffffff);
		TabSecurity.defaultTextColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorText", TabSecurity.defaultTextColor), 0, 0xffffff);
		TabSecurity.defaultBackgroundColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorBackground", TabSecurity.defaultBackgroundColor), 0, 0xffffff);

		category = "Tab.Tutorial";
		TabTutorial.enable = CoFHCore.CONFIG_CLIENT.get(category, "Enable", true);
		TabTutorial.defaultSide = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "Side", TabTutorial.defaultSide), 0, 1);
		TabTutorial.defaultHeaderColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorHeader", TabTutorial.defaultHeaderColor), 0, 0xffffff);
		TabTutorial.defaultSubHeaderColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorSubHeader", TabTutorial.defaultSubHeaderColor), 0, 0xffffff);
		TabTutorial.defaultTextColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorText", TabTutorial.defaultTextColor), 0, 0xffffff);
		TabTutorial.defaultBackgroundColor = MathHelper.clamp(CoFHCore.CONFIG_CLIENT.get(category, "ColorBackground", TabTutorial.defaultBackgroundColor), 0, 0xffffff);
	}

	private void initGeneralConfig() {

		String category = "General";
		String comment = "Set to false to disable shader effects in CoFH Mods.";
		if (!CoFHCore.CONFIG_CLIENT.get(category, "EnableShaderEffects", true, comment)) {
			CoFHProps.enableShaderEffects = false;
		}
		comment = "Set to true to use Color Blind Textures in CoFH Mods, where applicable.";
		if (CoFHCore.CONFIG_CLIENT.get(category, "EnableColorBlindTextures", false, comment)) {
			CoFHProps.enableColorBlindTextures = true;
		}
	}

	private void initGlobalConfig() {

		String category = "Global";
		CoFHCore.CONFIG_CLIENT.getCategory(category).setComment("The options in this section change core Minecraft behavior and are not limited to CoFH mods.");

		//TODO FIGURE OUT IF THIS IS NEEDED
/*
		String comment = "Set to false to disable any particles from spawning in Minecraft.";
		if (!CoFHCore.CONFIG_CLIENT.get(category, "EnableParticles", true, comment)) {
			CoFHCore.LOG.info("Replacing EffectRenderer");
			Minecraft.getMinecraft().effectRenderer = new cofh.core.render.CustomEffectRenderer();
		}

		String comment = "Set to false to disable chunk sorting during rendering.";
		if (!CoFHCore.CONFIG_CLIENT.get(category, "EnableRenderSorting", true, comment)) {
			CoFHProps.enableRenderSorting = false;
		}

		comment = "Set to false to disable all animated textures in Minecraft.";
		if (!CoFHCore.CONFIG_CLIENT.get(category, "EnableAnimatedTextures", true, comment)) {
			CoFHProps.enableAnimatedTextures = false;
		}

*/
	}

	/* HELPERS */
	@Override
	public void addIndexedChatMessage(ITextComponent chat, int index) {

		if (chat == null) {
			Minecraft.getMinecraft().ingameGUI.getChatGUI().deleteChatLine(index);
		} else {
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(chat, index);
		}
	}

	@Override
	public int getKeyBind(String key) {

		return 0;
	}

	/* SERVER UTILS */
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
	public World getClientWorld() {

		return Minecraft.getMinecraft().theWorld;
	}

	/* PLAYER UTILS */
	@Override
	public EntityPlayer findPlayer(String playerName) {

		for (Object o : FMLClientHandler.instance().getClient().theWorld.playerEntities) {
			EntityPlayer player = (EntityPlayer) o;
			if (player.getName().toLowerCase(Locale.US).equals(playerName.toLowerCase(Locale.US))) {
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
	public List<EntityPlayer> getPlayerList() {

		return new LinkedList<EntityPlayer>();
	}

	@Override
	public void updateSocialGui() {

		//		if (Minecraft.getMinecraft().currentScreen != null) {
		//			((GuiFriendsList) Minecraft.getMinecraft().currentScreen).taFriendsList.textLines = RegistrySocial.clientPlayerFriends;
		//		}
	}

	/* KEYBINDING CLASS */
	public static class KeyBindingCoFH extends KeyBinding {

		public KeyBindingCoFH(String name, int key, String category) {

			super(name, key, category);
		}

		public int cofh_conflictCode() {

			return 0;
		}

	}

}
