package cofh.core.proxy;

import cofh.CoFHCore;
import cofh.core.gui.client.GuiFriendsList;
import cofh.core.init.CoreProps;
import cofh.core.init.CoreTextures;
import cofh.core.key.KeyBindingItemMultiMode;
import cofh.core.key.KeyHandlerCore;
import cofh.core.render.CustomEffectRenderer;
import cofh.core.render.FontRendererCore;
import cofh.core.render.ShaderHelper;
import cofh.core.util.RegistrySocial;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

@SideOnly (Side.CLIENT)
public class ProxyClient extends Proxy {

	/* INIT */
	@Override
	public void preInit(FMLPreInitializationEvent event) {

		super.preInit(event);
		Minecraft.memoryReserve = null;

		ShaderHelper.initShaders();

		if (CoreProps.disableParticles) {
			CoFHCore.LOG.info("Replacing EffectRenderer - Particles have been disabled.");
			Minecraft.getMinecraft().effectRenderer = new CustomEffectRenderer();
		}
	}

	@Override
	public void initialize(FMLInitializationEvent event) {

		super.initialize(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {

		super.postInit(event);

		fontRenderer = new FontRendererCore(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().renderEngine, false);

		if (Minecraft.getMinecraft().gameSettings.language != null) {
			fontRenderer.setUnicodeFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLocaleUnicode());
			fontRenderer.setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
		}
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(fontRenderer);

		fontRenderer.initSpecialCharacters();

	}

	/* REGISTRATION */
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {

		CoreTextures.registerIcons(event);
	}

	@Override
	public void registerKeyBinds() {

		super.registerKeyBinds();

		KeyHandlerCore.addClientKeyBind(KeyBindingItemMultiMode.instance);
		// KeyHandlerCore.addClientKeyBind(KeyBindingPlayerAugments.instance);

		ClientRegistry.registerKeyBinding(KEYBINDING_MULTIMODE);
		// ClientRegistry.registerKeyBinding(KEYBINDING_AUGMENTS);
	}

	/* HELPERS */
	@Override
	public int getKeyBind(String key) {

		if (key.equalsIgnoreCase("cofh.multimode")) {
			return KEYBINDING_MULTIMODE.getKeyCode();
		} else if (key.equalsIgnoreCase("cofh.augment")) {
			//return KEYBINDING_AUGMENTS.getKeyCode();
		}
		return -1;
	}

	@Override
	public void addIndexedChatMessage(ITextComponent chat, int index) {

		if (chat == null) {
			Minecraft.getMinecraft().ingameGUI.getChatGUI().deleteChatLine(index);
		} else {
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(chat, index);
		}
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

	@Override
	public IThreadListener getClientListener() {

		return Minecraft.getMinecraft();
	}

	@Override
	public IThreadListener getServerListener() {

		return Minecraft.getMinecraft().getIntegratedServer();
	}

	/* PLAYER UTILS */
	@Override
	public EntityPlayer findPlayer(String playerName) {

		for (EntityPlayer player : FMLClientHandler.instance().getClient().theWorld.playerEntities) {
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
	public void updateFriendListGui() {

		if (Minecraft.getMinecraft().currentScreen != null) {
			((GuiFriendsList) Minecraft.getMinecraft().currentScreen).taFriendsList.textLines = RegistrySocial.clientPlayerFriends;
		}
	}

	/* SOUND UTILS */
	@Override
	public float getSoundVolume(int category) {

		if (category > SoundCategory.values().length) {
			return 0;
		}
		return FMLClientHandler.instance().getClient().gameSettings.getSoundLevel(SoundCategory.values()[category]);
	}

	/* REFERENCES */
	public static FontRendererCore fontRenderer;

	public static final KeyBind KEYBINDING_MULTIMODE = new KeyBind("key.cofh.multimode", Keyboard.KEY_V, "key.cofh.category");
	public static final KeyBind KEYBINDING_AUGMENTS = null; //new KeyBind("key.cofh.augments", Keyboard.KEY_C, "key.cofh.category");

	/* KEYBIND CLASS */
	public static class KeyBind extends KeyBinding {

		public KeyBind(String name, int key, String category) {

			super(name, key, category);
		}

		public int cofh_conflictCode() {

			return 0;
		}

	}

}
