package cofh.core.particles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public abstract class ParticleIcon extends ParticleBase {
	protected ParticleIcon(double x, double y, double z, double motX, double motY, double motZ, float size, int life, ResourceLocation location) {
		super(x, y, z, motX, motY, motZ, size, life, location);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(Tessellator tessellator, double partialTicks) {
		IIcon icon = getIcon();
		renderParticle(tessellator, partialTicks, size, icon.getMinU(), icon.getMaxU(), icon.getMinV(), icon.getMaxV());
	}

	@SideOnly(Side.CLIENT)
	public abstract IIcon getIcon();
}
