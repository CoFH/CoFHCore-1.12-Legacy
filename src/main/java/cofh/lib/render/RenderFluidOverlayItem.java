package cofh.lib.render;

import cofh.lib.util.helpers.ItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class RenderFluidOverlayItem implements IItemRenderer {

	private final boolean canFlip;

	public RenderFluidOverlayItem() {

		this(true);
	}

	public RenderFluidOverlayItem(boolean canFlip) {

		this.canFlip = canFlip;
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {

		return (type.ordinal() < ItemRenderType.FIRST_PERSON_MAP.ordinal());
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {

		return handleRenderType(item, type) & helper.ordinal() < ItemRendererHelper.EQUIPPED_BLOCK.ordinal();
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {

		Item item = stack.getItem();
		FluidStack fluid = null;
		if (item instanceof IFluidContainerItem) {
			IFluidContainerItem fluidItem = (IFluidContainerItem) item;
			fluid = fluidItem.getFluid(stack);
		} else if (item instanceof IFluidOverlayItem 
					&& item.getRenderPasses(ItemHelper.getItemDamage(stack)) == 2) {
			fluid = FluidContainerRegistry.getFluidForFilledItem(stack);
		}
		doRenderItem(type, stack, item, fluid);
	}

	protected void doRenderItem(ItemRenderType type, ItemStack item, Item iconItem, FluidStack fluid) {

		IIcon icon = iconItem.getIcon(item, 0);
		IIcon mask = iconItem.getIcon(item, 1);
		boolean hasFluid = fluid != null;

		IIcon fluidIcon = hasFluid ? fluid.getFluid().getIcon(fluid) : mask;
		int fluidSheet = hasFluid ? fluid.getFluid().getSpriteNumber() : 0;
		int colorMult = hasFluid ? fluid.getFluid().getColor(fluid) : 0xFFFFFF;
		boolean isFloaty = hasFluid ? fluid.getFluid().getDensity(fluid) < 0 : false;

		if (fluidIcon == null) {
			fluidIcon = Blocks.flowing_lava.getIcon(2, 0);
			fluidSheet = 0;
			colorMult = 0x3F3F3F;
		}
		GL11.glPushMatrix();

		Tessellator tessellator = Tessellator.instance;

		float iconMinX = icon.getMinU();
		float iconMaxX = icon.getMaxU();
		float iconMinY = icon.getMinV();
		float iconMaxY = icon.getMaxV();

		float maskMinX = mask.getMinU();
		float maskMaxX = mask.getMaxU();
		float maskMinY = mask.getMinV();
		float maskMaxY = mask.getMaxV();

		float fluidMinX = fluidIcon.getMinU();
		float fluidMaxX = fluidIcon.getMaxU();
		float fluidMinY = fluidIcon.getMinV();
		float fluidMaxY = fluidIcon.getMaxV();

		if (isFloaty && canFlip) {
			iconMaxY = icon.getMinV();
			iconMinY = icon.getMaxV();

			maskMaxY = mask.getMinV();
			maskMinY = mask.getMaxV();

			fluidMaxY = fluidIcon.getMinV();
			fluidMinY = fluidIcon.getMaxV();
		}
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		int texture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

		if (type == ItemRenderType.INVENTORY) {
			GL11.glDisable(GL11.GL_LIGHTING);

			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(0, 16, 0, iconMinX, iconMaxY);
			tessellator.addVertexWithUV(16, 16, 0, iconMaxX, iconMaxY);
			tessellator.addVertexWithUV(16, 0, 0, iconMaxX, iconMinY);
			tessellator.addVertexWithUV(0, 0, 0, iconMinX, iconMinY);
			tessellator.draw();

			if (hasFluid) {
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(0, 16, 0.001, maskMinX, maskMaxY);
				tessellator.addVertexWithUV(16, 16, 0.001, maskMaxX, maskMaxY);
				tessellator.addVertexWithUV(16, 0, 0.001, maskMaxX, maskMinY);
				tessellator.addVertexWithUV(0, 0, 0.001, maskMinX, maskMinY);
				tessellator.draw();

				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glDepthFunc(GL11.GL_EQUAL);
				GL11.glDepthMask(false);
				GL11.glMatrixMode(GL11.GL_TEXTURE);
				bindTexture(RenderHelper.engine(), fluidSheet);
				OpenGlHelper.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO, GL11.GL_ONE, GL11.GL_ZERO);

				tessellator.startDrawingQuads();
				tessellator.setColorOpaque_I(colorMult);
				tessellator.addVertexWithUV(0, 16, 0.001, fluidMinX, fluidMaxY);
				tessellator.addVertexWithUV(16, 16, 0.001, fluidMaxX, fluidMaxY);
				tessellator.addVertexWithUV(16, 0, 0.001, fluidMaxX, fluidMinY);
				tessellator.addVertexWithUV(0, 0, 0.001, fluidMinX, fluidMinY);
				tessellator.draw();

				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glDepthMask(true);
				GL11.glDepthFunc(GL11.GL_LEQUAL);
			}

			GL11.glEnable(GL11.GL_LIGHTING);
		} else {
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);

			if (type == ItemRenderType.ENTITY) {
				GL11.glTranslatef(0.5f, 4 / -16f, 0);
				GL11.glRotatef(180, 0, 1, 0);
			}
			ItemRenderer.renderItemIn2D(tessellator, iconMaxX, iconMinY, iconMinX, iconMaxY, icon.getIconWidth(), icon.getIconHeight(), 0.0625F);

			if (hasFluid) {
				tessellator.startDrawingQuads();
				tessellator.setNormal(0, 0, 1);
				tessellator.addVertexWithUV(0, 0, 0.001, maskMaxX, maskMaxY);
				tessellator.addVertexWithUV(1, 0, 0.001, maskMinX, maskMaxY);
				tessellator.addVertexWithUV(1, 1, 0.001, maskMinX, maskMinY);
				tessellator.addVertexWithUV(0, 1, 0.001, maskMaxX, maskMinY);
				tessellator.draw();
				tessellator.startDrawingQuads();
				tessellator.setNormal(0, 0, -1);
				tessellator.addVertexWithUV(0, 1, -0.0635, maskMinX, maskMinY);
				tessellator.addVertexWithUV(1, 1, -0.0635, maskMaxX, maskMinY);
				tessellator.addVertexWithUV(1, 0, -0.0635, maskMaxX, maskMaxY);
				tessellator.addVertexWithUV(0, 0, -0.0635, maskMinX, maskMaxY);
				tessellator.draw();

				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glDepthFunc(GL11.GL_EQUAL);
				GL11.glDepthMask(false);
				bindTexture(RenderHelper.engine(), fluidSheet);
				OpenGlHelper.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO, GL11.GL_ONE, GL11.GL_ZERO);

				tessellator.startDrawingQuads();
				tessellator.setNormal(0, 0, 1);
				tessellator.setColorOpaque_I(colorMult);
				tessellator.addVertexWithUV(0, 0, 0.001, fluidMaxX, fluidMaxY);
				tessellator.addVertexWithUV(1, 0, 0.001, fluidMinX, fluidMaxY);
				tessellator.addVertexWithUV(1, 1, 0.001, fluidMinX, fluidMinY);
				tessellator.addVertexWithUV(0, 1, 0.001, fluidMaxX, fluidMinY);
				tessellator.draw();

				tessellator.startDrawingQuads();
				tessellator.setNormal(0, 0, -1);
				tessellator.setColorOpaque_I(colorMult);
				tessellator.addVertexWithUV(0, 1, -0.0635, fluidMinX, fluidMinY);
				tessellator.addVertexWithUV(1, 1, -0.0635, fluidMaxX, fluidMinY);
				tessellator.addVertexWithUV(1, 0, -0.0635, fluidMaxX, fluidMaxY);
				tessellator.addVertexWithUV(0, 0, -0.0635, fluidMinX, fluidMaxY);
				tessellator.draw();

				GL11.glDepthMask(true);
				GL11.glDepthFunc(GL11.GL_LEQUAL);
			}
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		}

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glPopMatrix();
	}

	protected void bindTexture(TextureManager renderEngine, int spriteNumber) {

		if (spriteNumber == 0) {
			renderEngine.bindTexture(RenderHelper.MC_BLOCK_SHEET);
		} else {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, spriteNumber);
		}
	}

}
