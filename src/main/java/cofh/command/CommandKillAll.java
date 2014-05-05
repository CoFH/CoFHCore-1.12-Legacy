package cofh.command;

import gnu.trove.map.hash.THashMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;
import cofh.CoFHCore;
import cofh.util.CoreUtils;
import cofh.util.StringHelper;

public class CommandKillAll implements ISubCommand {

	public static CommandKillAll instance = new CommandKillAll();

	/* ISubCommand */
	@Override
	public String getCommandName() {

		return "killall";
	}

	@Override
	public void handleCommand(ICommandSender sender, String[] arguments) {

		if (CoreUtils.isOpOrServer(sender.getCommandSenderName())) {
			int killCount = 0;
			String curName;
			THashMap<String, Integer> names = new THashMap<String, Integer>();
			for (WorldServer theWorld : CoFHCore.server.worldServers) {
				for (int i = 0; i < theWorld.loadedEntityList.size(); i++) {
					if (theWorld.loadedEntityList.get(i) != null) {
						curName = EntityList.getEntityString((Entity) theWorld.loadedEntityList.get(i));
						if (arguments.length > 1) {
							if (curName != null && curName.toLowerCase().contains(arguments[1].toLowerCase())) {
								if (names.containsKey(curName)) {
									names.put(curName, names.get(curName) + 1);
								} else {
									names.put(curName, 1);
								}
								killCount++;
								theWorld.removeEntity((Entity) theWorld.loadedEntityList.get(i));
							}
						} else if (theWorld.loadedEntityList.get(i) instanceof EntityMob) {
							if (curName == null) {
								curName = theWorld.loadedEntityList.get(i).toString();
							}
							if (names.containsKey(curName)) {
								names.put(curName, names.get(curName) + 1);
							} else {
								names.put(curName, 1);
							}
							killCount++;
							theWorld.removeEntity((Entity) theWorld.loadedEntityList.get(i));
						}
					}
				}
			}
			if (killCount > 0) {
				String finalNames = "";
				Iterator<Map.Entry<String, Integer>> it = names.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, Integer> pairs = it.next();
					finalNames = finalNames + StringHelper.LIGHT_RED + pairs.getValue() + StringHelper.WHITE + "x" + StringHelper.YELLOW + pairs.getKey()
							+ StringHelper.WHITE + ", ";
				}
				finalNames = finalNames.substring(0, finalNames.length() - 2);
				sender.addChatMessage(new ChatComponentText("Removed " + killCount + (arguments.length > 1 ? "" : " hostile") + " entities. (" + finalNames
						+ ")"));
			} else {
				sender.addChatMessage(new ChatComponentText(arguments.length > 1 ? "No entities found matching \"" + StringHelper.YELLOW + arguments[1]
						+ StringHelper.WHITE + "\"!" : "No hostile mobs found!"));
			}
		} else {
			sender.addChatMessage(new ChatComponentText(CommandHandler.COMMAND_DISALLOWED));
		}
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {

		return null;
	}

}
