package cofh.core.render.font;

import cofh.core.render.FontRendererCore;
import cofh.lib.util.helpers.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.lwjgl.opengl.GL11;

public class RenderIcon implements ICustomCharRenderer {

	public final char underlyingCharacter;
	private final TextureAtlasSprite icon;

	public RenderIcon(char underlyingCharacter, TextureAtlasSprite icon) {

		this.underlyingCharacter = underlyingCharacter;
		this.icon = icon;
	}

	@Override
	public float renderChar(char letter, boolean italicFlag, float x, float y, FontRendererCore fontRenderer) {

		if (icon == null) {
			return 0;
		}

		GL11.glColor4f(1, 1, 1, 1);
		RenderHelper.setBlockTextureSheet();

		float u = this.icon.getMinU();
		float v = this.icon.getMinV();
		float w = this.icon.getMaxU() - u;
		float h = this.icon.getMaxV() - v;

		float rw = w / h * 8.02F;

		float italicOffset = italicFlag ? 1.0F : 0.0F;
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		GL11.glTexCoord2f(u, v);
		GL11.glVertex3f(x + italicOffset, y, 0.0F);
		GL11.glTexCoord2f(u, (v + h));
		GL11.glVertex3f(x - italicOffset, y + 7.99F, 0.0F);
		GL11.glTexCoord2f((u + w), v);
		GL11.glVertex3f(x + w / h * 7.99F + italicOffset, y, 0.0F);
		GL11.glTexCoord2f((u + w), (v + h));
		GL11.glVertex3f(x + w / h * 7.99F - italicOffset, y + 7.99F, 0.0F);
		GL11.glEnd();

		fontRenderer.resetColor();

		return rw;
	}

	@Override
	public int getCharWidth(char letter, FontRendererCore fontRenderer) {

		if (this.icon == null) {
			return 0;
		}

		float w = this.icon.getMaxU() - this.icon.getMinU();
		float h = this.icon.getMaxV() - this.icon.getMinV();

		return (int) Math.ceil(w / h * 8 - 0.0002f);
	}

	public static void addRenderer(char c, TextureAtlasSprite icon, FontRendererCore fontRenderer) {

		fontRenderer.renderOverrides.put(c, new RenderIcon(c, icon));
	}

}
