package cofh.core.render.hitbox;

import net.minecraft.entity.player.EntityPlayer;

public interface ICustomHitBox {

	boolean shouldRenderCustomHitBox(int subHit, EntityPlayer player);

	CustomHitBox getCustomHitBox(int subHit, EntityPlayer player);

}
