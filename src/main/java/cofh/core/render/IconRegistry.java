package cofh.core.render;

import cofh.lib.render.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

/**
 * Convenient String to Icon map. Allows easy reuse of Icons.
 *
 * @author King Lemming
 */
@Deprecated
public class IconRegistry {

	private static HashMap<String, TextureAtlasSprite> icons = new HashMap<String, TextureAtlasSprite>();

	private IconRegistry() {

	}

	public static void addIcon(String iconName, ResourceLocation iconLocation, TextureMap ir) {

		addIcon(iconName, ir.registerSprite(iconLocation));
	}

	public static void addIcon(String iconName, String iconLocation, TextureMap ir) {

		addIcon(iconName, ir.registerSprite(new ResourceLocation(iconLocation)));
	}

	public static void addIcon(String iconName, TextureAtlasSprite icon) {

		icons.put(iconName, icon);
	}

	public static TextureAtlasSprite getIcon(String iconName) {

		if (!icons.containsKey(iconName)) {
			return RenderHelper.textureMap().getMissingSprite();
		}
		return icons.get(iconName);
	}

	public static TextureAtlasSprite getIcon(String iconName, int iconOffset) {

		return getIcon(iconName + iconOffset);
	}

}
