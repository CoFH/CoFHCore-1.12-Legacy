package cofh.core;

import cofh.CoFHCore;
import cofh.core.entity.EntityCoFHArrow;
import cofh.core.gui.client.GuiFriendsList;
import cofh.core.gui.element.TabAugment;
import cofh.core.gui.element.TabConfiguration;
import cofh.core.gui.element.TabEnergy;
import cofh.core.gui.element.TabInfo;
import cofh.core.gui.element.TabRedstone;
import cofh.core.gui.element.TabSecurity;
import cofh.core.gui.element.TabTutorial;
import cofh.core.key.CoFHKeyHandler;
import cofh.core.render.CoFHFontRenderer;
import cofh.core.render.IconRegistry;
import cofh.core.render.ShaderHelper;
import cofh.core.sided.IFunctionSided;
import cofh.core.sided.IRunnableClient;
import cofh.core.sided.IRunnableServer;
import cofh.core.util.KeyBindingEmpower;
import cofh.core.util.KeyBindingMultiMode;
import cofh.lib.util.helpers.StringHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.client.event.TextureStitchEvent;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.EXTFramebufferObject;

@SideOnly(Side.CLIENT)
public class ProxyClient extends Proxy {

	public static CoFHFontRenderer fontRenderer;

	public static final KeyBind KEYBINDING_EMPOWER = new KeyBind("key.cofh.empower", Keyboard.KEY_V, "key.cofh.category");
	public static final KeyBind KEYBINDING_MULTIMODE = new KeyBind("key.cofh.multimode", Keyboard.KEY_C, "key.cofh.category");
	public static final KeyBind KEYBINDING_AUGMENTS = null; //new KeyBind("key.cofh.augments", Keyboard.KEY_G, "key.cofh.category");

	public static class KeyBind extends KeyBinding {

		public KeyBind(String name, int key, String category) {

			super(name, key, category);
		}

		public int cofh_conflictCode() {

			return 0;
		}

	}

