package cofh.render;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;

import org.lwjgl.opengl.GL11;

public class RenderItemUtils {

	public static RenderItem itemRenderer = new RenderItem();
	protected static RenderBlocks renderBlocks = new RenderBlocks();

	public static void renderItemStack(int xPos, int yPos, float tickPlace, ItemStack stack, Minecraft mc) {

		if (stack != null) {
			float subTickAnimtation = stack.animationsToGo - tickPlace;

			if (subTickAnimtation > 0.0F) {
				GL11.glPushMatrix();
				float skew = 1.0F + subTickAnimtation / 5.0F;
				GL11.glTranslatef(xPos + 8, yPos + 12, 0.0F);
				GL11.glScalef(1.0F / skew, (skew + 1.0F) / 2.0F, 1.0F);
				GL11.glTranslatef(-(xPos + 8), -(yPos + 12), 0.0F);
			}
			itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, stack, xPos, yPos);
			if (subTickAnimtation > 0.0F) {
				GL11.glPopMatrix();
			}
			itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, stack, xPos, yPos);
		}
	}

	public static void renderItemStackAtScale(float xPos, float yPos, float tickPlace, ItemStack stack, Minecraft mc, float scale, boolean renderStackSize) {

		if (stack != null) {
			if (!renderStackSize) {
				stack = stack.copy();
				stack.stackSize = 1;
			}
			float subTickAnimtation = stack.animationsToGo - tickPlace;
			itemRenderer.zLevel = 500;
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_DEPTH_TEST);

			if (subTickAnimtation > 0.0F) {
				float skew = scale + subTickAnimtation / 5.0F;
				GL11.glTranslatef(xPos + 8, yPos + 12, 0.0F);
				GL11.glScalef(scale / skew, (skew + scale) / 2.0F, scale);
				GL11.glTranslatef(-(xPos + 8), -(yPos + 12), 0.0F);
			} else {
				GL11.glScalef(scale, scale, scale);
			}
			renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, stack, xPos, yPos);
			// itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, stack, xPos, yPos);
			itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, stack, (int) xPos, (int) yPos);
			GL11.glPopMatrix();
			itemRenderer.zLevel = 0;
		}
	}

	public static void renderItemAndEffectIntoGUI(FontRenderer font, TextureManager texture, ItemStack stack, float xPos, float yPos) {

		if (stack != null) {
			if (!ForgeHooksClient.renderInventoryItem(renderBlocks, texture, stack, itemRenderer.renderWithColor, itemRenderer.zLevel, xPos, yPos)) {
				renderItemIntoGUI(font, texture, stack, xPos, yPos, true);
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
			GL11.glTranslatef(xPos - 2, yPos + 3, -3.0F + itemRenderer.zLevel);
			GL11.glScalef(10.0F, 10.0F, 10.0F);
			GL11.glTranslatef(1.0F, 0.5F, 1.0F);
			GL11.glScalef(1.0F, 1.0F, -1.0F); // ??? what's going on with the scale and translation
			GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);

			if (itemRenderer.renderWithColor) {
				color = item.getColorFromItemStack(stack, 0);
				red = (color >> 16 & 255) / 255.0F;
				green = (color >> 8 & 255) / 255.0F;
				blue = (color & 255) / 255.0F;

				GL11.glColor4f(red, green, blue, 1.0F);
			}

			GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			renderBlocks.useInventoryTint = itemRenderer.renderWithColor;
			renderBlocks.renderBlockAsItem(block, meta, 1.0F);
			renderBlocks.useInventoryTint = true;
			GL11.glPopMatrix();
		} else if (item.requiresMultipleRenderPasses()) {
			GL11.glDisable(GL11.GL_LIGHTING);

			ResourceLocation texture = stack.getItemSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture;
			manager.bindTexture(texture);
			for (int pass = 0, e = item.getRenderPasses(meta); pass < e; ++pass) {
				icon = item.getIcon(stack, pass);

				if (itemRenderer.renderWithColor) {
					color = item.getColorFromItemStack(stack, pass);
					red = (color >> 16 & 255) / 255.0F;
					green = (color >> 8 & 255) / 255.0F;
					blue = (color & 255) / 255.0F;

					GL11.glColor4f(red, green, blue, 1.0F);
				}

				renderIcon(xPos, yPos, icon, 16, 16);

				if (stack.hasEffect(pass)) {
					renderEffect(manager, xPos, yPos);
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

			if (itemRenderer.renderWithColor) {
				color = item.getColorFromItemStack(stack, 0);
				red = (color >> 16 & 255) / 255.0F;
				green = (color >> 8 & 255) / 255.0F;
				blue = (color & 255) / 255.0F;

				GL11.glColor4f(red, green, blue, 1.0F);
			}

			renderIcon(xPos, yPos, icon, 16, 16);
			GL11.glEnable(GL11.GL_LIGHTING);

			if (stack.hasEffect(0)) {
				renderEffect(manager, xPos, yPos);
			}
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

	private static void renderEffect(TextureManager manager, float x, float y) {

		GL11.glDepthFunc(GL11.GL_GREATER);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDepthMask(false);
		manager.bindTexture(RES_ITEM_GLINT);
		itemRenderer.zLevel -= 50.0F;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_DST_COLOR);
		GL11.glColor4f(0.5F, 0.25F, 0.8F, 1.0F);
		renderGlint(x * 431278612 + y * 32178161, x - 2, y - 2, 20, 20);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true);
		itemRenderer.zLevel += 50.0F;
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
	}

	private static void renderGlint(float seedOrSomething, float x, float y, int u, int v) {

		Tessellator tessellator = Tessellator.instance;
		float uScale = 1 / 256f;
		float vScale = 1 / 256f;
		float s = 4.0F; // skew
		GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
		for (int i = 0; i < 2; ++i) {
			float uOffset = Minecraft.getSystemTime() % (3000 + i * 1873) / (3000F + i * 1873) * 256F;
			float vOffset = 0.0F;

			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(x + 0, y + v, itemRenderer.zLevel, (uOffset + 0 + v * s) * uScale, (vOffset + v) * vScale);
			tessellator.addVertexWithUV(x + u, y + v, itemRenderer.zLevel, (uOffset + u + v * s) * uScale, (vOffset + v) * vScale);
			tessellator.addVertexWithUV(x + u, y + 0, itemRenderer.zLevel, (uOffset + u + 0 * 0) * uScale, (vOffset + 0) * vScale);
			tessellator.addVertexWithUV(x + 0, y + 0, itemRenderer.zLevel, (uOffset + 0 + 0 * 0) * uScale, (vOffset + 0) * vScale);
			tessellator.draw();
			s = -1.0F;
		}
	}

	public static void renderIcon(float x, float y, IIcon icon, int w, int h) {

		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + 0, y + h, itemRenderer.zLevel, icon.getMinU(), icon.getMaxV());
		tessellator.addVertexWithUV(x + w, y + h, itemRenderer.zLevel, icon.getMaxU(), icon.getMaxV());
		tessellator.addVertexWithUV(x + w, y + 0, itemRenderer.zLevel, icon.getMaxU(), icon.getMinV());
		tessellator.addVertexWithUV(x + 0, y + 0, itemRenderer.zLevel, icon.getMinU(), icon.getMinV());
		tessellator.draw();
	}

}
