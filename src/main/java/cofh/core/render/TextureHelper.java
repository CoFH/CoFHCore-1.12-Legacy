package cofh.core.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

public class TextureHelper {

	public static TextureMap getTextureMap() {

		return Minecraft.getMinecraft().getTextureMapBlocks();
	}

	public static TextureAtlasSprite getTexture(String location) {

		return getTextureMap().getAtlasSprite(location);
	}

	public static TextureAtlasSprite getTexture(ResourceLocation location) {

		return getTexture(location.toString());
	}

	public static TextureAtlasSprite getMissingSprite() {

		return getTextureMap().getMissingSprite();
	}

}
