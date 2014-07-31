package cofh.command;

import java.util.List;

import cofh.CoFHCore;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CommandVersion implements ISubCommand {

	public static CommandVersion instance = new CommandVersion();

	/* ISubCommand */
	@Override
	public String getCommandName() {

		return "version";
	}

	@Override
	public void handleCommand(ICommandSender sender, String[] arguments) {

		sender.addChatMessage(new ChatComponentText("This mod is on Version " + CoFHCore.version + "."));
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {

		return null;
	}

}
