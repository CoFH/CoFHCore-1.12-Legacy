package cofh.util;

import cofh.core.CoFHProps;
import cpw.mods.fml.common.FMLCommonHandler;
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
			player.addChatMessage(new ChatComponentText(StringHelper.YELLOW + "[CoFH] " + StringHelper.WHITE
					+ StringHelper.localize("message.cofh.secureWarning") + StringHelper.END));
		}
	}

}
