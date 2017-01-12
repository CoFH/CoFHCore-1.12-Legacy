package cofh.core.render.hitbox;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;

public class RenderHitbox {

	public static double extraSpace = 0.002;

	/**
	 * Draws the selection box for the player. Args: entityPlayer, rayTraceHit, i, itemStack, partialTickTime
	 *
	 * @param customHitBox
	 */
	public static void drawSelectionBox(EntityPlayer thePlayer, RayTraceResult rayTraceResult, float pTickTime, CustomHitBox customHitBox) {

		if (rayTraceResult.typeOfHit == Type.BLOCK) {
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
			GlStateManager.glLineWidth(2.0F);
			GlStateManager.disableTexture2D();
			GlStateManager.depthMask(false);
			IBlockState state = thePlayer.worldObj.getBlockState(rayTraceResult.getBlockPos());

			if (state.getMaterial() != Material.AIR) {
				//block.setBlockBoundsBasedOnState(thePlayer.worldObj, rayTraceResult.blockX, rayTraceResult.blockY, rayTraceResult.blockZ);
				double d0 = thePlayer.lastTickPosX + (thePlayer.posX - thePlayer.lastTickPosX) * pTickTime;
				double d1 = thePlayer.lastTickPosY + (thePlayer.posY - thePlayer.lastTickPosY) * pTickTime;
				double d2 = thePlayer.lastTickPosZ + (thePlayer.posZ - thePlayer.lastTickPosZ) * pTickTime;
				drawOutlinedBoundingBox(customHitBox.addExtraSpace(extraSpace).offsetForDraw(-d0, -d1, -d2));
			}

			GlStateManager.depthMask(true);
			GlStateManager.enableTexture2D();
			GlStateManager.disableBlend();
		}
	}

	/**
	 * Draws lines for the edges of the bounding box.
	 */
	public static void drawOutlinedBoundingBox(CustomHitBox hitbox) {

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		buffer.begin(1, DefaultVertexFormats.POSITION);

		// Top and Bottom faces
		addMainVertex(hitbox, 1, hitbox.middleHeight, buffer);
		addMainVertex(hitbox, 0, 0, buffer);

		// Top and Bottom extensions
		addTopBottomVertex(hitbox, 1, hitbox.sideLength[1], hitbox.middleHeight + hitbox.sideLength[1], buffer);
		addTopBottomVertex(hitbox, 0, -hitbox.sideLength[0], -hitbox.sideLength[0], buffer);

		// Vertical Lines
		addVerticalVertexs(hitbox, buffer);

		tessellator.draw();
	}

