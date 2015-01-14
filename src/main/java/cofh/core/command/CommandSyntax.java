package cofh.core.command;

import cofh.lib.util.helpers.StringHelper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

public class CommandSyntax implements ISubCommand {

	public static CommandSyntax instance = new CommandSyntax();

	/* ISubCommand */
	@Override
	public String getCommandName() {

		return "syntax";
	}

	@Override
	public int getPermissionLevel() {

		return -1;
	}

	@Override
	public void handleCommand(ICommandSender sender, String[] arguments) {

		switch (arguments.length) {
		case 1:
			StringBuilder output = new StringBuilder(StringHelper.localize("info.cofh.command.help.0") + " ");
			List<String> commandList = new ArrayList<String>(CommandHandler.getCommandList());
			// TODO: check permission level too

			for (int i = 0; i < commandList.size() - 1; i++) {
				output.append("/cofh " + StringHelper.YELLOW + commandList.get(i) + StringHelper.WHITE + ", ");
			}
			output.delete(output.length() - 2, output.length());
			output.append(" /cofh " + StringHelper.YELLOW + commandList.get(commandList.size() - 1) + StringHelper.WHITE + ".");
			sender.addChatMessage(new ChatComponentText(output.toString()));
			break;
		case 2:
			String commandName = arguments[1];
			if (!CommandHandler.getCommandExists(commandName)) {
				throw new CommandNotFoundException("info.cofh.command.notFound");
			}
			sender.addChatMessage(new ChatComponentText(StringHelper.localize("info.cofh.command." + commandName + ".syntax")));
			break;
		default:
			throw new WrongUsageException("info.cofh.command." + getCommandName() + ".syntax");
		}
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {

		if (args.length == 2) {
			return CommandBase.getListOfStringsFromIterableMatchingLastWord(args, CommandHandler.getCommandList());
		}
		return null;

	}

}
