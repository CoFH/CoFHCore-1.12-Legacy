package cofh.core.proxy;

import cofh.CoFHCore;
import cofh.core.block.IFogOverlay;
import cofh.core.gui.client.GuiFriendList;
import cofh.core.init.CoreProps;
import cofh.core.init.CoreTextures;
import cofh.core.key.KeyBindingItemMultiMode;
import cofh.core.key.KeyHandlerCore;
import cofh.core.render.CustomEffectRenderer;
import cofh.core.render.FontRendererCore;
import cofh.core.render.RenderEventHandler;
import cofh.core.render.ShaderHelper;
import cofh.core.util.RegistrySocial;
import cofh.lib.util.RayTracer;
import cofh.lib.util.helpers.RenderHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
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

	private static final ResourceLocation UNDERWATER_GRAYSCALE = new ResourceLocation("cofh:textures/misc/underwater_grayscale.png");

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
		MinecraftForge.EVENT_BUS.register(RenderEventHandler.instance);
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

	/* EVENT HANDLERS */
	@SubscribeEvent
	public void handleFluidBlockOverlayEvent(RenderBlockOverlayEvent event) {

		if (event.getOverlayType() == OverlayType.WATER) {
			EntityPlayer player = event.getPlayer();
			Vec3d playerEyePos = RayTracer.getCorrectedHeadVec(player);
			IBlockState state = player.worldObj.getBlockState(new BlockPos(playerEyePos));
			Block block = state.getBlock();

			if (block instanceof IFogOverlay) {

				RenderHelper.bindTexture(UNDERWATER_GRAYSCALE);
				float brightness = player.getBrightness(event.getRenderPartialTicks());
				Vec3d color = ((IFogOverlay) block).getFog(state, player, 0.0F, 0.0F, 0.0F).scale(brightness);

				GlStateManager.color((float) color.xCoord, (float) color.yCoord, (float) color.zCoord, 0.5F);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
				GlStateManager.pushMatrix();

				float yaw = -player.rotationYaw / 64.0F;
				float pitch = player.rotationPitch / 64.0F;

				Tessellator t = Tessellator.getInstance();
				VertexBuffer buffer = t.getBuffer();

				buffer.begin(0x07, DefaultVertexFormats.POSITION_TEX);
				buffer.pos(-1.0D, -1.0D, -0.5D).tex(4.0F + yaw, 4.0F + pitch).endVertex();
				buffer.pos(1.0D, -1.0D, -0.5D).tex(0.0F + yaw, 4.0F + pitch).endVertex();
				buffer.pos(1.0D, 1.0D, -0.5D).tex(0.0F + yaw, 0.0F + pitch).endVertex();
				buffer.pos(-1.0D, 1.0D, -0.5D).tex(4.0F + yaw, 0.0F + pitch).endVertex();
				t.draw();

				GlStateManager.popMatrix();
				GlStateManager.disableBlend();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				event.setCanceled(true);
			}

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

		return new LinkedList<>();
	}

	@Override
	public void updateFriendListGui() {

		if (Minecraft.getMinecraft().currentScreen != null) {
			((GuiFriendList) Minecraft.getMinecraft().currentScreen).taFriendList.textLines = RegistrySocial.clientPlayerFriends;
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
