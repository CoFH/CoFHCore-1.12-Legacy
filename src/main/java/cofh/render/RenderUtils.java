package cofh.render;

import cofh.repack.codechicken.lib.colour.Colour;
import cofh.repack.codechicken.lib.colour.ColourRGBA;
import cofh.repack.codechicken.lib.render.CCRenderState;
import cofh.repack.codechicken.lib.render.uv.IconTransformation;
import cofh.repack.codechicken.lib.render.uv.UV;
import cofh.repack.codechicken.lib.vec.Vector3;
import cofh.util.BlockHelper;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
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

	public static final RenderItem renderItem = new RenderItem();
	public static final RenderBlocks renderBlocks = new RenderBlocks();

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
			preRenderIconInv(maskIcon, 10);
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
			preRenderIconInv(subIcon, 10);
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

	public static void renderItemStack(int xPos, int yPos, float tickPlace, ItemStack stack, Minecraft mc) {

		if (stack != null) {
			float subTickAnimation = stack.animationsToGo - tickPlace;

			if (subTickAnimation > 0.0F) {
				GL11.glPushMatrix();
				float skew = 1.0F + subTickAnimation / 5.0F;
				GL11.glTranslatef(xPos + 8, yPos + 12, 0.0F);
				GL11.glScalef(1.0F / skew, (skew + 1.0F) / 2.0F, 1.0F);
				GL11.glTranslatef(-(xPos + 8), -(yPos + 12), 0.0F);
			}
			renderItem.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, stack, xPos, yPos);
			if (subTickAnimation > 0.0F) {
				GL11.glPopMatrix();
			}
			renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, stack, xPos, yPos);
		}
	}

	public static void renderItemStackAtScale(float xPos, float yPos, float tickPlace, ItemStack stack, Minecraft mc, float scale, boolean renderStackSize) {

		if (stack != null) {
			if (!renderStackSize) {
				stack = stack.copy();
				stack.stackSize = 1;
			}
			float subTickAnimation = stack.animationsToGo - tickPlace;
			renderItem.zLevel = 500;
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_DEPTH_TEST);

			if (subTickAnimation > 0.0F) {
				float skew = scale + subTickAnimation / 5.0F;
				GL11.glTranslatef(xPos + 8, yPos + 12, 0.0F);
				GL11.glScalef(scale / skew, (skew + scale) / 2.0F, scale);
				GL11.glTranslatef(-(xPos + 8), -(yPos + 12), 0.0F);
			} else {
				GL11.glScalef(scale, scale, scale);
			}
			RenderUtils.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, stack, xPos, yPos);
			// itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, stack, xPos, yPos);
			renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, stack, (int) xPos, (int) yPos);
			GL11.glPopMatrix();
			renderItem.zLevel = 0;
		}
	}

	public static void renderItemAndEffectIntoGUI(FontRenderer font, TextureManager texture, ItemStack stack, float xPos, float yPos) {

		if (stack != null) {
			if (!ForgeHooksClient.renderInventoryItem(renderBlocks, texture, stack, renderItem.renderWithColor, renderItem.zLevel, xPos, yPos)) {
				RenderUtils.renderItemIntoGUI(font, texture, stack, xPos, yPos, true);
			}
		}
	}

	public static void renderItemIntoGUI(FontRenderer font, TextureManager manager, ItemStack stack, float xPos, float yPos, boolean renderEffect) {

		Item item = stack.getItem();
		int meta = stack.getItemDamage();
		IIcon icon = stack.getIconIndex();
		int color;

		float red;
		float green;
		float blue;

		Block block = Block.getBlockFromItem(item);
		if (stack.getItemSpriteNumber() == 0 && block != null && RenderBlocks.renderItemIn3d(block.getRenderType())) {
			manager.bindTexture(TextureMap.locationBlocksTexture);
			GL11.glPushMatrix();
			GL11.glTranslatef(xPos - 2, yPos + 3, -3.0F + renderItem.zLevel);
			GL11.glScalef(10.0F, 10.0F, 10.0F);
			GL11.glTranslatef(1.0F, 0.5F, 1.0F);
			GL11.glScalef(1.0F, 1.0F, -1.0F); // ??? what's going on with the scale and translation
			GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);

			if (renderItem.renderWithColor) {
				color = item.getColorFromItemStack(stack, 0);
				red = (color >> 16 & 255) / 255.0F;
				green = (color >> 8 & 255) / 255.0F;
				blue = (color & 255) / 255.0F;

				GL11.glColor4f(red, green, blue, 1.0F);
			}

			GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			renderBlocks.useInventoryTint = renderItem.renderWithColor;
			renderBlocks.renderBlockAsItem(block, meta, 1.0F);
			renderBlocks.useInventoryTint = true;
			GL11.glPopMatrix();
		} else if (item.requiresMultipleRenderPasses()) {
			GL11.glDisable(GL11.GL_LIGHTING);

			ResourceLocation texture = stack.getItemSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture;
			manager.bindTexture(texture);
			for (int pass = 0, e = item.getRenderPasses(meta); pass < e; ++pass) {
				icon = item.getIcon(stack, pass);

				if (renderItem.renderWithColor) {
					color = item.getColorFromItemStack(stack, pass);
					red = (color >> 16 & 255) / 255.0F;
					green = (color >> 8 & 255) / 255.0F;
					blue = (color & 255) / 255.0F;

					GL11.glColor4f(red, green, blue, 1.0F);
				}

				RenderHelper.renderIcon(xPos, yPos, renderItem.zLevel, icon, 16, 16);

				if (stack.hasEffect(pass)) {
					RenderUtils.renderEffect(manager, xPos, yPos);
					manager.bindTexture(texture);
				}
			}

			GL11.glEnable(GL11.GL_LIGHTING);
		} else {
			GL11.glDisable(GL11.GL_LIGHTING);
			ResourceLocation resourcelocation = manager.getResourceLocation(stack.getItemSpriteNumber());
			manager.bindTexture(resourcelocation);

			if (icon == null) {
				icon = ((TextureMap) Minecraft.getMinecraft().getTextureManager().getTexture(resourcelocation)).getAtlasSprite("missingno");
			}

			if (renderItem.renderWithColor) {
				color = item.getColorFromItemStack(stack, 0);
				red = (color >> 16 & 255) / 255.0F;
				green = (color >> 8 & 255) / 255.0F;
				blue = (color & 255) / 255.0F;

				GL11.glColor4f(red, green, blue, 1.0F);
			}

			RenderHelper.renderIcon(xPos, yPos, renderItem.zLevel, icon, 16, 16);
			GL11.glEnable(GL11.GL_LIGHTING);

			if (stack.hasEffect(0)) {
				RenderUtils.renderEffect(manager, xPos, yPos);
			}
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	public static void renderEffect(TextureManager manager, float x, float y) {

		GL11.glDepthFunc(GL11.GL_GREATER);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDepthMask(false);

		manager.bindTexture(RenderHelper.MC_ITEM_GLINT);
		renderItem.zLevel -= 50.0F;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_DST_COLOR);
		GL11.glColor4f(0.5F, 0.25F, 0.8F, 1.0F);
		RenderUtils.renderGlint(x - 2, y - 2, 20, 20);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true);
		renderItem.zLevel += 50.0F;

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
	}

	public static void renderGlint(float x, float y, int u, int v) {

		Tessellator tessellator = RenderHelper.tessellator();
		float uScale = 1 / 256f;
		float vScale = 1 / 256f;
		float s = 4.0F; // skew
		GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
		for (int i = 0; i < 2; i++) {
			float uOffset = Minecraft.getSystemTime() % (3000 + i * 1873) / (3000F + i * 1873) * 256F;
			float vOffset = 0.0F;

			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(x + 0, y + v, renderItem.zLevel, (uOffset + 0 + v * s) * uScale, (vOffset + v) * vScale);
			tessellator.addVertexWithUV(x + u, y + v, renderItem.zLevel, (uOffset + u + v * s) * uScale, (vOffset + v) * vScale);
			tessellator.addVertexWithUV(x + u, y + 0, renderItem.zLevel, (uOffset + u + 0 * 0) * uScale, (vOffset + 0) * vScale);
			tessellator.addVertexWithUV(x + 0, y + 0, renderItem.zLevel, (uOffset + 0 + 0 * 0) * uScale, (vOffset + 0) * vScale);
			tessellator.draw();
			s = -1.0F;
		}
	}

	public static final void renderItemOnBlockSide(TileEntity tile, ItemStack stack, int side, double x, double y, double z) {

		if (stack == null) {
			return;
		}
		GL11.glPushMatrix();

		switch (side) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			GL11.glTranslated(x + 0.75, y + 0.875, z - RenderHelper.RENDER_OFFSET);
			break;
		case 3:
			GL11.glTranslated(x + 0.25, y + 0.875, z + 1 + RenderHelper.RENDER_OFFSET);
			GL11.glRotated(180, 0, 1, 0);
			break;
		case 4:
			GL11.glTranslated(x - RenderHelper.RENDER_OFFSET, y + 0.875, z + 0.25);
			GL11.glRotated(90, 0, 1, 0);
			break;
		case 5:
			GL11.glTranslated(x + 1 + RenderHelper.RENDER_OFFSET, y + 0.875, z + 0.75);
			GL11.glRotated(-90, 0, 1, 0);
			break;
		default:
		}
		GL11.glScaled(0.03125, 0.03125, -RenderHelper.RENDER_OFFSET);
		GL11.glRotated(180, 0, 0, 1);

		setupLight(tile, side);
		RenderHelper.enableGUIStandardItemLighting();

		if (!ForgeHooksClient.renderInventoryItem(renderBlocks, RenderHelper.engine(), stack, true, 0.0F, 0.0F, 0.0F)) {
			renderItem.renderItemIntoGUI(Minecraft.getMinecraft().fontRenderer, RenderHelper.engine(), stack, 0, 0);
		}
		GL11.glPopMatrix();
	}

	public static void setupLight(TileEntity tile, int side) {

		if (tile == null) {
			return;
		}
		int x = tile.xCoord + BlockHelper.SIDE_COORD_MOD[side][0];
		int y = tile.yCoord + BlockHelper.SIDE_COORD_MOD[side][1];
		int z = tile.zCoord + BlockHelper.SIDE_COORD_MOD[side][2];

		World world = tile.getWorldObj();

		if (world.getBlock(x, y, z).isOpaqueCube()) {
			return;
		}
		int br = world.getLightBrightnessForSkyBlocks(x, y, z, 0);
		int brX = br % 65536;
		int brY = br / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brX, brY);
	}

}
