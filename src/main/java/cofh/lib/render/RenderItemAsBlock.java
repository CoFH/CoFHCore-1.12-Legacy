package cofh.lib.render;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

/**
 * Easy way of rendering an item which should look like a block.
 *
 * @author King Lemming
 *
 */
public class RenderItemAsBlock implements IItemRenderer {

	public static RenderItemAsBlock instance = new RenderItemAsBlock();

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {

		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {

		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

		double offset = -0.5;
		if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
			offset = 0;
		} else if (type == ItemRenderType.ENTITY) {
			GL11.glScalef(0.5F, 0.5F, 0.5F);
		}
		RenderHelper.renderItemAsBlock((RenderBlocks) data[0], item, offset, offset, offset);
	}

}
