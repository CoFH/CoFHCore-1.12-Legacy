package cofh.core.render;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * Implement this interface on Tile Entities which can change their block's texture based on the current render pass. The block must defer the call to its Tile Entity.
 *
 * This is only used in Configuration Tabs and blocks which do not require advanced rendering. :)
 *
 * @author King Lemming
 */
public interface ISidedTexture {

	int getNumPasses();

	/**
	 * Returns the icon to use for a given side and render pass.
	 *
	 * @param side  Block side to get the texture for.
	 * @param pass  Render pass.
	 * @return The icon to use.
	 */
	TextureAtlasSprite getTexture(int side, int pass);

}
