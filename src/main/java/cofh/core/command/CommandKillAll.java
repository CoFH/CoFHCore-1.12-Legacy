package cofh.core.command;

import cofh.CoFHCore;
import cofh.lib.util.helpers.StringHelper;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.List;
import java.util.Locale;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

public class CommandKillAll implements ISubCommand {

	public static CommandKillAll instance = new CommandKillAll();

	/* ISubCommand */
	@Override
	public String getCommandName() {

		return "killall";
	}

	@Override
	public int getPermissionLevel() {

		return 2;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleCommand(ICommandSender sender, String[] arguments) {

		int killCount = 0;
		String curName;
		TObjectIntHashMap<String> names = new TObjectIntHashMap<String>();
		String target = null;
		boolean all = false;
		if (arguments.length > 1) {
			target = arguments[1].toLowerCase(Locale.US);
			all = "*".equals(target);
		}
		for (WorldServer theWorld : CoFHCore.server.worldServers) {
			synchronized (theWorld) {
				List<Entity> list = theWorld.loadedEntityList;
				for (int i = list.size(); i-- > 0;) {
					Entity entity = list.get(i);
					if (entity != null && !(entity instanceof EntityPlayer)) {
						curName = EntityList.getEntityString(entity);
						if (target != null | all) {
							if (all || curName != null && curName.toLowerCase(Locale.US).contains(target)) {
								names.adjustOrPutValue(curName, 1, 1);
								killCount++;
								theWorld.removeEntity(entity);
							}
						} else if (entity instanceof IMob) {
							if (curName == null) {
								curName = entity.getClass().getName();
							}
							names.adjustOrPutValue(curName, 1, 1);
							killCount++;
							theWorld.removeEntity(entity);
						}
					}
				}
			}
		}
		if (killCount > 0) {
			String finalNames = "";
			TObjectIntIterator<String> it = names.iterator();
			while (it.hasNext()) {
				it.advance();
				finalNames = finalNames + StringHelper.LIGHT_RED + it.value() + StringHelper.WHITE + "x" + StringHelper.YELLOW + it.key() + StringHelper.WHITE
						+ ", ";
			}
			finalNames = finalNames.substring(0, finalNames.length() - 2);
			CommandHandler.logAdminCommand(sender, this, "info.cofh.command.killall.success" + (target != null ? "" : "Hostile"), killCount, finalNames);
		} else {
			sender.addChatMessage(new TextComponentTranslation("info.cofh.command.killall.no" + (target != null ? "Match" : "Hostile")));
		}
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {

		return null;
	}

}
