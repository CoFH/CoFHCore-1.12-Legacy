package cofh.core.util;

import cofh.core.energy.FurnaceFuelHandler;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketCore;
import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLModIdMappingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class FMLEventHandler {

	public static FMLEventHandler instance = new FMLEventHandler();

	public static void initialize() {

		MinecraftForge.EVENT_BUS.register(instance);
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {

		EntityPlayer player = event.player;
		if (ServerHelper.isMultiPlayerServer() && CoreProps.enableOpSecureAccess && CoreProps.enableOpSecureAccessWarning) {
			player.addChatMessage(new TextComponentString(StringHelper.YELLOW + "[CoFH] ").appendSibling(new TextComponentTranslation("chat.cofh.secure.notice")));
		}
		PacketCore.sendConfigSyncPacketToClient(event.player);
		handleIdMappingEvent(null);
	}

	@EventHandler
	public void handleIdMappingEvent(FMLModIdMappingEvent event) {

		FurnaceFuelHandler.refresh();
		OreDictionaryArbiter.initialize();
	}

}
