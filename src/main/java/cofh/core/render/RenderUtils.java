package cofh.core.render;

import cofh.core.util.helpers.RenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class RenderUtils {

	public static float[][] angleBaseYNeg = new float[6][3];
	public static float[][] angleBaseYPos = new float[6][3];
	public static float[][] angleBaseXPos = new float[6][3];
	public static final float factor = 1F / 16F;

	public static final int[] facingAngle = { 0, 0, 180, 0, 90, -90 };

	static {
		float pi = (float) Math.PI;
		angleBaseYNeg[0][2] = pi;
		angleBaseYNeg[2][0] = -pi / 2;
		angleBaseYNeg[3][0] = pi / 2;
		angleBaseYNeg[4][2] = pi / 2;
		angleBaseYNeg[5][2] = -pi / 2;

		angleBaseYPos[1][2] = pi;
		angleBaseYPos[2][0] = pi / 2;
		angleBaseYPos[3][0] = -pi / 2;
		angleBaseYPos[4][2] = -pi / 2;
		angleBaseYPos[5][2] = pi / 2;

		angleBaseXPos[0][0] = -pi / 2;
		angleBaseXPos[1][0] = pi / 2;
		angleBaseXPos[2][1] = pi;
		angleBaseXPos[4][1] = -pi / 2;
		angleBaseXPos[5][1] = pi / 2;
	}

	public static void renderItemOnBlockSide(TileEntity tile, ItemStack stack, int side, double x, double y, double z) {

		if (stack.isEmpty()) {
			return;
		}
		GlStateManager.pushMatrix();

		switch (side) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				GlStateManager.translate(x + 0.75, y + 0.875, z + RenderHelper.RENDER_OFFSET * 145);
				break;
			case 3:
				GlStateManager.translate(x + 0.25, y + 0.875, z + 1 - RenderHelper.RENDER_OFFSET * 145);
				GlStateManager.rotate(180, 0, 1, 0);
				break;
			case 4:
				GlStateManager.translate(x + RenderHelper.RENDER_OFFSET * 145, y + 0.875, z + 0.25);
				GlStateManager.rotate(90, 0, 1, 0);
				break;
			case 5:
				GlStateManager.translate(x + 1 - RenderHelper.RENDER_OFFSET * 145, y + 0.875, z + 0.75);
				GlStateManager.rotate(-90, 0, 1, 0);
				break;
			default:
		}
		GlStateManager.scale(0.03125, 0.03125, -RenderHelper.RENDER_OFFSET);
		GlStateManager.rotate(180, 0, 0, 1);

		setupLight(tile, EnumFacing.VALUES[side]);

		RenderHelper.renderItem().renderItemAndEffectIntoGUI(stack, 0, 0);

		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GlStateManager.popMatrix();
		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
	}

	public static void setupLight(TileEntity tile, EnumFacing side) {

		if (tile == null) {
			return;
		}
		BlockPos pos = tile.getPos().offset(side);
		World world = tile.getWorld();

		if (world.getBlockState(pos).isOpaqueCube()) {
			return;
		}
		int br = world.getCombinedLight(pos, 4);
		int brX = br & 65535;
		int brY = br >>> 16;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brX, brY);
	}

}
