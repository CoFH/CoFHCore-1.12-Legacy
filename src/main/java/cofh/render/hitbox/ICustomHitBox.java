package cofh.render.hitbox;

public interface ICustomHitBox {

	boolean shouldRenderCustomHitBox(int subHit);

	CustomHitBox getCustomHitBox(int subHit);
}
