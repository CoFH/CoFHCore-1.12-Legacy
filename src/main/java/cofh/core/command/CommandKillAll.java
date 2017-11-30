package cofh.core.command;

import cofh.CoFHCore;
import cofh.core.init.CoreProps;
import cofh.core.util.helpers.StringHelper;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

import java.util.List;
import java.util.Locale;

public class CommandKillAll implements ISubCommand {

	public static final CommandKillAll INSTANCE = new CommandKillAll();

	public static int permissionLevel = 2;

	public static void config() {

		String category = "Command." + INSTANCE.getCommandName();
		String comment = "Adjust this value to change the default permission level for the " + INSTANCE.getCommandName() + " command.";
		permissionLevel = CoFHCore.CONFIG_CORE.getConfiguration().getInt("PermissionLevel", category, permissionLevel, -1, 4, comment);
	}

	/* ISubCommand */
	@Override
	public String getCommandName() {

		return "killall";
	}

	@Override
	public int getPermissionLevel() {

		return permissionLevel;
	}

	@SuppressWarnings ("unchecked")
	@Override
	public void handleCommand(MinecraftServer server, ICommandSender sender, String[] arguments) throws CommandException {

		int killCount = 0;
		String curName;
		TObjectIntHashMap<String> names = new TObjectIntHashMap<>();
		String target = null;
		boolean all = false;
		if (arguments.length > 1) {
			target = arguments[1].toLowerCase(Locale.US);
			all = "*".equals(target);
		}
		if ("item".equals(target) || "items".equals(target)) {
			for (WorldServer world : CoreProps.server.worlds) {
				synchronized (world) {
					List<Entity> list = world.loadedEntityList;
					for (int i = list.size(); i-- > 0; ) {
						Entity entity = list.get(i);
						if (entity != null && (entity instanceof EntityItem)) {
							curName = EntityList.getEntityString(entity);
							if (curName != null && curName.toLowerCase(Locale.US).contains(target)) {
								names.adjustOrPutValue(curName, 1, 1);
								killCount++;
								world.removeEntity(entity);
							}
						}
					}
				}
			}
		} else {
			for (WorldServer world : CoreProps.server.worlds) {
				synchronized (world) {
					List<Entity> list = world.loadedEntityList;
					for (int i = list.size(); i-- > 0; ) {
						Entity entity = list.get(i);
						if (entity != null && !(entity instanceof EntityPlayer)) {
							curName = EntityList.getEntityString(entity);
							if (target != null | all) {
								if (all || curName != null && curName.toLowerCase(Locale.US).contains(target)) {
									names.adjustOrPutValue(curName, 1, 1);
									killCount++;
									world.removeEntity(entity);
								}
							} else if (entity instanceof IMob) {
								if (curName == null) {
									curName = entity.getClass().getName();
								}
								names.adjustOrPutValue(curName, 1, 1);
								killCount++;
								world.removeEntity(entity);
							}
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
				finalNames = finalNames + StringHelper.LIGHT_RED + it.value() + StringHelper.WHITE + "x" + StringHelper.YELLOW + it.key() + StringHelper.WHITE + ", ";
			}
			finalNames = finalNames.substring(0, finalNames.length() - 2);
			CommandHandler.logAdminCommand(sender, this, "chat.cofh.command.killall.success" + (target != null ? "" : "Hostile"), killCount, finalNames);
		} else {
			sender.sendMessage(new TextComponentTranslation("chat.cofh.command.killall.no" + (target != null ? "Match" : "Hostile")));
		}
	}

	@Override
	public List<String> addTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args) {

		return null;
	}

}
