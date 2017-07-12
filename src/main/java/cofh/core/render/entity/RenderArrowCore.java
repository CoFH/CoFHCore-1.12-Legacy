package cofh.core.render.entity;

import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderArrowCore extends RenderArrow {

	ResourceLocation res;

	public RenderArrowCore(RenderManager renderManagerIn, ResourceLocation res) {

		super(renderManagerIn);
		this.res = res;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {

		return res;
	}

}
