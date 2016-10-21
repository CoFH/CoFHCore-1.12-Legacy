package cofh.core.render.customcharrendering;

import cofh.core.render.CoFHFontRenderer;
import cofh.core.render.IconRegistry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderIcon implements ICustomCharRenderer {

    public final char underlyingCharacter;
    private final ResourceLocation textureSheet;
    private final String icon;

    public RenderIcon(char underlyingCharacter, ResourceLocation textureSheet, String icon) {

        this.underlyingCharacter = underlyingCharacter;
        this.textureSheet = textureSheet;
        this.icon = icon;
    }

    @Override
    public float renderChar(char letter, boolean italicFlag, float x, float y, CoFHFontRenderer fontRenderer) {

        TextureAtlasSprite icon = IconRegistry.getIcon(this.icon);
        if (icon == null) {
            return 0;
        }

        GL11.glColor4f(1, 1, 1, 1);
        fontRenderer.bindTexture(textureSheet);

        float u = icon.getMinU();
        float v = icon.getMinV();
        float w = icon.getMaxU() - u;
        float h = icon.getMaxV() - v;

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
    public int getCharWidth(char letter, CoFHFontRenderer coFHFontRender) {

        TextureAtlasSprite icon = IconRegistry.getIcon(this.icon);
        if (icon == null) {
            return 0;
        }

        float w = icon.getMaxU() - icon.getMinU();
        float h = icon.getMaxV() - icon.getMinV();

        return (int) Math.ceil(w / h * 8 - 0.0002f);
    }

    public static void addRenderer(char c, ResourceLocation texture, String icon, CoFHFontRenderer fontRenderer) {

        fontRenderer.renderOverrides.put(c, new RenderIcon(c, texture, icon));
    }

}
