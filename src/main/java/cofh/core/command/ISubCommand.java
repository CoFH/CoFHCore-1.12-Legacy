package cofh.core.command;

import java.util.List;

import net.minecraft.command.ICommandSender;

public interface ISubCommand {

	public String getCommandName();

	public void handleCommand(ICommandSender sender, String[] arguments);

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args);

}
