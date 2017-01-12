package cofh.core.particles;

import net.minecraft.client.renderer.VertexBuffer;

public class ParticleMCSheet extends ParticleBase {

	float particleTextureIndexX;
	float particleTextureIndexY;
	final static float SPRITE_SIZE = 0.0624375F;

	protected ParticleMCSheet(double x, double y, double z, double motX, double motY, double motZ, float size, int life, int spriteIndex) {

		super(x, y, z, motX, motY, motZ, size, life, ParticleBase.MC_PARTICLES);
		setParticleIndex(spriteIndex);
	}

	public void setParticleIndex(int spriteIndex) {

		particleTextureIndexX = (spriteIndex % 16) / 16.0F;
		particleTextureIndexY = (spriteIndex / 16) / 16.0F;
	}

	@Override
	public void render(VertexBuffer buffer, double partialTicks) {

		renderParticle(buffer, partialTicks, size, particleTextureIndexX, particleTextureIndexX + SPRITE_SIZE, particleTextureIndexY, particleTextureIndexY + SPRITE_SIZE);
	}
}
