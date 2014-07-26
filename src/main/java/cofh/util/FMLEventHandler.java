package cofh.util;

import cofh.core.CoFHProps;
import cofh.render.ItemRenderRegistry;
import cofh.util.fluid.BucketHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLModIdMappingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

public class FMLEventHandler {

	public static FMLEventHandler instance = new FMLEventHandler();

	public static void initialize() {

		FMLCommonHandler.instance().bus().register(instance);
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {

		EntityPlayer player = event.player;
		if (ServerHelper.isMultiPlayerServer() && CoFHProps.enableOpSecureAccess && CoFHProps.enableOpSecureAccessWarning) {
			player.addChatMessage(new ChatComponentText(StringHelper.YELLOW + "[CoFH] " + StringHelper.WHITE + StringHelper.localize("chat.cofh.secure.0")
					+ StringHelper.END));
		}
		handleIdMappingEvent(null);
	}

	@EventHandler
	public void handleIdMappingEvent(FMLModIdMappingEvent event) {

		BucketHandler.refreshMap();
		ItemRenderRegistry.refreshMap();
	}

}
