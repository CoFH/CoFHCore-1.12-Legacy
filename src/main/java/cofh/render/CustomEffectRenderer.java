package cofh.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;

@SideOnly(Side.CLIENT)
public class CustomEffectRenderer extends EffectRenderer
{
	public CustomEffectRenderer()
	{
		super(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().renderEngine);
	}

	@Override
	public void addEffect(EntityFX p)
	{
	}
}
