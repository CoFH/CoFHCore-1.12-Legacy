package cofh.core.render.customcharrendering;

import cofh.core.render.CoFHFontRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderSprite implements ICustomCharRenderer {

    public final char underlyingCharacter;
    private final ResourceLocation textureSheet;

    private final float u, v, w, h, sw, rw;
    private final int bw;

    public RenderSprite(char underlyingCharacter, ResourceLocation textureSheet, float u, float v, float w, float h) {

        this.underlyingCharacter = underlyingCharacter;
        this.textureSheet = textureSheet;
        this.u = u;
        this.v = v;
        this.w = w;
        this.h = h;

        this.sw = w / h * 7.99F;
        this.rw = w / h * 8.02F;
        this.bw = (int) Math.ceil(w / h * 8 - 0.0002f);
    }

    @Override
    public float renderChar(char letter, boolean italicFlag, float x, float y, CoFHFontRenderer fontRenderer) {

        GL11.glColor4f(1, 1, 1, 1);
        fontRenderer.bindTexture(textureSheet);

        float italicOffset = italicFlag ? 1.0F : 0.0F;
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        GL11.glTexCoord2f(u / 256.0F, v / 256.0F);
        GL11.glVertex3f(x + italicOffset, y, 0.0F);
        GL11.glTexCoord2f(u / 256.0F, (v + h) / 256.0F);
        GL11.glVertex3f(x - italicOffset, y + 7.99F, 0.0F);
        GL11.glTexCoord2f((u + w) / 256.0F, v / 256.0F);
        GL11.glVertex3f(x + sw + italicOffset, y, 0.0F);
        GL11.glTexCoord2f((u + w) / 256.0F, (v + h) / 256.0F);
        GL11.glVertex3f(x + sw - italicOffset, y + 7.99F, 0.0F);
        GL11.glEnd();

        fontRenderer.resetColor();

        return rw;
    }

    @Override
    public int getCharWidth(char letter, CoFHFontRenderer fontRenderer) {

        return bw;
    }

    public static char addRenderer(char c, ResourceLocation texture, int u, int v, int w, int h, CoFHFontRenderer fontRenderer) {

        RenderSprite renderSprite = new RenderSprite(c, texture, u, v, w, h);
        fontRenderer.renderOverrides.put(c, renderSprite);
        return c;
    }

}
