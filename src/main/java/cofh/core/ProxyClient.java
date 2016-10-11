package cofh.core;

import cofh.CoFHCore;
import cofh.core.key.KeyBindingMultiMode;
import cofh.core.key.KeyHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import cofh.core.render.FontRendererCoFH;
import cofh.lib.util.helpers.StringHelper;
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

		/* GLOBAL */
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

		/* GENERAL */
		category = "General";
		String comment = "Set to false to disable shader effects in CoFH Mods.";
		if (!CoFHCore.CONFIG_CLIENT.get(category, "EnableShaderEffects", true, comment)) {
			CoFHProps.enableShaderEffects = false;
		}
		comment = "Set to true to use Color Blind Textures in CoFH Mods, where applicable.";
		if (CoFHCore.CONFIG_CLIENT.get(category, "EnableColorBlindTextures", false, comment)) {
			CoFHProps.enableColorBlindTextures = true;
		}

		/* INTERFACE */
		category = "Interface";
		comment = "Set to true to draw borders on GUI slots in CoFH Mods, where applicable.";
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

		/* SECURITY */
		category = "Security";

		//TODO FIGURE OUT IF NEEDED
/*
		comment = "Set to false to disable warnings about Ops having access to 'secure' blocks upon logging on to a server.";
		if (!CoFHCore.CONFIG_CLIENT.get(category, "OpsCanAccessSecureBlocksWarning", true, comment)) {
			CoFHProps.enableOpSecureAccessWarning = false;
		}
*/
		CoFHCore.CONFIG_CLIENT.save();

		super.preInit(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {

		super.postInit(event);

		registerRenderInformation();
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



	/* REGISTRATION */
	@Override
	public void registerKeyBinds() {

		super.registerKeyBinds();

		KeyHandler.addKeyBind(KeyBindingMultiMode.instance);
		// KeyHandler.addKeyBind(KeyBindingAugments.instance);

		ClientRegistry.registerKeyBinding(KEYBINDING_MULTIMODE);
		// ClientRegistry.registerKeyBinding(KEYBINDING_AUGMENTS);
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
