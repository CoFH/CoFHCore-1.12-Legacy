package cofh.core.command;

import cofh.lib.util.helpers.StringHelper;
import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandHelp implements ISubCommand {

	public static CommandHelp instance = new CommandHelp();

	/* ISubCommand */
	@Override
	public String getCommandName() {

		return "help";
	}

	@Override
	public int getPermissionLevel() {

		return -1;
	}

	@Override
	public void handleCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

		List<String> commandList = new ArrayList<String>(CommandHandler.getCommandList());
		Collections.sort(commandList, String.CASE_INSENSITIVE_ORDER);
		commandList.remove(getCommandName());
		for (int i = 0; i < commandList.size(); ++i) {
			String name = commandList.get(i);
			if (!CommandHandler.canUseCommand(sender, CommandHandler.getCommandPermission(name), name)) {
				commandList.remove(i--);
			}
		}
		final int pageSize = 7;
		int maxPages = (commandList.size() - 1) / pageSize;
		int page;

		try {
			page = args.length == 1 ? 0 : CommandBase.parseInt(args[1], 1, maxPages + 1) - 1;
		} catch (NumberInvalidException numberinvalidexception) {
			String commandName = args[1];
			if (!CommandHandler.getCommandExists(commandName)) {
				throw new CommandNotFoundException("info.cofh.command.notFound");
			}
			sender.addChatMessage(new TextComponentTranslation("info.cofh.command." + commandName));
			return;
		}

		int maxIndex = Math.min((page + 1) * pageSize, commandList.size());
		ITextComponent chatcomponenttranslation1 = new TextComponentTranslation("commands.help.header", page + 1, maxPages + 1);
		chatcomponenttranslation1.getStyle().setColor(TextFormatting.DARK_GREEN);
		sender.addChatMessage(chatcomponenttranslation1);

		for (int i = page * pageSize; i < maxIndex; ++i) {
			ITextComponent chatcomponenttranslation = new TextComponentString("/cofh " + StringHelper.YELLOW + commandList.get(i));
			chatcomponenttranslation.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/cofh " + commandList.get(i)));
			sender.addChatMessage(chatcomponenttranslation);
		}
	}

	@Override
	public List<String> addTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args) {

		if (args.length == 2) {
			return CommandBase.getListOfStringsMatchingLastWord(args, CommandHandler.getCommandList());
		}
		return null;

	}

}
