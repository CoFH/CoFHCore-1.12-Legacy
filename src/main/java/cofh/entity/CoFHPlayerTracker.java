package cofh.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import cofh.core.CoFHProps;
import cofh.util.ServerHelper;
import cofh.util.StringHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class CoFHPlayerTracker {

	public static CoFHPlayerTracker instance = new CoFHPlayerTracker();

	public static void initialize() {

		FMLCommonHandler.instance().bus().register(instance);
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent evt) {

		EntityPlayer player = evt.player;
		if (ServerHelper.isMultiPlayerServer()
				&& CoFHProps.enableOpSecureAccess
				&& CoFHProps.enableOpSecureAccessWarning) {
			player.addChatMessage(new ChatComponentText(StringHelper.YELLOW
					+ "[CoFH] " + StringHelper.WHITE
					+ StringHelper.localize("message.cofh.secureWarning")
					+ StringHelper.END));
		}
	}

}
