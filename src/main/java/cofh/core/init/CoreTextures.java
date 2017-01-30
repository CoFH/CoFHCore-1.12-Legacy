package cofh.core.init;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by covers1624 on 27/01/2017.
 */
public class CoreTextures {


	@SubscribeEvent
	public void stictchPre(TextureStitchEvent.Pre event) {
		TextureMap map = event.getMap();

		ICON_ACCESS_FRIENDS = register(map, ICONS_ + "icon_access_friends");
		ICON_ACCESS_TEAM  = register(map, ICONS_ + "icon_access_team");
		ICON_ACCESS_PRIVATE = register(map, ICONS_ + "icon_access_private");
		ICON_ACCESS_PUBLIC  = register(map, ICONS_ + "icon_access_public");
		ICON_ACCEPT  = register(map, ICONS_ + "icon_accept");
		ICON_ACCEPT_INACTIVE  = register(map, ICONS_ + "icon_accept_inactive");
		ICON_AUGMENT  = register(map, ICONS_ + "icon_augment");
		ICON_BUTTON  = register(map, ICONS_ + "icon_button");
		ICON_BUTTON_HIGHLIGHT  = register(map, ICONS_ + "icon_button_highlight");
		ICON_BUTTON_INACTIVE = register(map, ICONS_ + "icon_button_inactive");
		ICON_CANCEL  = register(map, ICONS_ + "icon_cancel");
		ICON_CANCEL_INACTIVE  = register(map, ICONS_ + "icon_cancel_inactive");
		ICON_CONFIG = register(map, ICONS_ + "icon_config");
		ICON_ENERGY  = register(map, ICONS_ + "icon_energy");
		ICON_NOPE  = register(map, ICONS_ + "icon_nope");
		ICON_INFORMATION = register(map, ICONS_ + "icon_information");
		ICON_TUTORIAL  = register(map, ICONS_ + "icon_tutorial");

		ICON_RS_TORCH_OFF  = register(map, ICONS_ + "icon_rs_torch_off");
		ICON_RS_TORCH_ON  = register(map, ICONS_ + "icon_rs_torch_on");

		ICON_ARROW_DOWN  = register(map, ICONS_ + "icon_arrow_down");
		ICON_ARROW_DOWN_INAVTIVE = register(map, ICONS_ + "icon_arrow_down_inactive");

		ICON_ARROW_UP = register(map, ICONS_ + "icon_arrow_up");
		ICON_ARROW_UP_INACTIVE = register(map, ICONS_ + "icon_arrow_up_inactive");
	}

	@SubscribeEvent
	public void stitchPost(TextureStitchEvent.Post event) {

		TextureMap map = event.getMap();

		ICON_GUNPOWDER = map.getAtlasSprite("minecraft:items/gunpowder");
		ICON_REDSTONE = map.getAtlasSprite("minecraft:items/redstone");
	}

	// Bouncer to make the class readable.
	private static TextureAtlasSprite register(TextureMap map, String sprite) {

		return map.registerSprite(new ResourceLocation(sprite));
	}

	private static final String ITEMS_ = "cofh:items/";
	private static final String ICONS_ = ITEMS_ + "icons/";



	public static TextureAtlasSprite ICON_ACCESS_FRIENDS;
	public static TextureAtlasSprite ICON_ACCESS_TEAM;
	public static TextureAtlasSprite ICON_ACCESS_PRIVATE;
	public static TextureAtlasSprite ICON_ACCESS_PUBLIC;
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
	public static TextureAtlasSprite ICON_TUTORIAL;

	public static TextureAtlasSprite ICON_RS_TORCH_OFF;
	public static TextureAtlasSprite ICON_RS_TORCH_ON;

	public static TextureAtlasSprite ICON_ARROW_DOWN;
	public static TextureAtlasSprite ICON_ARROW_DOWN_INAVTIVE;

	public static TextureAtlasSprite ICON_ARROW_UP;
	public static TextureAtlasSprite ICON_ARROW_UP_INACTIVE;

	public static TextureAtlasSprite ICON_GUNPOWDER;
	public static TextureAtlasSprite ICON_REDSTONE;





}
