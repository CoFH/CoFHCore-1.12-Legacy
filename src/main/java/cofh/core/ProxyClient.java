package cofh.core;

import cofh.core.key.KeyBindingMultiMode;
import cofh.core.key.KeyHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import cofh.core.render.FontRendererCoFH;
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
import org.lwjgl.input.Keyboard;

public class ProxyClient extends Proxy {

	public static FontRendererCoFH fontRenderer;

	public static final KeyBindingCoFH KEYBINDING_MULTIMODE = new KeyBindingCoFH("key.cofh.multimode", Keyboard.KEY_V, "key.cofh.category");
	public static final KeyBindingCoFH KEYBINDING_AUGMENTS = null; //new KeyBind("key.cofh.augments", Keyboard.KEY_G, "key.cofh.category");

	/* INIT */

	@Override
	public void postInit(FMLPostInitializationEvent event) {
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
