package cofh.mod.updater;

import static net.minecraft.util.EnumChatFormatting.*;

import cofh.core.CoFHProps;
import com.google.common.base.Strings;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

public class UpdateManager {

	private static transient int pollOffset = 0;
	private static final ChatStyle description = new ChatStyle();
	private static final ChatStyle version = new ChatStyle();
	private static final ChatStyle modname = new ChatStyle();
	private static final ChatStyle download = new ChatStyle();
	private static final ChatStyle white = new ChatStyle();
	static {

		description.setColor(GRAY);
		version.setColor(AQUA);
		modname.setColor(GOLD);
		download.setColor(GREEN);
		white.setColor(WHITE);

		{
			ChatStyle tooltip = new ChatStyle();
			tooltip.setColor(YELLOW);
			IChatComponent msg = new ChatComponentTranslation("info.cofh.updater.tooltip").setChatStyle(tooltip);
			download.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, msg));
		}
	}

	public static void registerUpdater(UpdateManager manager) {

		FMLCommonHandler.instance().bus().register(manager);
	}

	private boolean _notificationDisplayed;
	private final IUpdatableMod _mod;
	private final UpdateCheckThread _updateThread;
	private final String _downloadUrl;
	private int lastPoll = 400;

	public UpdateManager(IUpdatableMod mod) {

		this(mod, null);
	}

	public UpdateManager(IUpdatableMod mod, String releaseUrl) {

		this(mod, releaseUrl, null);
	}

	public UpdateManager(IUpdatableMod mod, String releaseUrl, String downloadUrl) {

		_mod = mod;
		_updateThread = new UpdateCheckThread(mod, releaseUrl, downloadUrl);
		_updateThread.start();
		_downloadUrl = downloadUrl;
		lastPoll += (pollOffset += 140);
	}

	@SubscribeEvent
	public void tickStart(PlayerTickEvent evt) {

		if (evt.phase != Phase.START) {
			return;
		}
		if (MinecraftServer.getServer() != null && MinecraftServer.getServer().isServerRunning()) {
			if (!MinecraftServer.getServer().getConfigurationManager().func_152596_g(evt.player.getGameProfile())) {
				return;
			}
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
				ModVersion newVersion = _updateThread.newVersion();

				EntityPlayer player = evt.player;
				IChatComponent chat = new ChatComponentText("");
				{
					ChatStyle data = modname.createShallowCopy();
					IChatComponent msg = new ChatComponentText(newVersion.modVersion().toString()).setChatStyle(version);
					data.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, msg));
					chat.appendSibling(new ChatComponentText("[" + _mod.getModName() + "] ").setChatStyle(data));
				}
				chat.appendSibling(new ChatComponentTranslation("info.cofh.updater.version").setChatStyle(white));
				chat.appendText(GOLD + ":");
				player.addChatMessage(chat);
				chat = new ChatComponentText("");
				if (!Strings.isNullOrEmpty(_downloadUrl)) {
					chat.appendText(WHITE + "[");
					ChatStyle data = download.createShallowCopy();
					data.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, _downloadUrl));
					chat.appendSibling(new ChatComponentTranslation("info.cofh.updater.download").setChatStyle(data));
					chat.appendText(WHITE + "] ");
				}
				chat.appendSibling(new ChatComponentText(newVersion.description()).setChatStyle(description));
				player.addChatMessage(chat);
			}
		}
	}

}
