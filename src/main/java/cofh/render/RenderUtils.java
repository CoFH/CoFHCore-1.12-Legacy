package cofh.render;

import codechicken.lib.colour.Colour;
import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.uv.IconTransformation;
import codechicken.lib.render.uv.UV;
import codechicken.lib.vec.Vector3;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

public class RenderUtils {

	public static class ScaledIconTransformation extends IconTransformation {

		double su = 0.0F;
		double sv = 0.0F;

		public ScaledIconTransformation(IIcon icon) {

			super(icon);
		}

		public ScaledIconTransformation(IIcon icon, double scaleu, double scalev) {

			super(icon);

			su = scaleu;
			sv = scalev;
		}

		@Override
		public void apply(UV texcoord) {

			texcoord.u = icon.getInterpolatedU(texcoord.u % 2 * 16) + su * (icon.getMaxU() - icon.getMinU());
			texcoord.v = icon.getInterpolatedV(texcoord.v % 2 * 16) + sv * (icon.getMaxV() - icon.getMinV());
		}
	}

	public static float[][] angleBaseYNeg = new float[6][3];
	public static float[][] angleBaseYPos = new float[6][3];
	public static float[][] angleBaseXPos = new float[6][3];
	public static final float factor = 1F / 16F;

	public static final int[] facingAngle = { 0, 0, 180, 0, 90, -90 };

	public static ScaledIconTransformation[] renderTransformations = new ScaledIconTransformation[4];

	static {
		renderTransformations[0] = new ScaledIconTransformation(Blocks.stone.getIcon(0, 0));
		renderTransformations[1] = new ScaledIconTransformation(Blocks.stone.getIcon(0, 0), -1F, 0F);
		renderTransformations[2] = new ScaledIconTransformation(Blocks.stone.getIcon(0, 0), 0F, -1F);
		renderTransformations[3] = new ScaledIconTransformation(Blocks.stone.getIcon(0, 0), -1F, -1F);
	}

	public static Vector3 renderVector = new Vector3();

	public static ScaledIconTransformation getIconTransformation(IIcon icon) {

		if (icon != null) {
			renderTransformations[0].icon = icon;
		}
		return renderTransformations[0];
	}

	// public static ScaledIconTransformation getIconTransformation(Icon icon, int scaleType) {
	//
	// renderTransformations[scaleType].icon = icon;
	// return renderTransformations[scaleType];
	// }

	public static Vector3 getRenderVector(double x, double y, double z) {

		renderVector.x = x;
		renderVector.y = y;
		renderVector.z = z;

		return renderVector;
	}

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

	public static int getFluidRenderColor(FluidStack fluid) {

		return fluid.getFluid().getColor(fluid);
	}

	public static void setFluidRenderColor(FluidStack fluid) {

		CCRenderState.setColour(0xFF | (fluid.getFluid().getColor(fluid) << 8));
	}

	public static void preItemRender() {

		TextureUtil.func_147950_a(false, false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

		CCRenderState.reset();
		CCRenderState.pullLightmap();
		CCRenderState.useNormals = true;
	}

	public static void postItemRender() {

		CCRenderState.useNormals = false;

		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		TextureUtil.func_147945_b();
	}

	public static void preWorldRender(IBlockAccess world, int x, int y, int z) {

		CCRenderState.reset();
		CCRenderState.setColour(0xFFFFFFFF);
		CCRenderState.setBrightness(world, x, y, z);
	}

	public static void renderMask(IIcon maskIcon, IIcon subIcon, Colour maskColor, ItemRenderType type) {

		if (maskIcon == null || subIcon == null) {
			return;
		}
		if (maskColor == null) {
			maskColor = new ColourRGBA(0xFFFFFFFF);
		}
		maskColor.glColour();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glDisable(GL11.GL_CULL_FACE);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setNormal(0, 0, 1);
		if (type.equals(ItemRenderType.INVENTORY)) {
			preRenderIconInv(maskIcon, 0.001);
		} else {
			preRenderIconWorld(maskIcon, 0.001);
		}
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0, 0, -1);
		if (type.equals(ItemRenderType.INVENTORY)) {
			preRenderIconInv(maskIcon, -0.0635);
		} else {
			preRenderIconWorld(maskIcon, -0.0635);
		}
		tessellator.draw();

		RenderHelper.setBlockTextureSheet();

		GL11.glDepthFunc(GL11.GL_EQUAL);
		GL11.glDepthMask(false);

		tessellator.startDrawingQuads();
		tessellator.setNormal(0, 0, 1);
		if (type.equals(ItemRenderType.INVENTORY)) {
			preRenderIconInv(subIcon, 0.001);
		} else {
			preRenderIconWorld(subIcon, 0.001);
		}
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0, 0, -1);
		if (type.equals(ItemRenderType.INVENTORY)) {
			preRenderIconInv(subIcon, -0.0635);
		} else {
			preRenderIconWorld(subIcon, -0.0635);
		}
		tessellator.draw();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glColor4f(1, 1, 1, 1);
	}

	public static void preRenderIconWorld(IIcon icon, double z) {

		Tessellator.instance.addVertexWithUV(0, 1, z, icon.getMinU(), icon.getMaxV());
		Tessellator.instance.addVertexWithUV(1, 1, z, icon.getMaxU(), icon.getMaxV());
		Tessellator.instance.addVertexWithUV(1, 0, z, icon.getMaxU(), icon.getMinV());
		Tessellator.instance.addVertexWithUV(0, 0, z, icon.getMinU(), icon.getMinV());
	}

	public static void preRenderIconInv(IIcon icon, double z) {

		Tessellator.instance.addVertexWithUV(0, 16, z, icon.getMinU(), icon.getMaxV());
		Tessellator.instance.addVertexWithUV(16, 16, z, icon.getMaxU(), icon.getMaxV());
		Tessellator.instance.addVertexWithUV(16, 0, z, icon.getMaxU(), icon.getMinV());
		Tessellator.instance.addVertexWithUV(0, 0, z, icon.getMinU(), icon.getMinV());
	}

}
