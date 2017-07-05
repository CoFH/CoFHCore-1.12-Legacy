package cofh.core.command;

import cofh.core.util.helpers.StringHelper;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CommandHandler extends CommandBase {

	public static final String COMMAND_DISALLOWED = StringHelper.LIGHT_RED + "You are not allowed to use this command.";

	public static final CommandHandler INSTANCE = new CommandHandler();

	private static TMap<String, ISubCommand> commands = new THashMap<>();

	static {
		registerSubCommand(CommandHelp.INSTANCE);
		registerSubCommand(CommandSyntax.INSTANCE);
		registerSubCommand(CommandVersion.INSTANCE);
		registerSubCommand(CommandKillAll.INSTANCE);
		registerSubCommand(CommandTPS.INSTANCE);
		registerSubCommand(CommandTPX.INSTANCE);
		registerSubCommand(CommandEnchant.INSTANCE);
		registerSubCommand(CommandClearBlock.INSTANCE);
		registerSubCommand(CommandReplaceBlock.INSTANCE);
		registerSubCommand(CommandUnloadChunk.INSTANCE);
		registerSubCommand(CommandCountBlock.INSTANCE);
		registerSubCommand(CommandHand.INSTANCE);
		registerSubCommand(CommandFriend.INSTANCE);
	}

	public static void initCommands(FMLServerStartingEvent event) {

		event.registerServerCommand(INSTANCE);
	}

	public static boolean registerSubCommand(ISubCommand subCommand) {

		if (!commands.containsKey(subCommand.getCommandName())) {
			commands.put(subCommand.getCommandName(), subCommand);
			return true;
		}
		return false;
	}

	public static Set<String> getCommandList() {

		return commands.keySet();
	}

	public static int getCommandPermission(String command) {

		return getCommandExists(command) ? commands.get(command).getPermissionLevel() : Integer.MAX_VALUE;
	}

	public static boolean getCommandExists(String command) {

		return commands.containsKey(command);
	}

	public static boolean canUseCommand(ICommandSender sender, int permission, String name) {

		if (getCommandExists(name)) {
			return sender.canUseCommand(permission, "cofh " + name) ||
					// this check below is because mojang is incompetent, as always
					(sender instanceof EntityPlayerMP && permission <= 0);
		}
		return false;
	}

	@Override
	public int getRequiredPermissionLevel() {

		return -1;
	}

	private static DummyCommand dummy = new DummyCommand();

	public static void logAdminCommand(ICommandSender sender, ISubCommand command, String info, Object... data) {

		dummy.setFromCommand(command);
		for (int i = 0, e = data.length; i < e; ++i) {
			Object entry = data[i];
			if (entry instanceof Number) {
				Number d = (Number) entry;
				int a = d.intValue();
				float f = d.floatValue();
				if (a != f) {
					data[i] = String.format("%.2f", f);
				}
			}
		}
		CommandBase.notifyCommandListener(sender, dummy, info, data);
	}

	@Override
	public String getName() {

		return "cofh";
	}

	@Override
	public List<String> getAliases() {

		return new ArrayList<>();
	}

	@Override
	public String getUsage(ICommandSender sender) {

		return "/" + getName() + " help";
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {

		return true;
	}

	@Override
	public void execute(MinecraftServer minecraftServer, ICommandSender sender, String[] arguments) throws CommandException {

		if (arguments.length < 1) {
			arguments = new String[] { "help" };
		}
		ISubCommand command = commands.get(arguments[0]);
		if (command != null) {
			if (canUseCommand(sender, command.getPermissionLevel(), command.getCommandName())) {
				command.handleCommand(minecraftServer, sender, arguments);
				return;
			}
			throw new CommandException("commands.generic.permission");
		}
		throw new CommandNotFoundException("chat.cofh.command.notFound");
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {

		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, commands.keySet());
		} else if (commands.containsKey(args[0])) {
			return commands.get(args[0]).addTabCompletionOptions(server, sender, args);
		}
		return null;
	}

	private static class DummyCommand extends CommandBase {

		private int perm = 4;
		private String name = "";

		public void setFromCommand(ISubCommand command) {

			name = command.getCommandName();
			perm = command.getPermissionLevel();
		}

		@Override
		public String getName() {

			return "cofh " + name;
		}

		@Override
		public int getRequiredPermissionLevel() {

			return perm;
		}

		@Override
		public String getUsage(ICommandSender sender) {

			return "";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

		}

	}
}
