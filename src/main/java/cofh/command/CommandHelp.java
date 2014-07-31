package cofh.command;

import cofh.util.StringHelper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CommandHelp implements ISubCommand {

	public static CommandHelp instance = new CommandHelp();

	/* ISubCommand */
	@Override
	public String getCommandName() {

		return "help";
	}

	@Override
	public void handleCommand(ICommandSender sender, String[] arguments) {

		if (arguments.length == 0) {
			return;
		}
		if (arguments.length == 1) {

			StringBuilder output = new StringBuilder(StringHelper.localize("info.cofh.command.help.0") + " ");
			List<String> commandList = new ArrayList<String>(CommandHandler.getCommandList());

			for (int i = 0; i < commandList.size() - 1; i++) {
				output.append(StringHelper.YELLOW + "/cofh " + commandList.get(i) + StringHelper.WHITE + ", ");
			}
			output.delete(output.length() - 2, output.length());
			output.append(" and /cofh " + commandList.get(commandList.size() - 1) + ".");
			sender.addChatMessage(new ChatComponentText(output.toString()));
		} else {
			String commandName = arguments[1];
			sender.addChatMessage(new ChatComponentText(StringHelper.localize("info.cofh.command." + commandName)));
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
