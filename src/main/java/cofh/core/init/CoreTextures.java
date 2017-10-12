package cofh.core.init;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

public class CoreTextures {

	private CoreTextures() {

	}

	public static void registerIcons(TextureMap map) {

		textureMap = map;

		ICON_ACCESS_PUBLIC = register(ICONS_ + "icon_access_public");
		ICON_ACCESS_TEAM = register(ICONS_ + "icon_access_team");
		ICON_ACCESS_FRIENDS = register(ICONS_ + "icon_access_friends");
		ICON_ACCESS_PRIVATE = register(ICONS_ + "icon_access_private");

		ICON_ACCEPT = register(ICONS_ + "icon_accept");
		ICON_ACCEPT_INACTIVE = register(ICONS_ + "icon_accept_inactive");
		ICON_AUGMENT = register(ICONS_ + "icon_augment");
		ICON_BUTTON = register(ICONS_ + "icon_button");
		ICON_BUTTON_HIGHLIGHT = register(ICONS_ + "icon_button_highlight");
		ICON_BUTTON_INACTIVE = register(ICONS_ + "icon_button_inactive");
		ICON_CANCEL = register(ICONS_ + "icon_cancel");
		ICON_CANCEL_INACTIVE = register(ICONS_ + "icon_cancel_inactive");
		ICON_CONFIG = register(ICONS_ + "icon_config");
		ICON_ENERGY = register(ICONS_ + "icon_energy");
		ICON_NOPE = register(ICONS_ + "icon_nope");
		ICON_INFORMATION = register(ICONS_ + "icon_information");
		ICON_STEAM = register(ICONS_ + "icon_steam");
		ICON_TUTORIAL = register(ICONS_ + "icon_tutorial");

		ICON_INPUT = register(ICONS_ + "icon_input");
		ICON_OUTPUT = register(ICONS_ + "icon_output");

		ICON_REDSTONE_OFF = register(ICONS_ + "icon_redstone_off");
		ICON_REDSTONE_ON = register(ICONS_ + "icon_redstone_on");

		ICON_RS_TORCH_OFF = register(ICONS_ + "icon_rs_torch_off");
		ICON_RS_TORCH_ON = register(ICONS_ + "icon_rs_torch_on");

		ICON_ARROW_DOWN = register(ICONS_ + "icon_arrow_down");
		ICON_ARROW_DOWN_INACTIVE = register(ICONS_ + "icon_arrow_down_inactive");

		ICON_ARROW_UP = register(ICONS_ + "icon_arrow_up");
		ICON_ARROW_UP_INACTIVE = register(ICONS_ + "icon_arrow_up_inactive");
	}

	/* HELPERS */
	private static TextureMap textureMap;

	private static TextureAtlasSprite register(String sprite) {

		return textureMap.registerSprite(new ResourceLocation(sprite));
	}

	private static final String ICONS_ = "cofh:gui/icons/";

	/* REFERENCES */
	public static TextureAtlasSprite ICON_ACCESS_PUBLIC;
	public static TextureAtlasSprite ICON_ACCESS_TEAM;
	public static TextureAtlasSprite ICON_ACCESS_FRIENDS;
	public static TextureAtlasSprite ICON_ACCESS_PRIVATE;

	public static TextureAtlasSprite ICON_ACCEPT;
	public static TextureAtlasSprite ICON_ACCEPT_INACTIVE;
	public static TextureAtlasSprite ICON_AUGMENT;
	public static TextureAtlasSprite ICON_BUTTON;
	public static TextureAtlasSprite ICON_BUTTON_HIGHLIGHT;
	public static TextureAtlasSprite ICON_BUTTON_INACTIVE;
	public static TextureAtlasSprite ICON_CANCEL;
	public static TextureAtlasSprite ICON_CANCEL_INACTIVE;
	public static TextureAtlasSprite ICON_CONFIG;
	public static TextureAtlasSprite ICON_ENERGY;
	public static TextureAtlasSprite ICON_NOPE;
	public static TextureAtlasSprite ICON_INFORMATION;
	public static TextureAtlasSprite ICON_STEAM;
	public static TextureAtlasSprite ICON_TUTORIAL;

	public static TextureAtlasSprite ICON_INPUT;
	public static TextureAtlasSprite ICON_OUTPUT;

	public static TextureAtlasSprite ICON_REDSTONE_OFF;
	public static TextureAtlasSprite ICON_REDSTONE_ON;

	public static TextureAtlasSprite ICON_RS_TORCH_OFF;
	public static TextureAtlasSprite ICON_RS_TORCH_ON;

	public static TextureAtlasSprite ICON_ARROW_DOWN;
	public static TextureAtlasSprite ICON_ARROW_DOWN_INACTIVE;

	public static TextureAtlasSprite ICON_ARROW_UP;
	public static TextureAtlasSprite ICON_ARROW_UP_INACTIVE;

}
