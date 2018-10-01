package cofh.core.proxy;

import cofh.core.fluid.BlockFluidCore;
import cofh.core.item.IAOEBreakItem;
import cofh.core.util.RayTracer;
import cofh.core.util.helpers.RenderHelper;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.List;

@SideOnly (Side.CLIENT)
public class EventHandlerRender implements IResourceManagerReloadListener {

	public static final EventHandlerRender INSTANCE = new EventHandlerRender();

	private static final ResourceLocation UNDERWATER_GRAYSCALE = new ResourceLocation("cofh:textures/misc/underwater_grayscale.png");

	@SubscribeEvent (priority = EventPriority.LOW)
	public void renderExtraBlockBreak(RenderWorldLastEvent event) {

		PlayerControllerMP controllerMP = Minecraft.getMinecraft().playerController;

		if (controllerMP == null) {
			return;
		}
		EntityPlayer player = Minecraft.getMinecraft().player;
		ItemStack stack = player.getHeldItemMainhand();

		if (!stack.isEmpty() && stack.getItem() instanceof IAOEBreakItem) {
			Entity renderEntity = Minecraft.getMinecraft().getRenderViewEntity();
			if (renderEntity == null) {
				return;
			}
			IAOEBreakItem aoeTool = (IAOEBreakItem) stack.getItem();

			double distance = Math.max(controllerMP.getBlockReachDistance(), aoeTool.getReachDistance(stack));
			RayTraceResult traceResult = renderEntity.rayTrace(distance, event.getPartialTicks());
			if (traceResult != null) {
				ImmutableList<BlockPos> extraBlocks = aoeTool.getAOEBlocks(stack, traceResult.getBlockPos(), player);
				for (BlockPos pos : extraBlocks) {
					event.getContext().drawSelectionBox(player, new RayTraceResult(new Vec3d(0, 0, 0), null, pos), 0, event.getPartialTicks());
				}
			}
		}
		if (controllerMP.isHittingBlock) {
			if (!stack.isEmpty() && stack.getItem() instanceof IAOEBreakItem) {
				BlockPos pos = controllerMP.currentBlock;
				IAOEBreakItem aoeTool = (IAOEBreakItem) stack.getItem();
				drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), player, event.getPartialTicks(), player.getEntityWorld(), aoeTool.getAOEBlocks(stack, pos, player));
			}
		}
	}

	@SubscribeEvent
	public void handleFogDensityEvent(EntityViewRenderEvent.FogDensity event) {

		if (event.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntity();
			Vec3d playerEyePos = RayTracer.getCorrectedHeadVec(player);
			BlockPos pos = new BlockPos(playerEyePos);
			if (player.world.getBlockState(pos).getBlock() instanceof BlockFluidCore) {
				event.setCanceled(true);
				GlStateManager.setFog(GlStateManager.FogMode.EXP);
			}
		}
	}

	@SubscribeEvent
	public void handleFluidBlockOverlayEvent(RenderBlockOverlayEvent event) {

		if (event.getOverlayType() == OverlayType.WATER) {
			EntityPlayer player = event.getPlayer();
			Vec3d playerEyePos = RayTracer.getCorrectedHeadVec(player);
			BlockPos pos = new BlockPos(playerEyePos);
			IBlockState state = player.world.getBlockState(pos);
			Block block = state.getBlock();

			if (block instanceof BlockFluidCore) {
				RenderHelper.bindTexture(UNDERWATER_GRAYSCALE);
				float brightness = player.getBrightness();
				Vec3d color = block.getFogColor(player.world, pos, state, player, new Vec3d(1, 1, 1), 0.0F).scale(brightness);

				GlStateManager.color((float) color.x, (float) color.y, (float) color.z, 1.0F);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
				GlStateManager.pushMatrix();

				float yaw = -player.rotationYaw / 64.0F;
				float pitch = player.rotationPitch / 64.0F;

				Tessellator t = Tessellator.getInstance();
				BufferBuilder buffer = t.getBuffer();

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

	/* HELPERS */
	// Copy of RenderGlobal.drawBlockDamageTexture
	public void drawBlockDamageTexture(Tessellator tessellatorIn, BufferBuilder bufferIn, Entity entityIn, float partialTicks, World world, List<BlockPos> blocks) {

		double d0 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double) partialTicks;
		double d1 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double) partialTicks;
		double d2 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double) partialTicks;

		TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;
		int progress = (int) (Minecraft.getMinecraft().playerController.curBlockDamageMP * 10.0F) - 1;

		if (progress < 0) {
			return;
		}
		renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		preRenderDamagedBlocks();

		bufferIn.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		bufferIn.setTranslation(-d0, -d1, -d2);
		bufferIn.noColor();

		for (BlockPos blockpos : blocks) {
			TileEntity tile = world.getTileEntity(blockpos);
			boolean hasBreak = tile != null && tile.canRenderBreaking();

			if (!hasBreak) {
				IBlockState state = world.getBlockState(blockpos);
				if (state.getMaterial() != Material.AIR) {
					Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockDamage(state, blockpos, blockDamageIcons[progress], world);
				}
			}
		}
		tessellatorIn.draw();
		bufferIn.setTranslation(0.0D, 0.0D, 0.0D);
		postRenderDamagedBlocks();
	}

	// Copy of RenderGlobal.preRenderDamagedBlocks
	private void preRenderDamagedBlocks() {

		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.enableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
		GlStateManager.doPolygonOffset(-3.0F, -3.0F);
		GlStateManager.enablePolygonOffset();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableAlpha();
		GlStateManager.pushMatrix();
	}

	// Copy of RenderGlobal.postRenderDamagedBlocks
	private void postRenderDamagedBlocks() {

		GlStateManager.disableAlpha();
		GlStateManager.doPolygonOffset(0.0F, 0.0F);
		GlStateManager.disablePolygonOffset();
		GlStateManager.enableAlpha();
		GlStateManager.depthMask(true);
		GlStateManager.popMatrix();
	}

	@Override
	public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {

		TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();

		for (int i = 0; i < this.blockDamageIcons.length; ++i) {
			this.blockDamageIcons[i] = texturemap.getAtlasSprite("minecraft:blocks/destroy_stage_" + i);
		}
	}

	private final TextureAtlasSprite[] blockDamageIcons = new TextureAtlasSprite[10];

}
