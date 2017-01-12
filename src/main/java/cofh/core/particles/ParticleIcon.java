package cofh.core.particles;

import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ParticleIcon extends ParticleBase {

	protected ParticleIcon(double x, double y, double z, double motX, double motY, double motZ, float size, int life, ResourceLocation location) {

		super(x, y, z, motX, motY, motZ, size, life, location);
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void render(VertexBuffer buffer, double partialTicks) {

		TextureAtlasSprite icon = getIcon();
		renderParticle(buffer, partialTicks, size, icon.getMinU(), icon.getMaxU(), icon.getMinV(), icon.getMaxV());
	}

	@SideOnly (Side.CLIENT)
	public abstract TextureAtlasSprite getIcon();
}
