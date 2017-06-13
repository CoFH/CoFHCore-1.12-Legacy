package cofh.core.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly (Side.CLIENT)
public class CustomEffectRenderer extends ParticleManager {

	public CustomEffectRenderer() {

		super(Minecraft.getMinecraft().world, Minecraft.getMinecraft().renderEngine);
	}

	@Override
	public void addEffect(Particle p) {

	}

}
