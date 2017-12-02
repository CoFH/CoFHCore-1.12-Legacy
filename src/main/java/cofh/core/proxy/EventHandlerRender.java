package cofh.core.proxy;

import cofh.core.item.IAOEBreakItem;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.List;

@SideOnly (Side.CLIENT)
public class EventHandlerRender implements IResourceManagerReloadListener {

	public static final EventHandlerRender INSTANCE = new EventHandlerRender();

	@SubscribeEvent
	public void renderExtraBlockBreak(RenderWorldLastEvent event) {

		PlayerControllerMP controllerMP = Minecraft.getMinecraft().playerController;

		if (controllerMP == null) {
			return;
		}
		EntityPlayer player = Minecraft.getMinecraft().player;
		ItemStack tool = player.getHeldItemMainhand();

		if (!tool.isEmpty() && tool.getItem() instanceof IAOEBreakItem) {
			Entity renderEntity = Minecraft.getMinecraft().getRenderViewEntity();
			if (renderEntity == null) {
				return;
			}
			double distance = controllerMP.getBlockReachDistance();
			RayTraceResult traceResult = renderEntity.rayTrace(distance, event.getPartialTicks());
			if (traceResult != null) {
				ImmutableList<BlockPos> extraBlocks = ((IAOEBreakItem) tool.getItem()).getAOEBlocks(tool, traceResult.getBlockPos(), player);
				for (BlockPos pos : extraBlocks) {
					event.getContext().drawSelectionBox(player, new RayTraceResult(new Vec3d(0, 0, 0), null, pos), 0, event.getPartialTicks());
				}
			}
		}
		if (controllerMP.isHittingBlock) {
			if (!tool.isEmpty() && tool.getItem() instanceof IAOEBreakItem) {
				BlockPos pos = controllerMP.currentBlock;
				drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), player, event.getPartialTicks(), player.getEntityWorld(), ((IAOEBreakItem) tool.getItem()).getAOEBlocks(tool, pos, player));
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
