package cofh.core.render.hitbox;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

import org.lwjgl.opengl.GL11;

public class RenderHitbox {

	public static double extraSpace = 0.002;

	/**
	 * Draws the selection box for the player. Args: entityPlayer, rayTraceHit, i, itemStack, partialTickTime
	 * 
	 * @param customHitBox
	 */
	public static void drawSelectionBox(EntityPlayer thePlayer, MovingObjectPosition mop, float pTickTime, CustomHitBox customHitBox) {

		if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
			GL11.glLineWidth(2.0F);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDepthMask(false);
			Block block = thePlayer.worldObj.getBlock(mop.blockX, mop.blockY, mop.blockZ);

			if (block.getMaterial() != Material.air) {
				block.setBlockBoundsBasedOnState(thePlayer.worldObj, mop.blockX, mop.blockY, mop.blockZ);
				double d0 = thePlayer.lastTickPosX + (thePlayer.posX - thePlayer.lastTickPosX) * pTickTime;
				double d1 = thePlayer.lastTickPosY + (thePlayer.posY - thePlayer.lastTickPosY) * pTickTime;
				double d2 = thePlayer.lastTickPosZ + (thePlayer.posZ - thePlayer.lastTickPosZ) * pTickTime;
				drawOutlinedBoundingBox(customHitBox.addExtraSpace(extraSpace).offsetForDraw(-d0, -d1, -d2));
			}

			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
		}
	}

	/**
	 * Draws lines for the edges of the bounding box.
	 */
	public static void drawOutlinedBoundingBox(CustomHitBox hitbox) {

		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawing(1);

		// Top and Bottom faces
		addMainVertex(hitbox, 1, hitbox.middleHeight, tessellator);
		addMainVertex(hitbox, 0, 0, tessellator);

		// Top and Bottom extensions
		addTopBottomVertex(hitbox, 1, hitbox.sideLength[1], hitbox.middleHeight + hitbox.sideLength[1], tessellator);
		addTopBottomVertex(hitbox, 0, -hitbox.sideLength[0], -hitbox.sideLength[0], tessellator);

		// Vertical Lines
		addVerticalVertexs(hitbox, tessellator);

		tessellator.draw();
	}

	public static void addVerticalVertexs(CustomHitBox hitbox, Tessellator tessellator) {

		if (hitbox.drawSide[2]) {
			tessellator.addVertex(hitbox.minX, hitbox.minY, hitbox.minZ - hitbox.sideLength[2]);
			tessellator.addVertex(hitbox.minX, hitbox.minY + hitbox.middleHeight, hitbox.minZ - hitbox.sideLength[2]);
			tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY, hitbox.minZ - hitbox.sideLength[2]);
			tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + hitbox.middleHeight, hitbox.minZ - hitbox.sideLength[2]);
		}

		if (hitbox.drawSide[3]) {
			tessellator.addVertex(hitbox.minX, hitbox.minY, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth);
			tessellator.addVertex(hitbox.minX, hitbox.minY + hitbox.middleHeight, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth);
			tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth);
			tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + hitbox.middleHeight, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth);
		}

		if (hitbox.drawSide[4]) {
			tessellator.addVertex(hitbox.minX - hitbox.sideLength[4], hitbox.minY, hitbox.minZ);
			tessellator.addVertex(hitbox.minX - hitbox.sideLength[4], hitbox.minY + hitbox.middleHeight, hitbox.minZ);
			tessellator.addVertex(hitbox.minX - hitbox.sideLength[4], hitbox.minY, hitbox.minZ + hitbox.middleWidth);
			tessellator.addVertex(hitbox.minX - hitbox.sideLength[4], hitbox.minY + hitbox.middleHeight, hitbox.minZ + hitbox.middleWidth);
		}
		if (hitbox.drawSide[5]) {
			tessellator.addVertex(hitbox.minX + hitbox.sideLength[5] + hitbox.middleDepth, hitbox.minY, hitbox.minZ);
			tessellator.addVertex(hitbox.minX + hitbox.sideLength[5] + hitbox.middleDepth, hitbox.minY + hitbox.middleHeight, hitbox.minZ);
			tessellator.addVertex(hitbox.minX + hitbox.sideLength[5] + hitbox.middleDepth, hitbox.minY, hitbox.minZ + hitbox.middleWidth);
			tessellator.addVertex(hitbox.minX + hitbox.sideLength[5] + hitbox.middleDepth, hitbox.minY + hitbox.middleHeight, hitbox.minZ + hitbox.middleWidth);
		}

		if (!hitbox.drawSide[2] && !hitbox.drawSide[5]) {
			tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY, hitbox.minZ);
			tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + hitbox.middleHeight, hitbox.minZ);
		}

		if (!hitbox.drawSide[3] && !hitbox.drawSide[5]) {
			tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY, hitbox.minZ + hitbox.middleWidth);
			tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + hitbox.middleHeight, hitbox.minZ + hitbox.middleWidth);
		}

		if (!hitbox.drawSide[2] && !hitbox.drawSide[4]) {
			tessellator.addVertex(hitbox.minX, hitbox.minY, hitbox.minZ);
			tessellator.addVertex(hitbox.minX, hitbox.minY + hitbox.middleHeight, hitbox.minZ);
		}

		if (!hitbox.drawSide[3] && !hitbox.drawSide[4]) {
			tessellator.addVertex(hitbox.minX, hitbox.minY, hitbox.minZ + hitbox.middleWidth);
			tessellator.addVertex(hitbox.minX, hitbox.minY + hitbox.middleHeight, hitbox.minZ + hitbox.middleWidth);
		}

	}

	public static void addTopBottomVertex(CustomHitBox hitbox, int side, double sideLength, double heightToAdd, Tessellator tessellator) {

		if (hitbox.drawSide[side]) {
			// Draw Square - I assume this is faster then drawing/changing modes/drawing/changing modes to go from line -> square -> line mode
			tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ);
			tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ);
			tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ);
			tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
			tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ);
			tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
			tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
			tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);

			// Draw vertical lines
			tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ);
			tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd - sideLength, hitbox.minZ);

			tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ);
			tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd - sideLength, hitbox.minZ);

			tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
			tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd - sideLength, hitbox.minZ + hitbox.middleWidth);

			tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
			tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd - sideLength, hitbox.minZ + hitbox.middleWidth);

		}
	}

	public static void addMainVertex(CustomHitBox hitbox, int side, double heightToAdd, Tessellator tessellator) {

		if (!hitbox.drawSide[side]) {
			if (!hitbox.drawSide[4]) {
				// Draw Main Line
				tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ);
				tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
			} else {
				// Draw Main Line
				tessellator.addVertex(hitbox.minX - hitbox.sideLength[4], hitbox.minY + heightToAdd, hitbox.minZ);
				tessellator.addVertex(hitbox.minX - hitbox.sideLength[4], hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
				// Draw North Side Line
				tessellator.addVertex(hitbox.minX - hitbox.sideLength[4], hitbox.minY + heightToAdd, hitbox.minZ);
				tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ);
				// Draw South Side Line
				tessellator.addVertex(hitbox.minX - hitbox.sideLength[4], hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
				tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
			}
			if (!hitbox.drawSide[5]) {
				// Draw Main Line
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ);
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
			} else {
				// Draw Main Line
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth + hitbox.sideLength[5], hitbox.minY + heightToAdd, hitbox.minZ);
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth + hitbox.sideLength[5], hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
				// Draw North Side Line
				tessellator.addVertex(hitbox.minX + hitbox.sideLength[5] + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ);
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ);
				// Draw South Side Line
				tessellator.addVertex(hitbox.minX + hitbox.sideLength[5] + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + +hitbox.middleWidth);
			}

			if (!hitbox.drawSide[2]) {
				// Draw Main Line
				tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ);
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ);
			} else {
				// Draw Main Line
				tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ - hitbox.sideLength[2]);
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ - hitbox.sideLength[2]);
				// Draw North Side Line
				tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ - hitbox.sideLength[2]);
				tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ);
				// Draw South Side Line
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ - hitbox.sideLength[2]);
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ);

			}
			if (!hitbox.drawSide[3]) {
				// Draw Main Line
				tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
			} else {
				// Draw Main Line
				tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth);
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth);
				// Draw North Side Line
				tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth);
				tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
				// Draw South Side Line
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth);
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
			}
		} else {
			if (hitbox.drawSide[4]) {
				// Draw Main Line
				tessellator.addVertex(hitbox.minX - hitbox.sideLength[4], hitbox.minY + heightToAdd, hitbox.minZ);
				tessellator.addVertex(hitbox.minX - hitbox.sideLength[4], hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
				// Draw North Side Line
				tessellator.addVertex(hitbox.minX - hitbox.sideLength[4], hitbox.minY + heightToAdd, hitbox.minZ);
				tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ);
				// Draw South Side Line
				tessellator.addVertex(hitbox.minX - hitbox.sideLength[4], hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
				tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
			}
			if (hitbox.drawSide[5]) {
				// Draw Main Line
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth + hitbox.sideLength[5], hitbox.minY + heightToAdd, hitbox.minZ);
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth + hitbox.sideLength[5], hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
				// Draw North Side Line
				tessellator.addVertex(hitbox.minX + hitbox.sideLength[5] + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ);
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ);
				// Draw South Side Line
				tessellator.addVertex(hitbox.minX + hitbox.sideLength[5] + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + +hitbox.middleWidth);
			}

			if (hitbox.drawSide[2]) {
				// Draw Main Line
				tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ - hitbox.sideLength[2]);
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ - hitbox.sideLength[2]);
				// Draw North Side Line
				tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ - hitbox.sideLength[2]);
				tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ);
				// Draw South Side Line
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ - hitbox.sideLength[2]);
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ);

			}
			if (hitbox.drawSide[3]) {
				// Draw Main Line
				tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth);
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth);
				// Draw North Side Line
				tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth);
				tessellator.addVertex(hitbox.minX, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
				// Draw South Side Line
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.sideLength[3] + hitbox.middleWidth);
				tessellator.addVertex(hitbox.minX + hitbox.middleDepth, hitbox.minY + heightToAdd, hitbox.minZ + hitbox.middleWidth);
			}
		}
	}

}
