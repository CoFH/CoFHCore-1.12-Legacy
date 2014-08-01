package cofh.updater;

import static net.minecraft.util.EnumChatFormatting.AQUA;
import static net.minecraft.util.EnumChatFormatting.GOLD;
import static net.minecraft.util.EnumChatFormatting.GRAY;
import static net.minecraft.util.EnumChatFormatting.WHITE;

import cofh.core.CoFHProps;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

public class UpdateManager {

	private static transient int pollOffset = 0;
	public static void registerUpdater(UpdateManager manager) {

		FMLCommonHandler.instance().bus().register(manager);
	}

	private boolean _notificationDisplayed;
	private final IUpdatableMod _mod;
	private final UpdateCheckThread _updateThread;
	private int lastPoll = 400;

	public UpdateManager(IUpdatableMod mod) {

		this(mod, null);
	}

	public UpdateManager(IUpdatableMod mod, String releaseUrl) {

		_mod = mod;
		_updateThread = new UpdateCheckThread(mod, releaseUrl);
		_updateThread.start();
		lastPoll += (pollOffset += 140);
	}

	@SubscribeEvent
	public void tickStart(PlayerTickEvent evt) {

		if (evt.phase != Phase.START) {
			return;
		}
		if (lastPoll > 0) {
			--lastPoll;
			return;
		}
		lastPoll = 400;

		if (!_notificationDisplayed && _updateThread.checkComplete()) {
			_notificationDisplayed = true;
			FMLCommonHandler.instance().bus().unregister(this);
			if (_updateThread.newVersionAvailable()) {
				if (!CoFHProps.enableUpdateNotice && !_updateThread.isCriticalUpdate()) {
					return;
				}
				ModVersion version = _updateThread.newVersion();

				EntityPlayer player = evt.player;
				player.addChatMessage(new ChatComponentText(GOLD + "[" + _mod.getModName() + "]").appendText(WHITE + " A new version is available: ")
						.appendText(AQUA + version.modVersion().toString()));
				player.addChatMessage(new ChatComponentText(GRAY + version.description()));
			}
		}
	}

}
