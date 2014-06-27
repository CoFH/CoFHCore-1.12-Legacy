package cofh.render.hitbox;

import net.minecraft.entity.player.EntityPlayer;

public interface ICustomHitBox {

	boolean shouldRenderCustomHitBox(int subHit, EntityPlayer thePlayer);

	CustomHitBox getCustomHitBox(int subHit, EntityPlayer thePlayer);
}
