package cofh.core.command;

import cofh.CoFHCore;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.StringHelper;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;

public class CommandKillAll implements ISubCommand {

	public static CommandKillAll instance = new CommandKillAll();

	/* ISubCommand */
	@Override
	public String getCommandName() {

		return "killall";
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleCommand(ICommandSender sender, String[] arguments) {

		if (!CoreUtils.isOpOrServer(sender.getCommandSenderName())) {
			sender.addChatMessage(new ChatComponentText(CommandHandler.COMMAND_DISALLOWED));
			return;
		}
		int killCount = 0;
		String curName;
		TObjectIntHashMap<String> names = new TObjectIntHashMap<String>();
		String target = null;
		boolean all = false;
		if (arguments.length > 1) {
			target = arguments[1].toLowerCase();
			all = "*".equals(target);
		}
		for (WorldServer theWorld : CoFHCore.server.worldServers) {
			synchronized (theWorld) {
				List<Entity> list = theWorld.loadedEntityList;
				for (int i = list.size(); i --> 0; ) {
					Entity entity = list.get(i);
					if (entity != null && !(entity instanceof EntityPlayer)) {
						curName = EntityList.getEntityString(entity);
						if (target != null | all) {
							if (all || curName != null && curName.toLowerCase().contains(target)) {
								names.adjustOrPutValue(curName, 1, 1);
								killCount++;
								theWorld.removeEntity(entity);
							}
						} else if (entity instanceof EntityMob) {
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
				finalNames = finalNames + StringHelper.LIGHT_RED + it.value() + StringHelper.WHITE + "x" + StringHelper.YELLOW + it.key()
						+ StringHelper.WHITE + ", ";
			}
			finalNames = finalNames.substring(0, finalNames.length() - 2);
			sender.addChatMessage(new ChatComponentText("Removed " + killCount + (arguments.length > 1 ? "" : " hostile") + " entities. (" + finalNames + ")"));
		} else {
			sender.addChatMessage(new ChatComponentText(arguments.length > 1 ? "No entities found matching \"" + StringHelper.YELLOW + arguments[1]
					+ StringHelper.WHITE + "\"!" : "No hostile mobs found!"));
		}
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {

		return null;
	}

}