	public static void addVerticalVertexs(CustomHitBox hitbox, VertexBuffer buffer) {

		if (hitbox.drawSide[2]) {
			buffer.pos(hitbox.minX, hitbox.minY, hitbox.minZ - hitbox.sideLength[2]).endVertex();
			buffer.pos(hitbox.minX, hitbox.minY + hitbox.middleHeight, hitbox.minZ - hitbox.sideLength[2]).endVertex();
			buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY, hitbox.minZ - hitbox.sideLength[2]).endVertex();
			buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + hitbox.middleHeight, hitbox.minZ - hitbox.sideLength[2]).endVertex();
		}

		if (hitbox.drawSide[3]) {
			buffer.pos(hitbox.minX, hitbox.minY, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth).endVertex();
			buffer.pos(hitbox.minX, hitbox.minY + hitbox.middleHeight, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth).endVertex();
			buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth).endVertex();
			buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + hitbox.middleHeight, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth).endVertex();
		}

		if (hitbox.drawSide[4]) {
			buffer.pos(hitbox.minX - hitbox.sideLength[4], hitbox.minY, hitbox.minZ).endVertex();
			buffer.pos(hitbox.minX - hitbox.sideLength[4], hitbox.minY + hitbox.middleHeight, hitbox.minZ).endVertex();
			buffer.pos(hitbox.minX - hitbox.sideLength[4], hitbox.minY, hitbox.minZ + hitbox.middleWidth).endVertex();
			buffer.pos(hitbox.minX - hitbox.sideLength[4], hitbox.minY + hitbox.middleHeight, hitbox.minZ + hitbox.middleWidth).endVertex();
		}
		if (hitbox.drawSide[5]) {
			buffer.pos(hitbox.minX + hitbox.sideLength[5] + hitbox.middleDepth, hitbox.minY, hitbox.minZ).endVertex();
			buffer.pos(hitbox.minX + hitbox.sideLength[5] + hitbox.middleDepth, hitbox.minY + hitbox.middleHeight, hitbox.minZ).endVertex();
			buffer.pos(hitbox.minX + hitbox.sideLength[5] + hitbox.middleDepth, hitbox.minY, hitbox.minZ + hitbox.middleWidth).endVertex();
			buffer.pos(hitbox.minX + hitbox.sideLength[5] + hitbox.middleDepth, hitbox.minY + hitbox.middleHeight, hitbox.minZ + hitbox.middleWidth).endVertex();
		}

		if (!hitbox.drawSide[2] && !hitbox.drawSide[5]) {
			buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY, hitbox.minZ).endVertex();
			buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + hitbox.middleHeight, hitbox.minZ).endVertex();
		}

		if (!hitbox.drawSide[3] && !hitbox.drawSide[5]) {
			buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY, hitbox.minZ + hitbox.middleWidth).endVertex();
			buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + hitbox.middleHeight, hitbox.minZ + hitbox.middleWidth).endVertex();
		}

		if (!hitbox.drawSide[2] && !hitbox.drawSide[4]) {
			buffer.pos(hitbox.minX, hitbox.minY, hitbox.minZ).endVertex();
			buffer.pos(hitbox.minX, hitbox.minY + hitbox.middleHeight, hitbox.minZ).endVertex();
		}

		if (!hitbox.drawSide[3] && !hitbox.drawSide[4]) {
			buffer.pos(hitbox.minX, hitbox.minY, hitbox.minZ + hitbox.middleWidth).endVertex();
			buffer.pos(hitbox.minX, hitbox.minY + hitbox.middleHeight, hitbox.minZ + hitbox.middleWidth).endVertex();
		}

	}

	public static void addTopBottomVertex(CustomHitBox hitbox, int side, double sideLength, double heightToAdd, VertexBuffer buffer) {

		if (hitbox.drawSide[side]) {
			// Draw Square - I assume this is faster then drawing/changing modes/drawing/changing modes to go from line -> square -> line mode
			buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
			buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
			buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
			buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
			buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
			buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
			buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
			buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();

			// Draw vertical lines
			buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
			buffer.pos(hitbox.minX, hitbox.minY + heightToAdd - sideLength, hitbox.minZ).endVertex();

			buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
			buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd - sideLength, hitbox.minZ).endVertex();

			buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
			buffer.pos(hitbox.minX, hitbox.minY + heightToAdd - sideLength, hitbox.minZ + hitbox.middleWidth).endVertex();

			buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
			buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd - sideLength, hitbox.minZ + hitbox.middleWidth).endVertex();

		}
	}

	public static void addMainVertex(CustomHitBox hitbox, int side, double heightToAdd, VertexBuffer buffer) {

		if (!hitbox.drawSide[side]) {
			if (!hitbox.drawSide[4]) {
				// Draw Main Line
				buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
				buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
			} else {
				// Draw Main Line
				buffer.pos(hitbox.minX - hitbox.sideLength[4], hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
				buffer.pos(hitbox.minX - hitbox.sideLength[4], hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
				// Draw North Side Line
				buffer.pos(hitbox.minX - hitbox.sideLength[4], hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
				buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
				// Draw South Side Line
				buffer.pos(hitbox.minX - hitbox.sideLength[4], hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
				buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
			}
			if (!hitbox.drawSide[5]) {
				// Draw Main Line
				buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
				buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
			} else {
				// Draw Main Line
				buffer.pos(hitbox.minX + hitbox.middleDepth + hitbox.sideLength[5], hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
				buffer.pos(hitbox.minX + hitbox.middleDepth + hitbox.sideLength[5], hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
				// Draw North Side Line
				buffer.pos(hitbox.minX + hitbox.sideLength[5] + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
				buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
				// Draw South Side Line
				buffer.pos(hitbox.minX + hitbox.sideLength[5] + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
				buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + +hitbox.middleWidth).endVertex();
			}

			if (!hitbox.drawSide[2]) {
				// Draw Main Line
				buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
				buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
			} else {
				// Draw Main Line
				buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ - hitbox.sideLength[2]).endVertex();
				buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ - hitbox.sideLength[2]).endVertex();
				// Draw North Side Line
				buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ - hitbox.sideLength[2]).endVertex();
				buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
				// Draw South Side Line
				buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ - hitbox.sideLength[2]).endVertex();
				buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ).endVertex();

			}
			if (!hitbox.drawSide[3]) {
				// Draw Main Line
				buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
				buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
			} else {
				// Draw Main Line
				buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth).endVertex();
				buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth).endVertex();
				// Draw North Side Line
				buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth).endVertex();
				buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
				// Draw South Side Line
				buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth).endVertex();
				buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
			}
		} else {
			if (hitbox.drawSide[4]) {
				// Draw Main Line
				buffer.pos(hitbox.minX - hitbox.sideLength[4], hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
				buffer.pos(hitbox.minX - hitbox.sideLength[4], hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
				// Draw North Side Line
				buffer.pos(hitbox.minX - hitbox.sideLength[4], hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
				buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
				// Draw South Side Line
				buffer.pos(hitbox.minX - hitbox.sideLength[4], hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
				buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
			}
			if (hitbox.drawSide[5]) {
				// Draw Main Line
				buffer.pos(hitbox.minX + hitbox.middleDepth + hitbox.sideLength[5], hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
				buffer.pos(hitbox.minX + hitbox.middleDepth + hitbox.sideLength[5], hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
				// Draw North Side Line
				buffer.pos(hitbox.minX + hitbox.sideLength[5] + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
				buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
				// Draw South Side Line
				buffer.pos(hitbox.minX + hitbox.sideLength[5] + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
				buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + +hitbox.middleWidth).endVertex();
			}

			if (hitbox.drawSide[2]) {
				// Draw Main Line
				buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ - hitbox.sideLength[2]).endVertex();
				buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ - hitbox.sideLength[2]).endVertex();
				// Draw North Side Line
				buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ - hitbox.sideLength[2]).endVertex();
				buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ).endVertex();
				// Draw South Side Line
				buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ - hitbox.sideLength[2]).endVertex();
				buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ).endVertex();

			}
			if (hitbox.drawSide[3]) {
				// Draw Main Line
				buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth).endVertex();
				buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth).endVertex();
				// Draw North Side Line
				buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth).endVertex();
				buffer.pos(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
				// Draw South Side Line
				buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth).endVertex();
				buffer.pos(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth).endVertex();
			}
		}
	}

}