	@Override
	public void preInit() {

		Minecraft.memoryReserve = null;

		RenderingRegistry.registerEntityRenderingHandler(EntityCoFHArrow.class, new RenderArrow());

		/* GLOBAL */
		String category = "Global";
		CoFHCore.configClient.getCategory(category).setComment("The options in this section change core Minecraft behavior and are not limited to CoFH mods.");

		String comment = "Set to false to disable any particles from spawning in Minecraft.";
		if (!CoFHCore.configClient.get(category, "EnableParticles", true, comment)) {
			CoFHCore.log.info("Replacing EffectRenderer");
			Minecraft.getMinecraft().effectRenderer = new cofh.core.render.CustomEffectRenderer();
		}

		comment = "Set to false to disable chunk sorting during rendering.";
		if (!CoFHCore.configClient.get(category, "EnableRenderSorting", true, comment)) {
			CoFHProps.enableRenderSorting = false;
		}

		comment = "Set to false to disable all animated textures in Minecraft.";
		if (!CoFHCore.configClient.get(category, "EnableAnimatedTextures", true, comment)) {
			CoFHProps.enableAnimatedTextures = false;
		}

		/* GENERAL */
		category = "General";
		comment = "Set to false to disable shader effects in CoFH Mods.";
		if (!CoFHCore.configClient.get(category, "EnableShaderEffects", true, comment)) {
			CoFHProps.enableShaderEffects = false;
		}
		comment = "Set to true to use Color Blind Textures in CoFH Mods, where applicable.";
		if (CoFHCore.configClient.get(category, "EnableColorBlindTextures", false, comment)) {
			CoFHProps.enableColorBlindTextures = true;
		}

		/* INTERFACE */
		category = "Interface";
		comment = "Set to true to draw borders on GUI slots in CoFH Mods, where applicable.";
		if (!CoFHCore.configClient.get(category, "EnableGUISlotBorders", true, comment)) {
			CoFHProps.enableGUISlotBorders = false;
		}

		/* INTERFACE - TOOLTIPS */
		category = "Interface.Tooltips";
		comment = "Set to false to hide a tooltip prompting you to press Shift for more details on various items.";
		if (!CoFHCore.configClient.get(category, "DisplayHoldShiftForDetail", true, comment)) {
			StringHelper.displayShiftForDetail = false;
		}
		comment = "Set to true to display large item counts as stacks rather than a single quantity.";
		if (CoFHCore.configClient.get(category, "DisplayContainedItemsAsStackCount", false, comment)) {
			StringHelper.displayStackCount = true;
		}

		/* SECURITY */
		category = "Security";

		comment = "Set to false to disable warnings about Ops having access to 'secure' blocks upon logging on to a server.";
		if (!CoFHCore.configClient.get(category, "OpsCanAccessSecureBlocksWarning", true, comment)) {
			CoFHProps.enableOpSecureAccessWarning = false;
		}
		CoFHCore.configClient.save();

		l: if (!Boolean.parseBoolean(System.getProperty("forge.forceDisplayStencil", "false"))) {
			if (Boolean.parseBoolean(System.getProperty("forge.forceNoStencil", "false"))) {
				break l;
			}
			try {
				if (Loader.isModLoaded("OpenMods")) {
					if (ReflectionHelper.findField(OpenGlHelper.class, "field_153212_w").getInt(null) == 2) {
						break l;
					}
				}
				int i = 8;
				ReflectionHelper.findField(ForgeHooksClient.class, "stencilBits").setInt(null, i);
				Framebuffer b = Minecraft.getMinecraft().getFramebuffer();
				b.createBindFramebuffer(b.framebufferWidth, b.framebufferHeight);
				switch (ReflectionHelper.findField(OpenGlHelper.class, "field_153212_w").getInt(null)) {
				case 2:
					switch (EXTFramebufferObject.glCheckFramebufferStatusEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT)) {
					case EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT:
						break;
					default: // stencil buffer is not supported
						ReflectionHelper.findField(ForgeHooksClient.class, "stencilBits").setInt(null, i = 0);
						b.createBindFramebuffer(b.framebufferWidth, b.framebufferHeight);
					}
					break;
				default:
					break;
				}
				BitSet stencilBits = ReflectionHelper.getPrivateValue(MinecraftForgeClient.class, null, "stencilBits");
				stencilBits.set(0, i);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void registerKeyBinds() {

		super.registerKeyBinds();
		CoFHKeyHandler.addKeyBind(KeyBindingEmpower.instance);
		CoFHKeyHandler.addKeyBind(KeyBindingMultiMode.instance);
		// CoFHKeyHandler.addKeyBind(KeyBindingAugments.instance);

		ClientRegistry.registerKeyBinding(KEYBINDING_EMPOWER);
		ClientRegistry.registerKeyBinding(KEYBINDING_MULTIMODE);
		// ClientRegistry.registerKeyBinding(KEYBINDING_AUGMENTS);
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

		ShaderHelper.initShaders();

		fontRenderer = new CoFHFontRenderer(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"),
				Minecraft.getMinecraft().renderEngine, false);

		if (Minecraft.getMinecraft().gameSettings.language != null) {
			fontRenderer.setUnicodeFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLocaleUnicode());
			fontRenderer.setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
		}
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(fontRenderer);

		fontRenderer.initSpecialCharacters();
	}

	@Override
	public void registerTickHandlers() {

		super.registerTickHandlers();
	}

	@Override
	public int getKeyBind(String key) {

		if (key.equalsIgnoreCase("cofh.empower")) {
			return KEYBINDING_EMPOWER.getKeyCode();
		} else if (key.equalsIgnoreCase("cofh.multimode")) {
			return KEYBINDING_MULTIMODE.getKeyCode();
		} else if (key.equalsIgnoreCase("cofh.augment")) {
			//return KEYBINDING_AUGMENTS.getKeyCode();
		}
		return -1;
	}

	@Override
	public void addIndexedChatMessage(IChatComponent chat, int index) {

		if (chat == null) {
			Minecraft.getMinecraft().ingameGUI.getChatGUI().deleteChatLine(index);
		} else {
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(chat, index);
		}
	}

	/* EVENT HANDLERS */
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

	@SubscribeEvent
	public void blockRenderBlockOverlay(RenderBlockOverlayEvent evt) {

		if (evt.overlayType == OverlayType.BLOCK && !evt.blockForOverlay.isNormalCube()) {
			// occasionally the overlay code screws up and tries to overlay a block that didn't pass the checks, this fixes that
			evt.setCanceled(true);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void initializeIcons(TextureStitchEvent.Post event) {

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

		for (Object a : FMLClientHandler.instance().getClient().theWorld.playerEntities) {
			EntityPlayer player = (EntityPlayer) a;
			if (player.getCommandSenderName().toLowerCase(Locale.US).equals(playerName.toLowerCase(Locale.US))) {
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

	@Override
	public void runServer(IRunnableServer runnable) {

	}

	@Override
	public void runClient(IRunnableClient runnable) {

		runnable.runClient();
	}

	@Override
	public <F, T> T apply(IFunctionSided<F, T> function, F input) {

		return function.applyClient(input);
	}
}
